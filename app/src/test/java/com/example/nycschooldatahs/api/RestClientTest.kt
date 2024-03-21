package com.example.nycschooldatahs.api

import com.example.nycschooldatahs.api.response.NYCSchoolResponse
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RestClientTest{

    private lateinit var mockWebServer: MockWebServer
    private lateinit var service: RestClient

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        service = retrofit.create(RestClient::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    // similar tests can be written for other APIs as well
    // specifically negative tests can be written which can be really helpful with API failing cases

    // test to check when the list is not empty
    @Test
    fun testApiCall_SchoolListNotEmpty() = runBlocking {
        // Enqueue a mock response
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("[{\"school_name\": \"Test School\", \"dbn\": \"123\"}]")
        mockWebServer.enqueue(mockResponse)

        // Make the API call
        val response: List<NYCSchoolResponse> = service.getNYCSchoolInformationNoResponse("yourAppToken", 10, 0)

        // Assert the response
        assert(response.isNotEmpty())
        assert(response[0].school_name== "Test School")
        assert(response[0].dbn == "123")
    }

    // test to check when the list is empty
    @Test
    fun testApiCall_SchoolListEmpty() = runBlocking {
        // Enqueue a mock response
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("[]")
        mockWebServer.enqueue(mockResponse)

        // Make the API call
        val response: List<NYCSchoolResponse> = service.getNYCSchoolInformationNoResponse("yourAppToken", 10, 0)

        // Assert the response
        assert(response.isEmpty())
    }
}