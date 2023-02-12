package app.index_it.models.email

import kotlinx.serialization.Serializable

@Serializable
data class SendinblueEmailVerificationRequestBody(
    val sender: From,
    val to: List<To>,
    val templateId: Long,
    val params: Params
) {
    @Serializable
    data class Params (
        val url: String
    )

    @Serializable
    data class To (
        val email: String
    )

    @Serializable
    data class From (
        val name: String,
        val email: String
    )
}
