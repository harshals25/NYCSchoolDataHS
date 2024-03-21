package com.example.nycschooldatahs.api.response

data class NYCSchoolResponse (

    val dbn: String?,
    val school_name: String?,
    val overview_paragraph: String?,
    val primary_address_line_1: String?,
    val city: String?,
    val zip: String?,
    val state_code: String?,
    val phone_number: String?,
    val school_email: String?,
    val website: String?

)