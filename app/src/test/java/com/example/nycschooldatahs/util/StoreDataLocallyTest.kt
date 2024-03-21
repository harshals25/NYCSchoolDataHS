package com.example.nycschooldatahs.util

import org.junit.Assert.*

import android.content.Context
import android.content.SharedPreferences
import com.example.nycschooldatahs.api.response.NYCSchoolResponse
import com.google.gson.Gson

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

class StoreDataLocallyTest {

    @Mock
    private lateinit var mockContext: Context

    @Mock
    private lateinit var mockSharedPreferences: SharedPreferences

    private lateinit var storeDataLocally: StoreDataLocally

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        `when`(mockContext.getSharedPreferences("SchoolData", Context.MODE_PRIVATE))
            .thenReturn(mockSharedPreferences)
        storeDataLocally = StoreDataLocally(mockContext)
    }

    // Tests for storing data locally.
    // as we're storing in SP we have tests for that but if we decide to store with RoomDB we can make changes to our tests as required.

    // test to get all stored info
    @Test
    fun testGetAllSchoolInfo() {
        // Prepare mock JSON response
        val jsonMockResponse = "[{\"dbn\":\"1\",\"school_name\":\"School 1\"," +
                "\"overview_paragraph\":\"Overview 1\",\"primary_address_line_1\":\"Address 1\"," +
                "\"city\":\"City 1\",\"zip\":\"12345\",\"state_code\":\"NY\"," +
                "\"phone_number\":\"123-456-7890\",\"school_email\":\"school1@example.com\"," +
                "\"website\":\"www.school1.com\"}," +
                "{\"dbn\":\"2\",\"school_name\":\"School 2\"," +
                "\"overview_paragraph\":\"Overview 2\",\"primary_address_line_1\":\"Address 2\"," +
                "\"city\":\"City 2\",\"zip\":\"67890\",\"state_code\":\"NY\"," +
                "\"phone_number\":\"987-654-3210\",\"school_email\":\"school2@example.com\"," +
                "\"website\":\"www.school2.com\"}]"
        `when`(mockSharedPreferences.getString("school_info", null)).thenReturn(jsonMockResponse)

        // Call the function under test
        val result = storeDataLocally.getAllSchoolInfo()

        // Verify the result
        val expected = listOf(
            NYCSchoolResponse("1", "School 1", "Overview 1", "Address 1", "City 1",
                "12345", "NY", "123-456-7890", "school1@example.com", "www.school1.com"),
            NYCSchoolResponse("2", "School 2", "Overview 2", "Address 2", "City 2",
                "67890", "NY", "987-654-3210", "school2@example.com", "www.school2.com")
        )
        assertEquals(expected, result)
    }
}
