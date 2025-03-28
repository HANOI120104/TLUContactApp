package com.example.tlucontactapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.tlucontactapp.databinding.ActivityStaffDetailBinding
import com.google.firebase.firestore.FirebaseFirestore

class StaffDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStaffDetailBinding
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStaffDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val staffId = intent.getStringExtra("STAFF_ID")
        if (staffId != null) {
            loadStaffDetails(staffId)
        }
    }

    private fun loadStaffDetails(staffId: String) {
        db.collection("staffs").document(staffId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    binding.tvStaffName.text = document.getString("fullName")
                    binding.tvStaffPhone.text = document.getString("phone")
                    binding.tvStaffEmail.text = document.getString("email")
                    binding.tvStaffPosition.text = document.getString("position")
                    binding.tvStaffDepartment.text = document.getString("department")

                }
            }
    }
}
