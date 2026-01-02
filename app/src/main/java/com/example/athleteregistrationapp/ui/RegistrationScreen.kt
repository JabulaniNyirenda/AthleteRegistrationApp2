package com.example.athleteregistrationapp.ui

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.athleteregistrationapp.network.RetrofitClient
import com.example.athleteregistrationapp.utils.FileUploadUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun RegistrationScreen(navController: NavController) {

    val context = LocalContext.current

    // ---------- FORM STATE ----------
    var fullname by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }
    var weightClass by remember { mutableStateOf("") }
    var club by remember { mutableStateOf("") }
    var medicalInfo by remember { mutableStateOf("") }

    var athleteId by remember { mutableStateOf<String?>(null) }
    var isRegistering by remember { mutableStateOf(false) }

    // ---------- FILE STATES ----------
    var idUri by remember { mutableStateOf<Uri?>(null) }
    var medicalUri by remember { mutableStateOf<Uri?>(null) }
    var photoUri by remember { mutableStateOf<Uri?>(null) }

    // ---------- FILE PICKER ----------
    val filePicker =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                when {
                    idUri == null -> idUri = it
                    medicalUri == null -> medicalUri = it
                    else -> photoUri = it
                }
            }
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(
            text = "Athlete Registration",
            style = MaterialTheme.typography.headlineMedium
        )

        // ---------- INPUT FIELDS ----------
        OutlinedTextField(
            value = fullname,
            onValueChange = { fullname = it },
            label = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = dob,
            onValueChange = { dob = it },
            label = { Text("Date of Birth (YYYY-MM-DD)") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = weightClass,
            onValueChange = { weightClass = it },
            label = { Text("Weight Class") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = club,
            onValueChange = { club = it },
            label = { Text("Club") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = medicalInfo,
            onValueChange = { medicalInfo = it },
            label = { Text("Medical Info") },
            modifier = Modifier.fillMaxWidth()
        )

        HorizontalDivider()

        // ---------- DOCUMENT UPLOAD ----------
        Text("Required Documents", style = MaterialTheme.typography.titleMedium)

        Button(
            onClick = { filePicker.launch("*/*") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (idUri == null) "Upload National ID" else "ID Selected ✅")
        }

        Button(
            onClick = { filePicker.launch("*/*") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (medicalUri == null) "Upload Medical Certificate" else "Medical Selected ✅")
        }

        Button(
            onClick = { filePicker.launch("image/*") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (photoUri == null) "Upload Passport Photo" else "Photo Selected ✅")
        }

        HorizontalDivider()

        // ---------- REGISTER ATHLETE ----------
        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = !isRegistering,
            onClick = {
                if (fullname.isBlank() || dob.isBlank()) {
                    Toast.makeText(context, "Please fill required fields", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                isRegistering = true
                RetrofitClient.api.registerAthlete(
                    fullname,
                    dob,
                    weightClass,
                    club,
                    medicalInfo
                ).enqueue(object : Callback<String> {

                    override fun onResponse(call: Call<String>, response: Response<String>) {
                        isRegistering = false
                        val responseBody = response.body() ?: ""
                        
                        if (response.isSuccessful && responseBody.isNotEmpty()) {
                            // Extract ID if response is like "Success: 123" or just "123"
                            athleteId = responseBody.filter { it.isDigit() }
                            Toast.makeText(context, "Athlete Registered Successfully!", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(context, "Registration Failed: $responseBody", Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onFailure(call: Call<String>, t: Throwable) {
                        isRegistering = false
                        Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_LONG).show()
                    }
                })
            }
        ) {
            Text(if (isRegistering) "Registering..." else "Register Athlete")
        }

        // ---------- UPLOAD DOCUMENTS ----------
        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = athleteId != null,
            onClick = {
                athleteId?.let { id ->
                    idUri?.let { FileUploadUtils.uploadDocumentToServer(context, it, "ID", id) }
                    medicalUri?.let { FileUploadUtils.uploadDocumentToServer(context, it, "MEDICAL_CERT", id) }
                    photoUri?.let { FileUploadUtils.uploadDocumentToServer(context, it, "PHOTO", id) }
                    
                    Toast.makeText(context, "Documents sent to server", Toast.LENGTH_SHORT).show()
                }
            }
        ) {
            Text("Upload Documents")
        }

        // ---------- VIEW PROFILE ----------
        if (athleteId != null) {
            Button(
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                onClick = {
                    navController.navigate("details/$athleteId")
                }
            ) {
                Text("View Athlete Profile (File)")
            }
        }
    }
}
