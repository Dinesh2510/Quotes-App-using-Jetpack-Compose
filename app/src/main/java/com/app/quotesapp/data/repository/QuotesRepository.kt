package com.app.quotesapp.data.repository

import com.app.quotesapp.data.api.ApiService
import com.app.quotesapp.data.models.QuotesResult
import com.app.quotesapp.data.models.ResponseQuotes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class QuotesRepository @Inject constructor(private val apiService: ApiService) {


    fun getQuotesRepo(): Flow<ResponseQuotes> = flow {
        val response = apiService.getListOfQuotes()
        emit(response)
    }.flowOn(Dispatchers.IO)

    fun getQuotesRandomRepo(): Flow<QuotesResult> = flow {
        val response = apiService.getRandomQuotes()
        emit(response)
    }.flowOn(Dispatchers.IO)
}
