package com.example.nycschooldatahs.util

import android.content.Context


// increases testability - we can change the name and add more functions here which we can override in Utilities.
// we can test if our interface properly calls the required function without actually trying to call the function.
// number of verification checks can increase based on what functions we decide to add here


interface InternetChecker {
    fun isInternetAvailable(context: Context): Boolean
}