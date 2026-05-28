package app.index.shared.core.data.models.email

import kotlinx.serialization.Serializable

@Serializable
data class BrevoListInviteRequestBody(
    val to: List<BrevoEmailField>,
    val templateId: Long,
    val params: Params,
    val replyTo: BrevoReplyToField
) {
    @Serializable
    data class Params(
        val url: String,
        val inviter: String,
        val list_name: String,
        val role: String
    )
}
