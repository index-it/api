package app.index_it.models.email

import kotlinx.serialization.Serializable

@Serializable
data class SendinblueCodeOperationRequestBody(
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
}
