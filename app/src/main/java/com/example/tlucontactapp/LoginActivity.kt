package com.example.tlucontactapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnRegister: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        btnRegister = findViewById(R.id.btnRegister)

        btnLogin.setOnClickListener { loginUser() }
        btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun loginUser() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            showToast("Vui lòng nhập email và mật khẩu!")
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                val user = authResult.user ?: return@addOnSuccessListener
                if (!user.isEmailVerified) {
                    showToast("Email chưa được xác thực. Vui lòng kiểm tra hộp thư.")
                    return@addOnSuccessListener
                }
                fetchUserRole(user.uid)
            }
            .addOnFailureListener { e ->
                showToast("Lỗi đăng nhập: ${e.message}")
            }
    }

    private fun fetchUserRole(userId: String) {
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val role = document.getString("role") ?: "SV"
                    navigateToHome(role)
                } else {
                    showToast("Tài khoản không tồn tại trong hệ thống!")
                }
            }
            .addOnFailureListener { e ->
                showToast("Lỗi khi lấy dữ liệu người dùng: ${e.message}")
            }
    }

    private fun navigateToHome(role: String) {
        val intent = if (role == "CBGV") {
            Intent(this, StaffContactActivity::class.java)
        } else {
            Intent(this, StudentContactActivity::class.java)
        }
        startActivity(intent)
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

