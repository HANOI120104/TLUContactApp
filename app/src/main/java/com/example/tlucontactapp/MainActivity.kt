package com.example.tlucontactapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var tvWelcome: TextView
    private lateinit var btnUnitContacts: Button
    private lateinit var btnStaffContacts: Button
    private lateinit var btnStudentContacts: Button

    private var userRole: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        tvWelcome = findViewById(R.id.tvWelcome)
        btnUnitContacts = findViewById(R.id.btnUnitContacts)
        btnStaffContacts = findViewById(R.id.btnStaffContacts)
        btnStudentContacts = findViewById(R.id.btnStudentContacts)

        loadUserInfo()

        btnUnitContacts.setOnClickListener {
            startActivity(Intent(this, UnitContactActivity::class.java))
        }

        btnStaffContacts.setOnClickListener {
            if (userRole == "CBGV") {
                startActivity(Intent(this, StaffContactActivity::class.java))
            } else {
                Toast.makeText(this, "Bạn không có quyền xem danh bạ CBGV!", Toast.LENGTH_SHORT).show()
            }
        }

        btnStudentContacts.setOnClickListener {
            startActivity(Intent(this, StudentContactActivity::class.java))
        }
    }

    private fun loadUserInfo() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val fullName = document.getString("fullName") ?: "Người dùng"
                    userRole = document.getString("role")

                    tvWelcome.text = "Chào, $fullName!"
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Lỗi khi tải thông tin người dùng!", Toast.LENGTH_SHORT).show()
            }
    }
}
