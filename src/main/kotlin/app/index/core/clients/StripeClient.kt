package app.index.core.clients

import app.index.config.StripeConfig
import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.pro.ProSubscriptionCancellationRequestData
import app.index.data.models.user.UserData
import com.stripe.Stripe
import com.stripe.exception.StripeException
import com.stripe.model.Customer
import com.stripe.model.PromotionCode
import com.stripe.model.Subscription
import com.stripe.net.RequestOptions
import com.stripe.param.CustomerCreateParams
import com.stripe.param.CustomerRetrieveParams
import com.stripe.param.PromotionCodeListParams
import com.stripe.param.SubscriptionCreateParams
import com.stripe.param.SubscriptionUpdateParams
import com.stripe.param.SubscriptionUpdateParams.CancellationDetails
import org.koin.core.annotation.Single


@Single(createdAtStart = true)
class StripeClient {
    object MetadataKeys {
        const val INDEX_USER_ID = "index_user_id"
    }

    init {
        Stripe.apiKey = StripeConfig.apiKey
    }

    /**
     * @return true if the promotion code is valid, false otherwise
     */
    fun isPromotionCodeValid(promotionCode: String): Boolean {
        return try {
            val params = PromotionCodeListParams.builder()
                .setCode(promotionCode)
                .setLimit(1L)
                .build()

            val coupons = PromotionCode.list(params).data

            return coupons.isNotEmpty()
        } catch (e: StripeException) {
            if (e.code == "resource_missing") {
                false
            } else {
                throw e
            }
        }
    }

    /**
     * @return the promotion code matching the customer facing [code], null if no promotion code matched
     */
    fun getPromotionCode(code: String): PromotionCode? {
        return try {
            val params = PromotionCodeListParams.builder()
                .setCode(code)
                .setLimit(1L)
                .build()

            val promotionCode = PromotionCode.list(params).data.firstOrNull()

            return promotionCode
        } catch (e: StripeException) {
            if (e.code == "resource_missing") {
                null
            } else {
                throw e
            }
        }
    }

    /**
     * @param customerId
     *
     * @return true if the customer has an active subscription, false otherwise
     */
    fun hasActiveSubscription(customerId: String): Boolean {
        return try {
            val params = CustomerRetrieveParams.builder()
                .addExpand("subscriptions")
                .build()

            val customer = Customer.retrieve(customerId, params, RequestOptions.getDefault())

            if (customer.subscriptions == null || customer.subscriptions.data == null) {
                return false
            }

            customer.subscriptions.data.any { it.status == "active" }
        } catch (e: StripeException) {
            if (e.code == "resource_missing") {
                false
            } else {
                throw e
            }
        }
    }

    /**
     * @param customerId
     *
     * @return null if the customer doesn't have a subscription, a pair with the subscription id and price id otherwise
     */
    fun getActiveSubscription(customerId: String): Pair<String, String>? {
        return try {
            val params = CustomerRetrieveParams.builder()
                .addExpand("subscriptions")
                .build()

            val customer = Customer.retrieve(customerId, params, RequestOptions.getDefault())

            val activeSub = customer.subscriptions.data.firstOrNull { it.status == "active" }
            if (activeSub == null) {
                null
            } else {
                val priceId = activeSub.items.data.getOrNull(0)?.price?.id
                if (priceId == null) {
                    null
                } else {
                    activeSub.id to priceId
                }
            }
        } catch (e: StripeException) {
            if (e.code == "resource_missing") {
                null
            } else {
                throw e
            }
        }
    }

    /**
     * Creates a subscription for the customer with the specified [customerId]
     *
     * @param customerId
     * @param priceId
     *
     * @throws StripeException
     *
     * @return the client_secret for the subscription payment intent, null if the invoice amount is $0 and the subscription has already been created
     */
    fun createSubscription(
        customerId: String,
        priceId: String,
        promotionCode: String?
    ): String? {
        val paymentSettings = SubscriptionCreateParams.PaymentSettings.builder()
            .setSaveDefaultPaymentMethod(SubscriptionCreateParams.PaymentSettings.SaveDefaultPaymentMethod.ON_SUBSCRIPTION)
            .build()

        val subCreateParams = SubscriptionCreateParams
            .builder()
            .setCustomer(customerId)
            .addItem(
                SubscriptionCreateParams
                    .Item.builder()
                    .setPrice(priceId)
                    .build()
            )
            .setPaymentSettings(paymentSettings)
            .setPaymentBehavior(SubscriptionCreateParams.PaymentBehavior.DEFAULT_INCOMPLETE)
            .addAllExpand(listOf("latest_invoice.payment_intent"))
            .apply {
                if (promotionCode != null) {
                    val promotionCodeId = getPromotionCode(promotionCode)?.id

                    val discount = SubscriptionCreateParams.Discount.builder()
                        .setPromotionCode(promotionCodeId)
                        .build()

                    setDiscounts(listOf(discount))
                }
            }
            .build()

        val subscription = Subscription.create(subCreateParams)
        val paymentIntent = subscription.latestInvoiceObject.paymentIntentObject

        if (paymentIntent == null) {
            return null
        } else {
            return paymentIntent.clientSecret
        }
    }

    /**
     * Cancels the subscription that matches the given [subscriptionId]
     *
     * @throws StripeException
     *
     * @return true if the subscription was canceled, false if no subscription matched the [subscriptionId]
     */
    fun cancelSubscription(subscriptionId: String, cancellationInfo: ProSubscriptionCancellationRequestData): Boolean {
        return try {
            val sub = Subscription.retrieve(subscriptionId)

            val params = SubscriptionUpdateParams.builder()
                .setCancelAtPeriodEnd(true)
                .setCancellationDetails(
                    CancellationDetails.builder()
                        .apply {
                            if (cancellationInfo.comment != null)
                                setComment(cancellationInfo.comment)

                            if (cancellationInfo.feedback != null)
                                setFeedback(cancellationInfo.feedback.asStripeFeedback())
                        }
                        .build()
                )
                .build()

            sub.update(params)
            true
        } catch (e: StripeException) {
            if (e.code == "resource_missing") {
                false
            } else {
                throw e
            }
        }
    }

    /**
     * Gets a single [Customer] from Stripe by its id (aka the index [userId])
     *
     * @throws StripeException
     *
     * @return a pair with a boolean indicating if the customer has been created and the [Customer] itself
     */
    fun getCustomerOrCreateIfMissing(customerId: String?, userId: IxId<UserData>, email: String): Pair<Boolean, Customer> {
        return if (customerId == null) {
            true to createCustomer(userId, email)
        } else {
            try {
                val customer = Customer.retrieve(customerId)

                try {
                    if (customer.deleted) {
                        true to createCustomer(userId, email)
                    } else {
                        false to customer
                    }
                } catch (e: NullPointerException) {
                    false to customer
                }
            } catch (e: StripeException) {
                if (e.code == "resource_missing") {
                    true to createCustomer(userId, email)
                } else {
                    throw e
                }
            }
        }
    }

    /**
     * Gets a single [Customer] from Stripe by its id
     *
     * @param customerId the Stripe customer id, this is **different from the index user id!**
     *
     * @throws StripeException
     *
     * @return the [Customer] or null if a customer with the provided id doesn't exist
     */
    @Suppress("UNUSED")
    private fun getCustomer(customerId: String): Customer? {
        return try {
            Customer.retrieve(customerId)
        } catch (e: StripeException) {
            if (e.code == "resource_missing") {
                null
            } else {
                throw e
            }
        }
    }

    /**
     * Creates a Stripe customer.
     *
     * Always check if the customer exists before trying to create it,
     * as the [userId] is not used for the Stripe customer id, it's just going to be put in the Stripe customer metadata
     *
     * @param userId
     * @param email
     *
     * @throws StripeException
     *
     * @return the created [Customer]
     */
    private fun createCustomer(userId: IxId<UserData>, email: String): Customer {
        val params = CustomerCreateParams.builder()
            .setEmail(email)
            .setMetadata(mapOf(MetadataKeys.INDEX_USER_ID to userId.toString()))
            .build()

        return Customer.create(params)
    }
}