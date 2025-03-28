package com.example.tlucontactapp.models

data class Staff(
    val staffId: String = "",
    val fullName: String = "",
    val phone: String = "",
    val email: String = "",
    val position: String = "",
    val department: String = "",
    val photoURL: String = ""  // ✅ Thêm ảnh đại diện
){
    constructor() : this("", "", "", "", "", "", "")
}