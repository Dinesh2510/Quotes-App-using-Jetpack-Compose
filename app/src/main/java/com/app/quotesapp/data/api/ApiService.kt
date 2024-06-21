package com.app.quotesapp.data.api

import com.app.quotesapp.data.api.Constants.Companion.List_Of_quotes
import com.app.quotesapp.data.api.Constants.Companion.random_quotes
import com.app.quotesapp.data.models.QuotesResult
import com.app.quotesapp.data.models.ResponseQuotes
import retrofit2.http.GET

interface ApiService {
    @GET(List_Of_quotes)
    suspend fun getListOfQuotes(): ResponseQuotes

    @GET(random_quotes)
    suspend fun getRandomQuotes(): QuotesResult
}
