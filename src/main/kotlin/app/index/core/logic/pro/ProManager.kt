package app.index.core.logic.pro

import app.index.core.clients.StripeClient
import app.index.core.logic.typedId.impl.IxId
import app.index.data.daos.user.UserDao
import app.index.data.models.user.UserData
import com.stripe.exception.StripeException
import org.koin.core.annotation.Single

@Single(createdAtStart = true)
class ProManager(
    private val userDao: UserDao,
    private val stripeClient: StripeClient
) {

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
    suspend fun createSubscription(
        customerId: String?,
        userId: IxId<UserData>,
        email: String,
        priceId: String
    ): String {
        val (created, customer) = stripeClient.getCustomerOrCreateIfMissing(
            customerId = customerId,
            userId = userId,
            email = email
        )

        if (created) {
            userDao.setStripeCustomerId(userId, customer.id)
        }

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