package ru.sulgik.marksupdates.domain

import kotlinx.coroutines.flow.StateFlow
import ru.sulgik.marksupdates.domain.data.MarksUpdatesOutput
import ru.sulgik.marksupdates.domain.data.PagingData

interface PagingRemoteMarksUpdateSource {

    val data: StateFlow<PagingData<MarksUpdatesOutput>>

    suspend fun loadNextPage(count: Int = 1)

    suspend fun refreshPage(startCount: Int = 1)

}