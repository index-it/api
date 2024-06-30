package app.index.core.logic

import app.index.core.clients.StripeClient
import app.index.core.logic.typedId.impl.IxId
import app.index.data.models.user.UserData
import com.stripe.exception.StripeException
import org.koin.core.annotation.Single

@Single(createdAtStart = true)
class ProManager(
    private val stripeClient: StripeClient
) {
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
     * @return the client_secret for the subscription payment intent
     */
    fun createSubscription(
        customerId: String?,
        userId: IxId<UserData>,
        email: String,
        priceId: String
    ): String {
        val customer = stripeClient.getCustomerOrCreateIfMissing(
            customerId = customerId,
            userId = userId,
            email = email
        )

        return stripeClient.createSubscription(customer.id, priceId)
    }

    /**
     * Cancels the subscription that matches the given [subscriptionId]
     *
     * @throws StripeException
     *
     * @return true if the subscription was canceled, false if no subscription matched the [subscriptionId]
     */
    fun cancelSubscription(subscriptionId: String) =
        stripeClient.cancelSubscription(subscriptionId)
}