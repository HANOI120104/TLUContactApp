package com.example.tlucontactapp.models

data class Contact(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val address: String = "",
    val department: String = "",
    val position: String = "",
    val type: String = ""
) {
    // Constructor không tham số (cần thiết cho Firestore)
    constructor() : this("", "", "", "", "", "", "", "")
}
