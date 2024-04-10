package com.example.nycschooldatahs.schoolinfo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.nycschooldatahs.api.response.NYCSchoolSATInfoResponse
import com.example.nycschooldatahs.ui.theme.NYCSchoolDataHSTheme

@Composable
fun DisplaySchoolInfo(nycSchoolSATInfoResponse: NYCSchoolSATInfoResponse) {
    Card(
        modifier = Modifier.padding(12.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                textAlign = TextAlign.Center,
                text = "${nycSchoolSATInfoResponse.school_name}",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "SAT Takers: ${nycSchoolSATInfoResponse.num_of_sat_test_takers}",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = "SAT Math Average: ${nycSchoolSATInfoResponse.sat_math_avg_score}",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = "SAT Writing Average: ${nycSchoolSATInfoResponse.sat_writing_avg_score}",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = "SAT Critical Reading Average: ${nycSchoolSATInfoResponse.sat_critical_reading_avg_score}",
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }

}

// Can pass school name from MainActivity and show here.
@Composable
fun NoInformationPresent(
    modifier: Modifier = Modifier,
    schoolName: String?
) {
    Card(
        modifier = modifier.padding(12.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = modifier.padding(16.dp)
        ) {
            Text(
                textAlign = TextAlign.Center,
                text = "$schoolName",
                style = MaterialTheme.typography.headlineLarge,
                modifier = modifier.fillMaxWidth()
            )
            Spacer(modifier = modifier.height(8.dp))
            Text(
                // we can block the user on first screen itself
                // we can call the API on click of the item in the list and block/show user appropriate message.
                textAlign = TextAlign.Center,
                text = "No information is available for $schoolName, please try a different school.",
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DisplaySchoolInfoPreview() {
    NYCSchoolDataHSTheme {
        val nycSchoolSATInfoResponse = NYCSchoolSATInfoResponse("1234","School Name","1234","123213","2132131","12321321")
        DisplaySchoolInfo(nycSchoolSATInfoResponse)
    }
}
