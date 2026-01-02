package com.example.athleteregistrationapp.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.athleteregistrationapp.network.RetrofitClient
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

object FileUploadUtils {

    fun uploadDocumentToServer(
        context: Context,
        fileUri: Uri,
        docType: String,
        athleteId: String
    ) {
        try {
            val inputStream = context.contentResolver.openInputStream(fileUri) ?: return
            
            // Create a temporary file to upload
            val tempFile = File(context.cacheDir, "upload_${System.currentTimeMillis()}.jpg")
            FileOutputStream(tempFile).use { output ->
                inputStream.copyTo(output)
            }

            // Create RequestBody for the file with correct media type
            val requestFile = tempFile.asRequestBody("image/*".toMediaTypeOrNull())
            
            // The name "file" must match $_FILES['file'] in your PHP
            val filePart = MultipartBody.Part.createFormData("file", tempFile.name, requestFile)

            RetrofitClient.api.uploadDocument(
                athleteId,
                docType,
                filePart
            ).enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.isSuccessful) {
                        Log.d("UPLOAD_SUCCESS", "Server says: ${response.body()}")
                    } else {
                        Log.e("UPLOAD_ERROR", "Server returned: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.e("UPLOAD_ERROR", "Network failure: ${t.message}")
                }
            })
        } catch (e: Exception) {
            Log.e("UPLOAD_EXCEPTION", "Error: ${e.message}")
        }
    }
}
