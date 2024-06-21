package com.app.quotesapp.data.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.quotesapp.data.repository.QuotesRepository
import com.app.quotesapp.include.QuotesState
import com.app.quotesapp.data.models.QuotesResult
import com.app.quotesapp.data.models.ResponseQuotes
import com.app.quotesapp.data.utlis.CommonFunction
import com.app.quotesapp.data.utlis.CommonFunction.isNetworkAvailable
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuotesViewModel @Inject constructor(
    private val repository: QuotesRepository, @ApplicationContext private val context: Context,
) : ViewModel() {
    private val _response: MutableStateFlow<QuotesState<ResponseQuotes?>> =
        MutableStateFlow(QuotesState.Loading)
    val quoteResponse: StateFlow<QuotesState<ResponseQuotes?>> = _response

    private val _responseRandomQuote: MutableStateFlow<QuotesState<QuotesResult?>> =
        MutableStateFlow(QuotesState.Loading)
    val randomQuoteResponse: StateFlow<QuotesState<QuotesResult?>> = _responseRandomQuote

    init {
        fetchQuotes()
        fetchRandomQuotes()
    }

    private fun fetchQuotes() {
        viewModelScope.launch {
            if (CommonFunction.isNetworkAvailable(context)) {
                try {
                    val response = repository.getQuotesRepo().first()
                    _response.emit(QuotesState.Success(response))
                } catch (e: Exception) {
                    val errorMessage = "An error occurred. Please try again."
                    _response.emit(QuotesState.Error(errorMessage))
                }
            } else {
                _response.emit(QuotesState.Error("No internet connection"))
            }
        }
    }


    fun fetchRandomQuotes() {
        viewModelScope.launch {
            if (isNetworkAvailable(context)) {
                try {
                    val response = repository.getQuotesRandomRepo().first()
                    _responseRandomQuote.emit(QuotesState.Success(response))
                } catch (e: Exception) {
                    val errorMessage = "An error occurred. Please try again."
                    _responseRandomQuote.emit(QuotesState.Error(errorMessage))
                }
            } else {
                val errorMessage = "No internet connection."
                _responseRandomQuote.emit(QuotesState.Error(errorMessage))
            }
        }
    }

}


/*
//this is LiveData Code
private val _response: MutableLiveData<ResponseQuotes> = MutableLiveData()
val quoteResponse: LiveData<ResponseQuotes> = _response
init {
    fetchQuotes()
}

private fun fetchQuotes(){
    viewModelScope.launch {
        repository.getQuotesRepo()
            .catch {e->
                Log.e("MainViewModel_ERROR", "getPost: ${e.message}")
            }.collect {response->
                _response.value = response
                // postLiveData.value=response
            }

    }
}*/
