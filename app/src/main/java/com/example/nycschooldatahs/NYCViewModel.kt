package com.example.nycschooldatahs

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.nycschooldatahs.api.ListState
import com.example.nycschooldatahs.api.response.NYCSchoolResponse
import com.example.nycschooldatahs.api.response.NYCSchoolSATInfoResponse
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import retrofit2.Response

class NYCViewModel(private val nycRepository: NYCRepository, private val  header : String) : ViewModel() {

    // initializing required variables

    private val nycSchoolInfo = MutableLiveData<List<NYCSchoolResponse>>()
    val nycSchoolInfoPaging = mutableStateListOf<NYCSchoolResponse>()

    var page = mutableStateOf(1) // if we use page by mutableStateOf(1) we don't have to use.value
    var canPaginate = mutableStateOf(false)
    var listState by mutableStateOf(ListState.IDLE)

    val nycSchoolSATInfo = MutableLiveData<Set<NYCSchoolSATInfoResponse>>()
    val errorMessage = MutableLiveData<String>()
    private var job: Job? = null
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }


    // simple way to get all the values in one go, big API call - time consuming. We can use pagination instead.
    fun getNYCSchoolInfo(header : String){
        job = CoroutineScope(Dispatchers.IO).launch() {
                val nycSchoolData = nycRepository.getNYCSchoolInfo(header)
                if(nycSchoolData.isSuccessful)
                    nycSchoolInfo.postValue(nycSchoolData.body())
                else{
                    errorMessage.value = "Unexpected error occurred. Please try again in some time."
                }
        }
    }


    fun getNYCSchoolInfoPaging(header : String) {
        viewModelScope.launch {
            try {
                if (page.value == 1 || (page.value != 1 && canPaginate.value) && listState == ListState.IDLE) {
                    listState = if (page.value == 1) ListState.LOADING else ListState.PAGINATING
                    val offset = (page.value - 1) * PAGE_SIZE
                    val limit = PAGE_SIZE
                    nycRepository.getNYCSchoolInfoNoResponse(
                        header,
                        limit,
                        offset
                    ).collect() {
                        if (it.isNotEmpty()) {
                            canPaginate.value = it.size == PAGE_SIZE

                            if (page.value == 1) {
                                nycSchoolInfoPaging.clear()
                                nycSchoolInfoPaging.addAll(it)
                            } else {
                                nycSchoolInfoPaging.addAll(it)
                            }
                            listState = ListState.IDLE
                            if (canPaginate.value)
                                page.value++
                        } else {
                            listState =
                                if (page.value == 1) ListState.ERROR else ListState.PAGINATION_EXHAUST
                        }
                    }
                }
            }
            catch (e : Exception){
                errorMessage.value = "Unexpected error occurred. Please try again in some time."
            }
        }
    }


    // if there was a refresh functionality this would've come in handy
    override fun onCleared() {
        page.value = 1
        listState = ListState.IDLE
        canPaginate.value = false
        super.onCleared()
    }

    fun getNYCSchoolSATInfo(header : String, dbn : String){
//        job?.cancel() // to make synchronous we can cancel the previous one but not needed here
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch() {
                val nycSchoolSATData = nycRepository.getNYCSchoolSatInfo(header, dbn)
                if(nycSchoolSATData.isSuccessful)
                    nycSchoolSATInfo.postValue(nycSchoolSATData.body())
                else{
                    errorMessage.value = "Unexpected error occurred. Please try again in some time."
                }
        }
    }

    // error handling can be improved based on what the API is giving when it fails or what we want to show the user
    private fun onError(message: String) {
        errorMessage.value = message
    }


    companion object {
        const val PAGE_SIZE = 10 // setting the page size to 10
    }

}
