package com.example.nycschooldatahs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class NYCViewModelFactory constructor(private val repository: NYCRepository, private val header : String): ViewModelProvider.Factory {

    // helps with separation of concerns if we have more viewModels
    // also good practice to do so for bigger projects as it enables dependency injection as well
    // really not sure if this is needed tho, maybe there is a better way to define architecture for this project which wont require this class?!
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(NYCViewModel::class.java)) {
            NYCViewModel(this.repository, header) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}
