package app.index.core.logic.pro

import app.index.core.clients.StripeClient
import app.index.core.logic.typedId.impl.IxId
import app.index.data.daos.user.UserDao
import app.index.data.models.pro.ProSubscriptionCancellationRequestData
import app.index.data.models.user.UserData
import com.stripe.exception.StripeException
import org.koin.core.annotation.Single

@Single(createdAtStart = true)
class ProManager(
    private val userDao: UserDao,
    private val stripeClient: StripeClient
) {

    /**
     * @return true if the promotion code is valid, false otherwise
     */
    fun isPromotionCodeValid(promotionCode: String): Boolean {
        return stripeClient.isPromotionCodeValid(promotionCode)
    }

    /**
     * @param priceId
     * @param proFeature
     *
     * @return true if the user has access to the [proFeature], false otherwise
     */
    fun hasAccessToProFeature(
        priceId: String?,
        proFeature: ProFeature
    ): Boolean {
        // atm there is only one plan and no specific logic is needed
        return priceId != null
    }

    /**
     * @param customerId
     *
     * @return true if the customer has an active subscription, false otherwise
     */
    fun hasActiveSubscription(customerId: String): Boolean {
        return stripeClient.hasActiveSubscription(customerId)
    }

    /**
     * @param customerId
     *
     * @return null if the customer doesn't have a subscription, a pair with the subscription id and price id otherwise
     */
    fun getActiveSubscription(customerId: String): Pair<String, String>? {
        return stripeClient.getActiveSubscription(customerId)
    }

    /**
     * Creates a subscription for the customer with the specified [customerId]
     *
     * This also creates the customer if missing
     *
     * @param customerId
     * @param userId
     * @param email
     * @param priceId
     *
     * @throws StripeException
     *
     * @return the client_secret for the subscription payment intent, or null if the charge amount is $0 and the subscription has successfully been created
     */
    suspend fun createSubscription(
        customerId: String?,
        userId: IxId<UserData>,
        email: String,
        priceId: String,
        promotionCode: String?
    ): String? {
        val (created, customer) = stripeClient.getCustomerOrCreateIfMissing(
            customerId = customerId,
            userId = userId,
            email = email
        )

        if (created) {
            userDao.setStripeCustomerId(userId, customer.id)
        }

        return stripeClient.createSubscription(customer.id, priceId, promotionCode)
    }

    /**
     * Cancels the subscription that matches the given [subscriptionId]
     *
     * @throws StripeException
     *
     * @return true if the subscription was canceled, false if no subscription matched the [subscriptionId]
     */
    fun cancelSubscription(subscriptionId: String, cancellationInfo: ProSubscriptionCancellationRequestData) =
        stripeClient.cancelSubscription(subscriptionId, cancellationInfo)
}