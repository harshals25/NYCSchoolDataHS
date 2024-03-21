package com.example.nycschooldatahs.schoolinfo

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.example.nycschooldatahs.NYCRepository
import com.example.nycschooldatahs.NYCViewModel
import com.example.nycschooldatahs.NYCViewModelFactory
import com.example.nycschooldatahs.api.RestClient
import com.example.nycschooldatahs.api.RetrofitClient
import com.example.nycschooldatahs.api.response.NYCSchoolSATInfoResponse
import com.example.nycschooldatahs.ui.theme.NYCSchoolDataHSTheme
import com.example.nycschooldatahs.util.StoreDataLocally
import com.example.nycschooldatahs.util.Utilities
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SchoolInformationActivity() : ComponentActivity() {

    private lateinit var nycViewModel: NYCViewModel
    private lateinit var retrofitClient: RestClient
    private lateinit var nycRepository: NYCRepository
    private val applicationScope = CoroutineScope(Dispatchers.Default)
    private lateinit var storeDataLocally: StoreDataLocally
    private var schoolName: String? = ""
    private var nycSchoolSATInfoResponse: NYCSchoolSATInfoResponse? = null
    lateinit var utilities: Utilities


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        storeDataLocally = StoreDataLocally(this)
        utilities = Utilities()

        // fetch values from intent.
        val dbn = intent.getStringExtra("dbn")
        schoolName = intent.getStringExtra("schoolName")

        // null check and handling for DBN
        if (dbn.isNullOrEmpty()) {
            handleNullDBN()
        } else {
            // checking for internet and displaying as required.
            if (utilities.isInternetAvailable(this)) {
                fetchDataFromRemote(dbn)
            } else {
                val schoolSATInfo = dbn.let { storeDataLocally.getSchoolSATInfoByDBN(dbn) }
                if (schoolSATInfo != null) {
                    nycSchoolSATInfoResponse = schoolSATInfo
                    displayCurrentSchoolInfo()
                } else {
                    showNoDataAndFinish()
                }
            }
        }

    }

    private fun fetchDataFromRemote(dbn: String?) {

        retrofitClient = RetrofitClient.getRetrofitInstance()
        nycRepository = NYCRepository(retrofitClient)
        nycViewModel = ViewModelProvider(
            this,
            NYCViewModelFactory(nycRepository, HEADER)
        )[NYCViewModel::class.java]

        dbn?.let { nycViewModel.getNYCSchoolSATInfo(HEADER, it) }

        nycViewModel.nycSchoolSATInfo.observe(this) { nycSchoolSatInfoList ->
            if (nycSchoolSatInfoList != null) {
                if (nycSchoolSatInfoList.isNotEmpty()) {
                    applicationScope.launch {
                        // Save the retrieved school information locally
                        storeDataLocally.saveSchoolSATInfo(nycSchoolSatInfoList.first())
                    }
                }
                nycSchoolSATInfoResponse = nycSchoolSatInfoList.firstOrNull()
                displayCurrentSchoolInfo()
            }
        }
        nycViewModel.errorMessage.observe(this) { errorString ->
            Log.d("demo", "Error $errorString")
        }
    }

    private fun displayCurrentSchoolInfo() {
        setContent {
            NYCSchoolDataHSTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (nycSchoolSATInfoResponse != null)
                        DisplaySchoolInfo(nycSchoolSATInfoResponse!!)
                    else
                        NoInformationPresent(modifier = Modifier, schoolName)
                }
            }

        }
    }

    private fun showNoDataAndFinish() {
        Toast.makeText(
            this,
            "No internet connection and no local data available",
            Toast.LENGTH_SHORT
        ).show()
        finish()
    }

    private fun handleNullDBN() {
        Toast.makeText(this, "Invalid or missing DBN", Toast.LENGTH_SHORT).show()
        finish()
    }

    // Header can be put into string.xml or to be more secure can be fetched by another API call.
    companion object {
        const val HEADER = "I6J1uiQp9KehdJkPpnldarnhO"
    }
}

