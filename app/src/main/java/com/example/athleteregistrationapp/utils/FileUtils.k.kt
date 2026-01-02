package com.example.athleteregistrationapp.utils

import java.io.File
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody

fun prepareFilePart(
    partName: String,
    file: File
): MultipartBody.Part {

    val requestFile =
        file.asRequestBody("image/*".toMediaTypeOrNull())

    return MultipartBody.Part.createFormData(
        partName,
        file.name,
        requestFile
    )
}
