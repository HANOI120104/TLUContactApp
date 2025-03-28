package com.example.tlucontactapp.models

data class Department(
    var departmentId: String = "",
    var departmentName: String = "",
    var departmentPhone: String = "",
    var departmentEmail: String = "",
    var departmentAddress: String = "",
    var logoURL: String = ""
)
{
    constructor() : this("", "", "", "", "", "")
}
