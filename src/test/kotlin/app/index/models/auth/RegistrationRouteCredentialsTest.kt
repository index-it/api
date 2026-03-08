package app.index.models.auth

import app.index.data.models.auth.RegistrationCredentials
import io.konform.validation.Valid
import org.junit.jupiter.api.Test

class TokenCredentialsTest {
    @Test
    fun validate() {
        val invalidCredentials1 =
            RegistrationCredentials(
                "test@gmail.com",
                "invalidpassword",
            )

        val invalidCredentials2 =
            RegistrationCredentials(
                "test@gmail.com",
                "invalidPassword",
            )

        val invalidCredentials3 =
            RegistrationCredentials(
                "test@gmail.com",
                "invalidpassword1",
            )

        val validCredentials =
            RegistrationCredentials(
                "test@gmail.com",
                "validPassword1",
            )

        val validCredentials2 =
            RegistrationCredentials(
                "hello@giuliopime.dev",
                "vAli4Passw0rD987",
            )

        val bruno = RegistrationCredentials(
            "index.detest750@simplelogin.com",
            "katia2roll.TOPOLOGY"
        )

        assert(invalidCredentials1.validate() !is Valid)
        assert(invalidCredentials2.validate() !is Valid)
        assert(invalidCredentials3.validate() !is Valid)

        assert(validCredentials.validate() is Valid)
        assert(validCredentials2.validate() is Valid)
        assert(bruno.validate() is Valid)
    }
}
