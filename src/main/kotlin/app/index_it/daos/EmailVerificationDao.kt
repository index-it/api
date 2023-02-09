package app.index_it.daos

import app.index_it.core.db.EmailVerificationDBM
import app.index_it.models.email.EmailVerificationDto

object EmailVerificationDao {
    fun get(code: String): EmailVerificationDto? = EmailVerificationDBM.get(code)

    fun save(emailVerificationDto: EmailVerificationDto) = EmailVerificationDBM.save(emailVerificationDto)

    fun delete(code: String) = EmailVerificationDBM.delete(code)

    fun isRateLimited(email: String): Boolean {
        val sent = EmailVerificationDBM.countSaved(email)
        return sent >= 3
    }
}
