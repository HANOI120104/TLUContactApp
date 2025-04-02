package com.example.tlucontactapp

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EditProfileActivity : AppCompatActivity() {
    private lateinit var etFullName: EditText
    private lateinit var etPhone: EditText
    private lateinit var etEmail: EditText
    private lateinit var btnSave: Button
    private val db = FirebaseFirestore.getInstance()
    private val user = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        etFullName = findViewById(R.id.etFullName)
        etPhone = findViewById(R.id.etPhone)
        etEmail = findViewById(R.id.etEmail)
        btnSave = findViewById(R.id.btnSave)

        loadUserData()

        btnSave.setOnClickListener {
            updateUserData()
        }
    }

    private fun loadUserData() {
        user?.let {
            db.collection("users").document(it.uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        etFullName.setText(document.getString("fullName"))
                        etPhone.setText(document.getString("phone"))
                        etEmail.setText(document.getString("email"))
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Lỗi khi tải dữ liệu", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun updateUserData() {
        val fullName = etFullName.text.toString().trim()
        val phone = etPhone.text.toString().trim()
        val email = etEmail.text.toString().trim()

        if (fullName.isEmpty() || phone.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
            return
        }

        user?.let {
            db.collection("users").document(it.uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val role = document.getString("role") ?: ""
                        val collection = if (role == "CBGV") "staffs" else "students"

                        val userData = mapOf(
                            "fullName" to fullName,
                            "phone" to phone,
                            "email" to email
                        )

                        db.collection(collection).document(it.uid).set(userData)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show()
                                setResult(Activity.RESULT_OK)
                                finish()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Lỗi khi cập nhật dữ liệu", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
        }
    }
}
