package com.example.tlucontactapp.models

data class Student(
    val fullName: String = "",
    val phone: String = "",
    val email: String = "",
    val studentId: String = "",
    val classId: String = "",
    val photoURL: String = ""
) {
    constructor() : this("", "", "", "", "", "")
}
