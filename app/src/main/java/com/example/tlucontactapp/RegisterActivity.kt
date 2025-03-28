package com.example.tlucontactapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etFullName: EditText
    private lateinit var btnRegister: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etFullName = findViewById(R.id.etFullName)
        btnRegister = findViewById(R.id.btnRegister)

        btnRegister.setOnClickListener { registerUser() }
    }

    private fun registerUser() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val fullName = etFullName.text.toString().trim()

        if (email.isEmpty() || password.isEmpty() || fullName.isEmpty()) {
            showToast("Vui lòng nhập đầy đủ thông tin!")
            return
        }

        if (!email.endsWith("@e.tlu.edu.vn") && !email.endsWith("@e.tlu.edu.vn")) {
            showToast("Bạn phải sử dụng email của trường học!")
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                val user = authResult.user ?: return@addOnSuccessListener
                user.sendEmailVerification().addOnSuccessListener {
                    showToast("Mã xác thực đã gửi đến email. Vui lòng kiểm tra hộp thư!")
                }
                saveUserToFirestore(user.uid, fullName, email)
            }
            .addOnFailureListener { e ->
                showToast("Lỗi khi đăng ký: ${e.message}")
            }
    }

    private fun saveUserToFirestore(userId: String, fullName: String, email: String) {
        val role = if (email.endsWith("@e.tlu.edu.vn")) "CBGV" else "SV"
        val userData = hashMapOf(
            "fullName" to fullName,
            "email" to email,
            "role" to role,
            "isVerified" to false
        )

        db.collection("users").document(userId).set(userData)
            .addOnSuccessListener {
                showToast("Đăng ký thành công! Vui lòng xác thực email trước khi đăng nhập.")
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
            .addOnFailureListener { e ->
                showToast("Lỗi khi lưu thông tin người dùng: ${e.message}")
            }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}