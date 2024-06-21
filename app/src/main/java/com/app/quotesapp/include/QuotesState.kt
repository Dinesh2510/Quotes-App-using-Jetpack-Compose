package com.app.quotesapp.include

sealed class QuotesState<out T> {
    data class Success<T>(val data: T) : QuotesState<T>()
    data class Error(val message: String) : QuotesState<Nothing>()
    object Loading : QuotesState<Nothing>()
}