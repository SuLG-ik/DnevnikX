package ru.sulgik.marksupdates.domain

import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import ru.sulgik.auth.core.AuthScope
import ru.sulgik.marksupdates.domain.data.MarksUpdatesOutput
import ru.sulgik.marksupdates.domain.data.PagingData

class AndroidPagingRemoteMarksUpdateSource(
    private val remoteMarksUpdatesRepository: RemoteMarksUpdatesRepository,
    private val authScope: AuthScope,
) : PagingRemoteMarksUpdateSource {

    override val data: MutableStateFlow<PagingData<MarksUpdatesOutput>> = MutableStateFlow(
        PagingData(
            isLoading = true,
            isNextPageLoading = false,
            isRefreshing = false,
            currentPage = 0,
            isFullLoaded = false,
            data = null,
        )
    )

    private var isProcessing = false
    private val processingMutex = Mutex()

    private var nextPageJob: Job? = null

    private suspend fun toggleProcessing(value: Boolean): Boolean {
        processingMutex.withLock {
            if (isProcessing == value) {
                return false
            }
            isProcessing = value
            return true
        }
    }

    override suspend fun loadNextPage(count: Int) {
        if (!toggleProcessing(true))
            return
        val job = Job()
        nextPageJob = job
        withContext(job) {
            repeat(count) {
                loadPage()
            }
        }
        toggleProcessing(false)
    }

    override suspend fun refreshPage(startCount: Int) {
        toggleProcessing(true)
        nextPageJob?.cancelAndJoin()
        refresh()
        repeat(startCount - 1) {
            loadPage()
        }
        toggleProcessing(false)
    }


    private suspend fun loadPage() {
        val currentData = data.value
        val nextKey = currentData.nextKey() ?: return
        data.value = currentData.copy(isNextPageLoading = true)
        val output =
            remoteMarksUpdatesRepository.getMarksUpdates(authScope, nextKey, 30)
        data.value = PagingData(
            isLoading = false,
            isNextPageLoading = false,
            isRefreshing = false,
            currentPage = nextKey,
            isFullLoaded = (output.old.size + output.latest.size) < 30,
            data = appendedData(currentData.data, output),
        )
    }

    private suspend fun refresh() {
        val currentData = data.value
        data.value = currentData.copy(isRefreshing = true, isNextPageLoading = false)
        val output =
            remoteMarksUpdatesRepository.getMarksUpdates(authScope, 1, 30)
        data.value = PagingData(
            isLoading = false,
            isNextPageLoading = false,
            isRefreshing = false,
            currentPage = 1,
            isFullLoaded = (output.old.size + output.latest.size) < 30,
            data = output,
        )
    }

    private fun appendedData(
        currentData: MarksUpdatesOutput?,
        nextData: MarksUpdatesOutput,
    ): MarksUpdatesOutput {
        return if (currentData == null)
            nextData
        else
            MarksUpdatesOutput(
                latest = currentData.latest + nextData.latest,
                old = currentData.old + nextData.old,
            )
    }


    private fun PagingData<MarksUpdatesOutput>.nextKey(): Int? {
        if (data == null) return 1
        if (isFullLoaded) {
            return null
        }
        return currentPage + 1
    }

}