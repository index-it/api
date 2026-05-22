package app.index.core.logic.typedId.serialization

import app.index.api.core.logic.typedId.impl.IxId
import app.index.api.core.logic.typedId.newIxId
import app.index.api.core.logic.typedId.newIxIntId
import app.index.api.core.logic.typedId.serialization.IdKotlinXSerializationModule
import app.index.api.data.models.email.EmailVerificationData
import app.index.api.data.models.user.UserData
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class IdKotlinXSerializationModuleKtTest {
    private val json =
        Json {
            serializersModule = IdKotlinXSerializationModule
            prettyPrint = true
        }

    @Test
    fun idSerialization() {
        val id = newIxId<UserData>()
        val serialized =
            json.encodeToString(id).let {
                it.substring(1, it.length - 1)
            }

        assertEquals(id.toString(), serialized)

        val intId = newIxIntId<EmailVerificationData>()
        val serializedInt =
            json.encodeToString(intId).let {
                it.substring(1, it.length - 1)
            }

        assertEquals(intId.toString(), serializedInt)
    }

    @Test
    fun objectWithIdSerialization() {
        @Serializable
        data class TestObject(
            @Contextual val id: IxId<UserData>,
            val name: String,
        )

        val testObject =
            TestObject(
                id = newIxId(),
                name = "test",
            )
        val serialized = json.encodeToString(testObject)

        assertEquals(
            """
            {
                "id": "${testObject.id}",
                "name": "${testObject.name}"
            }
            """.trimIndent(),
            serialized,
        )
    }
}
