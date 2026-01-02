package com.example.athleteregistrationapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.athleteregistrationapp.model.Athlete
import com.example.athleteregistrationapp.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AthleteDetailsScreen(navController: NavController, athleteId: String) {
    var athlete by remember { mutableStateOf<Athlete?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(athleteId) {
        RetrofitClient.api.getAthleteDetails(athleteId).enqueue(object : Callback<Athlete> {
            override fun onResponse(call: Call<Athlete>, response: Response<Athlete>) {
                isLoading = false
                if (response.isSuccessful) {
                    athlete = response.body()
                } else {
                    errorMessage = "Failed to load details: ${response.code()}"
                }
            }

            override fun onFailure(call: Call<Athlete>, t: Throwable) {
                isLoading = false
                errorMessage = "Network failure: ${t.message}"
            }
        })
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Athlete Profile (File)") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator()
            } else if (errorMessage != null) {
                Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error)
            } else {
                athlete?.let { data ->
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        item {
                            Text(text = "Personal Information", style = MaterialTheme.typography.titleLarge)
                            Text("Name: ${data.fullname ?: "N/A"}")
                            Text("DOB: ${data.dob ?: "N/A"}")
                            Text("Weight Class: ${data.weight_class ?: "N/A"}")
                            Text("Club: ${data.club ?: "N/A"}")
                            Text("Medical Info: ${data.medical_info ?: "N/A"}")
                            HorizontalDivider()
                        }

                        item {
                            Text(text = "Uploaded Documents", style = MaterialTheme.typography.titleLarge)
                        }

                        if (data.documents.isNullOrEmpty()) {
                            item {
                                Text(text = "No documents found for this athlete.")
                            }
                        } else {
                            items(data.documents!!) { doc ->
                                Card(modifier = Modifier.fillMaxWidth()) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text(text = "Type: ${doc.doc_type}", style = MaterialTheme.typography.labelLarge)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        AsyncImage(
                                            model = "http://10.246.90.233/boxing_api/${doc.file_path}",
                                            contentDescription = doc.doc_type,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(250.dp),
                                            contentScale = ContentScale.Fit
                                        )
                                    }
                                }
                            }
                        }
                        
                        item {
                            Button(
                                onClick = { navController.popBackStack() },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Back")
                            }
                        }
                    }
                }
            }
        }
    }
}
