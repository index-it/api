package app.index_it.data.models.email

import kotlinx.serialization.Serializable

@Serializable
data class SendinblueCodeOperationRequestBody(
    val to: List<SendinblueGenericRequestBody.To>,
    val templateId: Long,
    val params: Params
) {
    @Serializable
    data class Params (
        val url: String
    )
}
