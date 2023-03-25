package ru.sulgik.marksupdates.domain

import ru.sulgik.auth.core.AuthScope
import ru.sulgik.marksupdates.domain.data.MarksUpdatesOutput

interface RemoteMarksUpdatesRepository {

    suspend fun getMarksUpdates(auth: AuthScope, page: Int, limit: Int): MarksUpdatesOutput

}