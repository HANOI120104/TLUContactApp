package com.example.tlucontactapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.tlucontactapp.databinding.ActivityDepartmentDetailBinding
import com.google.firebase.firestore.FirebaseFirestore

class DepartmentDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDepartmentDetailBinding
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDepartmentDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val departmentId = intent.getStringExtra("DEPARTMENT_ID")
        if (departmentId != null) {
            loadStaffDetails(departmentId)
        }
    }

    private fun loadStaffDetails(departmentIdId: String) {
        db.collection("departments").document(departmentIdId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    binding.tvDepartmentName.text = document.getString("departmentName")
                    binding.tvDepartmentAddress.text = document.getString("departmentAddress")
                    binding.tvDepartmentPhone.text = document.getString("departmentPhone")
                    binding.tvDepartmentEmail.text = document.getString("departmentEmail")
                }
            }
    }


}
