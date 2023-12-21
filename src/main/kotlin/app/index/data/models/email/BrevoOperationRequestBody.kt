package app.index.data.models.email

import kotlinx.serialization.Serializable

@Serializable
data class BrevoOperationRequestBody(
    val to: List<BrevoGenericRequestBody.To>,
    val templateId: Long,
    val params: Params,
) {
    @Serializable
    data class Params(
        val url: String,
    )
}
