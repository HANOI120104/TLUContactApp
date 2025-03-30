package com.example.tlucontactapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
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

                // Kiểm tra xem email đã xác thực chưa
                if (!user.isEmailVerified) {
                    showToast("Email chưa được xác thực. Vui lòng kiểm tra hộp thư.")
                    return@addOnSuccessListener
                }

                // Lấy thông tin từ Firestore
                fetchUserRole(user.uid)
            }
            .addOnFailureListener { e ->
                when (e) {
                    is FirebaseAuthInvalidUserException -> showToast("Tài khoản không tồn tại!")
                    is FirebaseAuthInvalidCredentialsException -> showToast("Email hoặc mật khẩu không chính xác!")
                    else -> showToast("Lỗi đăng nhập: ${e.message}")
                }
            }
    }

    private fun fetchUserRole(userId: String) {
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val role = document.getString("role") ?: "SV"
                    val classId = document.getString("classId") ?: ""
                    navigateToHome(role, classId)
                } else {
                    showToast("Tài khoản chưa được đăng ký trong hệ thống!")
                }
            }
            .addOnFailureListener { e ->
                showToast("Lỗi khi lấy dữ liệu người dùng: ${e.message}")
            }
    }

    private fun navigateToHome(role: String, classId: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("USER_ROLE", role)
            putExtra("CLASS_ID", classId)
        }
        startActivity(intent)
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
