package ru.sulgik.marksupdates.domain.data

data class PagingData<T>(
    val isLoading: Boolean,
    val isNextPageLoading: Boolean,
    val isRefreshing: Boolean,
    val currentPage: Int,
    val isFullLoaded: Boolean,
    val data: T?,
)