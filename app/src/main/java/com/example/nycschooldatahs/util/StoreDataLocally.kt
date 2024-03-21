package com.example.nycschooldatahs.util

import android.content.Context
import com.example.nycschooldatahs.api.response.NYCSchoolResponse
import com.example.nycschooldatahs.api.response.NYCSchoolSATInfoResponse
import com.google.gson.Gson

class StoreDataLocally (private val context: Context) {

    // Future implementation - Use Room DB instead of Shared Preference.
    // Paging 3 given by JetPack compose works with RoomDB, can be used to enhance code

    private val gson = Gson()

    private val sharedPreferences = context.getSharedPreferences(
        PREF_NAME,
        Context.MODE_PRIVATE
    )

    // make this into a set, so that only unique values are getting stored.
    // currently it just keeps on storing the information, in case of offline mode this might just display a very big repeating list of same objects.
    // Converting to set will be good so that only unique values are getting stored in the SP.
    fun saveAllSchoolInfo(nycResponseList: List<NYCSchoolResponse>) {
        // store all the school's information
        val jsonString = gson.toJson(nycResponseList)
        sharedPreferences.edit().putString(PREF_KEY, jsonString).apply()
    }

    fun getAllSchoolInfo(): List<NYCSchoolResponse>? {
        // retrieves all the school information
        val jsonString = sharedPreferences.getString(PREF_KEY, null)
        return gson.fromJson(jsonString, Array<NYCSchoolResponse>::class.java)?.toList()
    }


    // incorporated Set for SAT info to store only unique values.
    // The search here is more optimized as well to get the one school by DBN

    fun saveSchoolSATInfo(nycSchoolSATInfo: NYCSchoolSATInfoResponse) {
        // store the SAT info for the school
        val existingList = getSchoolSATInfo().toMutableList()
        if(!existingList.contains(nycSchoolSATInfo))
            existingList.add(nycSchoolSATInfo)
        val jsonString = gson.toJson(existingList)
        sharedPreferences.edit().putString(PREF_KEY_SAT, jsonString).apply()
    }

    fun getSchoolSATInfoByDBN(dbn : String): NYCSchoolSATInfoResponse? {
        // get the school info by DBN
        val jsonString = sharedPreferences.getString(PREF_KEY_SAT, null)
        val allSchoolSATInfo = if (jsonString != null) {
            gson.fromJson(jsonString, Array<NYCSchoolSATInfoResponse>::class.java).toSet()
        } else {
            emptySet()
        }
        // Filter the set to include only items with the given DBN
        return allSchoolSATInfo.firstOrNull{ it.dbn == dbn }
    }

    private fun getSchoolSATInfo(): Set<NYCSchoolSATInfoResponse> {
        // get the list of stored SAT info for schools.
        val jsonString = sharedPreferences.getString(PREF_KEY_SAT, null)
        return if (jsonString != null) {
            gson.fromJson(jsonString, Array<NYCSchoolSATInfoResponse>::class.java).toSet()
        } else {
            emptySet()
        }
    }
    companion object {
        private const val PREF_NAME = "SchoolData"
        private const val PREF_KEY = "school_info"
        private const val PREF_KEY_SAT = "school_sat_info"
    }
}