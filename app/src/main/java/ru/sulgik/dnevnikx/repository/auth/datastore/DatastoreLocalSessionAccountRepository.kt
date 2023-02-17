package ru.sulgik.dnevnikx.repository.auth.datastore

import android.content.Context
import androidx.datastore.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable
import org.koin.core.annotation.Single
import ru.sulgik.dnevnikx.data.AccountSession
import ru.sulgik.dnevnikx.repository.auth.LocalSessionAccountRepository
import ru.sulgik.dnevnikx.utils.DataStoreSerializer

@Single
class DatastoreLocalSessionAccountRepository(
    private val context: Context,
) : LocalSessionAccountRepository {

    @Serializable
    private class SessionValue(
        val accountId: String?,
    )

    private val Context.dataStore by dataStore(
        fileName = "sessions.data",
        serializer = DataStoreSerializer(SessionValue(null))
    )

    override suspend fun getLastAccountSession(): AccountSession? {
        return context.dataStore.data.first().accountId?.let { AccountSession(it) }
    }

    override suspend fun updateLastAccountSession(session: AccountSession?) {
        context.dataStore.updateData { SessionValue(session?.accountId) }
    }
}