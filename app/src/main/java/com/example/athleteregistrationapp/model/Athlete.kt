package com.example.athleteregistrationapp.model

data class Athlete(
    val id: String?,
    val fullname: String?,
    val dob: String?,
    val weight_class: String?,
    val club: String?,
    val medical_info: String?,
    val documents: List<Document>? = emptyList()
)

data class Document(
    val doc_type: String,
    val file_path: String // This should be the full URL or relative path to the image
)
