package app.index.data.models.email

import kotlinx.serialization.Serializable

interface BrevoOperationRequestParams

@Serializable
data class BrevoOperationRequestBody(
    val to: List<BrevoGenericRequestBody.To>,
    val templateId: Long,
    val params: BrevoOperationRequestParams,
) {
    @Serializable
    data class Params(
        val url: String,
    ) : BrevoOperationRequestParams

    @Serializable
    data class ListInviteParams(
        val url: String,
        val inviter: String,
        val list_name: String,
        val role: String
    ) : BrevoOperationRequestParams
}
