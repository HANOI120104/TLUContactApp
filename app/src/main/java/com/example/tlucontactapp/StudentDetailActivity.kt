package com.example.tlucontactapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.tlucontactapp.databinding.ActivityStudentDetailBinding
import com.google.firebase.firestore.FirebaseFirestore

class StudentDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStudentDetailBinding
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val studentId = intent.getStringExtra("STUDENT_ID")
        if (studentId != null) {
            loadStudentDetails(studentId)
        }
    }

    private fun loadStudentDetails(studentId: String) {
        db.collection("students").document(studentId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    binding.tvStudentName.text = document.getString("name")
                    binding.tvStudentPhone.text = document.getString("phone")
                    binding.tvStudentEmail.text = document.getString("email")
                    binding.tvStudentAddress.text = document.getString("address")
                }
            }
    }
}
