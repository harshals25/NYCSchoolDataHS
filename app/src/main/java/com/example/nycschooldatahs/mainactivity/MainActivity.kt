package com.example.nycschooldatahs.mainactivity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.example.nycschooldatahs.NYCRepository
import com.example.nycschooldatahs.NYCViewModel
import com.example.nycschooldatahs.NYCViewModelFactory
import com.example.nycschooldatahs.api.RestClient
import com.example.nycschooldatahs.api.RetrofitClient
import com.example.nycschooldatahs.api.response.NYCSchoolResponse
import com.example.nycschooldatahs.ui.theme.NYCSchoolDataHSTheme
import com.example.nycschooldatahs.util.InternetChecker
import com.example.nycschooldatahs.util.StoreDataLocally
import com.example.nycschooldatahs.util.Utilities

class MainActivity : ComponentActivity() {

    lateinit var nycViewModel: NYCViewModel
    lateinit var retrofitClient: RestClient
    lateinit var nycRepository: NYCRepository
    lateinit var storeDataLocally : StoreDataLocally
    lateinit var internetChecker: InternetChecker


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        storeDataLocally = StoreDataLocally(this)
        internetChecker = Utilities()

        fetchData()
    }

    // separate out the logics into a MainActivityService class and also create an interface which can be used to
    // call all the functions inside MainActivityService, increasing testability and code readability.
    // it'll also increase dependency injection as we can include what to pass to what function as required.

    private fun fetchData() {
        // internet check - if present fetching from API, if not fetching from local if present
        if (internetChecker.isInternetAvailable(this)) {
            fetchDataFromRemote()
        } else {
            val localData = storeDataLocally.getAllSchoolInfo()
            if (localData != null && localData.isNotEmpty()) {
                displayListFromLocalStorage(localData)
            } else {
                showNoDataToast()
            }
        }
    }
    private fun fetchDataFromRemote() {

        // more error handling can be put, making sure all these are properly initialized.
        // they can be put in try catch and if an exception occurs we can show a proper message.

        retrofitClient = RetrofitClient.getRetrofitInstance()
        nycRepository = NYCRepository(retrofitClient)
        nycViewModel = ViewModelProvider(
            this,
            NYCViewModelFactory(nycRepository, HEADER)
        )[NYCViewModel::class.java]

        nycViewModel.getNYCSchoolInfoPaging(HEADER)
        displayListFromAPI()

         // in case the API fails at any point, we log it, we can also show a toast as needed or initiate the call again
         // by implementing a retry mechanism
        nycViewModel.errorMessage.observe(this) { errorString ->
            Log.d("demo", "Error $errorString")
        }
    }



    private fun displayListFromAPI() {
        setContent {
            NYCSchoolDataHSTheme{
                Surface(
                    modifier = Modifier.fillMaxSize(),
                )
                {
                    val navController = rememberNavController()
//                    Navigation(navController = navController) dont know how to use NavController. Yet!
                    NycSchoolListPaginated(
                        modifier = Modifier.fillMaxSize(),
                        navController,
                        nycViewModel)
                }
            }
        }
    }

    private fun displayListFromLocalStorage(nycResponseList: List<NYCSchoolResponse>) {
        setContent {
            NYCSchoolDataHSTheme{
                Surface(
                    modifier = Modifier.fillMaxSize(),
                )
                {
                    val navController = rememberNavController()
//                    Navigation(navController = navController)
                    ShowSchoolList(
                        modifier = Modifier.fillMaxSize(),
                        navController,
                        nycResponseList
                    )
                }
            }
        }

    }


    private fun showNoDataToast() {
        Toast.makeText(this,"No internet connection and no local data available",Toast.LENGTH_LONG).show()
    }


    // Header can be put into string.xml or to be more secure can be fetched by another API call.
    companion object{
        const val HEADER = "I6J1uiQp9KehdJkPpnldarnhO"
    }



}

