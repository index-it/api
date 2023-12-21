package app.index.data.models.email

import kotlinx.serialization.Serializable

@Serializable
data class SendinblueGenericRequestBody(
    val to: List<To>,
    val templateId: Long,
) {
    @Serializable
    data class To(
        val email: String,
    )
}
