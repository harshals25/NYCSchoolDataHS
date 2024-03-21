package com.example.nycschooldatahs.mainactivity

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Face
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.nycschooldatahs.NYCViewModel
import com.example.nycschooldatahs.schoolinfo.SchoolInformationActivity
import com.example.nycschooldatahs.api.ListState
import com.example.nycschooldatahs.api.response.NYCSchoolResponse
import com.example.nycschooldatahs.ui.theme.NYCSchoolDataHSTheme
import com.example.nycschooldatahs.util.StoreDataLocally
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// when in offline mode - no pagination required - show the whole list
@Composable
fun ShowSchoolList(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    nycResponseList: List<NYCSchoolResponse>
) {
    LazyColumn(modifier = modifier.padding(vertical = 4.dp)) {
        items(items = nycResponseList) { nycSchoolResponse ->
            DisplayListItems(modifier, navController, nycSchoolResponse = nycSchoolResponse)
        }
    }
}

@Composable
fun DisplayListItems(
    modifier: Modifier,
    navController: NavHostController,
    nycSchoolResponse: NYCSchoolResponse
) {

    val context = LocalContext.current
    Card(
        modifier = modifier.padding(vertical = 4.dp, horizontal = 8.dp).clickable {
            takeMeToNextScreen(navController,context, nycSchoolResponse)
        },

        ) {
        CardContent( nycSchoolResponse)
    }
}


fun takeMeToNextScreen(
    navController: NavHostController,
    context: Context,
    nycSchoolResponse: NYCSchoolResponse
) {
    // if I had more time, I'd have implemented Jetpack Compose Navigation or some other library with the current use case
    // instead of using intents.
    val intent = Intent(context, SchoolInformationActivity::class.java)
    intent.putExtra("dbn", nycSchoolResponse.dbn)
    intent.putExtra("schoolName",nycSchoolResponse.school_name)
    context.startActivity(intent)
}


@Composable
private fun CardContent(nycSchoolResponse: NYCSchoolResponse) {
    val address: String = nycSchoolResponse.primary_address_line_1 + ", " +
            nycSchoolResponse.city + ", " + nycSchoolResponse.state_code + ", " + nycSchoolResponse.zip

    Row(modifier = Modifier.padding(12.dp)) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(12.dp)
        ) {
            nycSchoolResponse.dbn?.let { dbn ->
                Text(color = Color.Cyan, text = dbn)
            }
            nycSchoolResponse.school_name?.let { schoolName ->
                Text(
                    text = schoolName,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold
                    )
                )
            }
            Text(
                text = address,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Normal
                )
            )
        }
    }
}

@Composable
fun NycSchoolListPaginated(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    nycViewModel: NYCViewModel
) {
    val lazyColumnListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val shouldStartPaginate = remember {
        derivedStateOf {
            nycViewModel.canPaginate.value &&
                    (lazyColumnListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1) >=
                    (lazyColumnListState.layoutInfo.totalItemsCount - 5)
        }
    }

    LaunchedEffect(key1 = shouldStartPaginate.value) {
        if (shouldStartPaginate.value && nycViewModel.listState == ListState.IDLE)
            nycViewModel.getNYCSchoolInfoPaging(MainActivity.HEADER)
    }

    val nycSchoolResponse = nycViewModel.nycSchoolInfoPaging
    val applicationScope = CoroutineScope(Dispatchers.Default)
    val storeDataLocally = StoreDataLocally(LocalContext.current)


    LazyColumn(state = lazyColumnListState) {
        items(
            items = nycSchoolResponse,
            key = { article -> article.dbn!! }
        ) { article ->
            applicationScope.launch {
                    storeDataLocally.saveAllSchoolInfo(nycSchoolResponse)
                }
            DisplayListItems(modifier,navController,nycSchoolResponse = article)
            HorizontalDivider()
        }
        item(
            key = nycViewModel.listState,
        ) {
            when (nycViewModel.listState) {
                ListState.LOADING -> {
                    Column(
                        modifier = Modifier
                            .fillParentMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(8.dp),
                            text = "Loading Content"
                        )

                        CircularProgressIndicator(color = Color.White)
                    }
                }
                ListState.PAGINATING -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text(text = "Loading")

                        CircularProgressIndicator(color = Color.White)
                    }
                }
                ListState.PAGINATION_EXHAUST -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 6.dp, vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Icon(imageVector = Icons.Rounded.Face, contentDescription = "")

                        Text(text = "Nothing left.")

                        // click function can be implemented which can take user to the top of the screen
                        TextButton(
                            modifier = Modifier
                                .padding(top = 8.dp),
                            onClick = {
                                coroutineScope.launch {
                                    lazyColumnListState.animateScrollToItem(0)
                                }
                            },
                            content = {
                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {

                                    Text(text = "Back to Top")
                                }
                            }
                        )
                    }
                }
                else -> {}
            }
        }
    }
}

@Preview
@Composable
fun NycSchoolListPreview() {
    val nycResponseList = listOf(
        NYCSchoolResponse(
            "12345",
            "School 11",
            "School 12 info",
            "123 Merlin Way",
            "FXBG",
            "22312",
            "VA",
            "",
            "",
            ""
        ),
        NYCSchoolResponse("1234", "School 31", "School 13 info", "", "", "", "", "", "", ""),
        NYCSchoolResponse("123as4", "School 1s", "School 1 info", "", "", "", "", "", "", ""),
        NYCSchoolResponse("12w34", "School 14", "School 15 info", "", "", "", "", "", "", "")

    )
    NYCSchoolDataHSTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {

            ShowSchoolList(Modifier.fillMaxSize(), NavHostController(LocalContext.current), nycResponseList)
        }
    }
}


