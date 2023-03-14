package ru.sulgik.account.domain

import android.content.Context
import androidx.datastore.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable
import ru.sulgik.account.domain.data.AccountSession
import ru.sulgik.common.DataStoreSerializer

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