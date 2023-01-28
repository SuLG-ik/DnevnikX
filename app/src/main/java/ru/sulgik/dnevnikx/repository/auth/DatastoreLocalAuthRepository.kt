package ru.sulgik.dnevnikx.repository.auth

import android.content.Context
import androidx.datastore.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable
import org.koin.core.annotation.Single
import ru.sulgik.dnevnikx.utils.DataStoreSerializer

@Single
class DatastoreLocalAuthRepository(
    private val context: Context,
) : LocalAuthRepository {

    @Serializable
    data class DataStoreValue(
        val authorization: Authorization? = null,
    ) {
        @Serializable
        data class Authorization(
            val token: String,
            val id: String,
        )
    }

    private val Context.source by dataStore(
        fileName = "auth",
        serializer = DataStoreSerializer(DataStoreValue())
    )

    override suspend fun getAuthorizationOrNull(): Authorization? {
        return context.source.data.first().authorization?.toData()
    }

    override suspend fun getAuthorization(id: String): Authorization {
        return getAuthorizationOrNull(id)
            ?: throw IllegalStateException("User with id = $id does not authorized")
    }

    override suspend fun getAuthorizationOrNull(id: String): Authorization? {
        return getAuthorizationOrNull()
    }

    override suspend fun setAuthorization(authorization: Authorization) {
        context.source.updateData {
            it.copy(
                authorization = DataStoreValue.Authorization(
                    token = authorization.token,
                    id = authorization.id
                )
            )
        }
    }

}

private fun DatastoreLocalAuthRepository.DataStoreValue.Authorization.toData(): Authorization {
    return Authorization(token = token, id = id)
}
