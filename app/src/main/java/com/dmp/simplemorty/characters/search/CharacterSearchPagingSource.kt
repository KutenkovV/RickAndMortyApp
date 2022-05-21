package com.dmp.simplemorty.characters.search

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dmp.simplemorty.domain.mappers.CharacterMapper
import com.dmp.simplemorty.domain.models.Character
import com.dmp.simplemorty.network.NetworkLayer

class CharacterSearchPagingSource(
    private val userSearch: String,
    private val localExceptionCallback: (LocalException) -> Unit
) : PagingSource<Int, Character>() {

    sealed class LocalException(
        val title: String,
        val description: String = ""
    ): Exception() {
        object EmptySearch : LocalException(
            title = "Начните поиск!"
        )
        object NoResults : LocalException(
            title = "Упс!",
            description = "Похоже такого персонажа нету :С"
        )
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Character> {

        if (userSearch.isEmpty()) {
            val exception = LocalException.EmptySearch
            localExceptionCallback(exception)
            return LoadResult.Error(exception)
        }

        val pageNumber = params.key ?: 1
        val previousKey = if (pageNumber == 1) null else pageNumber - 1

        val request = NetworkLayer.apiClient.getCharactersPage(
            characterName = userSearch,
            pageIndex = pageNumber
        )

        // Fail to find something from the user's search
        if (request.data?.code() == 404) {
            val exception = LocalException.NoResults
            localExceptionCallback(exception)
            return LoadResult.Error(exception)
        }

        request.exception?.let {
            return LoadResult.Error(it)
        }

        return LoadResult.Page(
            data = request.bodyNullable?.results?.map { characterResponse ->
                CharacterMapper.buildFrom(characterResponse)
            } ?: emptyList(),
            prevKey = previousKey,
            nextKey = getPageIndexFromNext(request.bodyNullable?.info?.next)
        )
    }

    override fun getRefreshKey(state: PagingState<Int, Character>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    private fun getPageIndexFromNext(next: String?): Int? {
        if (next == null) {
            return null
        }

        val remainder = next.substringAfter("page=")
        val finalIndex = if (remainder.contains('&')) {
            remainder.indexOfFirst { it == '&' }
        } else {
            remainder.length
        }

        return remainder.substring(0, finalIndex).toIntOrNull()
    }
}