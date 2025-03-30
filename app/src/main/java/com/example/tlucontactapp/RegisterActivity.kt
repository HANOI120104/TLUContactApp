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
    private lateinit var etCode: EditText
    private lateinit var etClassId: EditText // Thêm trường này trong XML
    private lateinit var etPhone: EditText // Thêm trường này trong XML
    private lateinit var btnRegister: Button
    private lateinit var btnCheckVerification: Button // Nút kiểm tra xác thực

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etFullName = findViewById(R.id.etFullName)
        etCode = findViewById(R.id.etCode)
        etClassId = findViewById(R.id.etClass) // Thêm trường này trong XML
        etPhone = findViewById(R.id.etPhone) // Thêm trường này trong XML
        btnRegister = findViewById(R.id.btnRegister)
        btnCheckVerification = findViewById(R.id.btnCheckVerification) // Thêm nút này trong XML

        btnRegister.setOnClickListener { registerUser() }
        btnCheckVerification.setOnClickListener { checkEmailVerification() } // Kiểm tra xác thực
    }

    private fun registerUser() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val fullName = etFullName.text.toString().trim()
        val userCode = etCode.text.toString().trim()
        val classId = etClassId.text.toString().trim() // Lấy mã lớp
        val phone = etPhone.text.toString().trim() // Lấy số điện thoại

        // Kiểm tra thông tin đầu vào
        if (email.isEmpty() || password.isEmpty() || fullName.isEmpty() || userCode.isEmpty()) {
            showToast("Vui lòng nhập đầy đủ thông tin!")
            return
        }

        if (!email.endsWith("@tlu.edu.vn") && !email.endsWith("@e.tlu.edu.vn")) {
            showToast("Bạn phải sử dụng email của trường học (@tlu.edu.vn hoặc @e.tlu.edu.vn)!")
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                val user = authResult.user ?: return@addOnSuccessListener
                user.sendEmailVerification().addOnSuccessListener {
                    showToast("Mã xác thực đã gửi đến email. Kiểm tra hộp thư rồi nhấn 'Kiểm tra xác thực'.")
                }
            }
            .addOnFailureListener { e ->
                showToast("Lỗi khi đăng ký: ${e.message}")
            }
    }

    private fun checkEmailVerification() {
        val user = auth.currentUser
        user?.reload()?.addOnSuccessListener {
            if (user.isEmailVerified) {
                saveUserToFirestore(
                    user.uid,
                    etFullName.text.toString(),
                    etEmail.text.toString(),
                    etCode.text.toString(),
                    etPhone.text.toString(), // Lấy số điện thoại từ EditText
                    etClassId.text.toString() // Lấy mã lớp từ EditText
                )
            } else {
                showToast("Email chưa được xác thực. Vui lòng kiểm tra hộp thư!")
            }
        }?.addOnFailureListener { e ->
            showToast("Lỗi khi kiểm tra xác thực: ${e.message}")
        }
    }


    private fun saveUserToFirestore(userId: String, fullName: String, email: String, userCode: String, phone: String, classId: String) {
        val role = when {
            email.endsWith("@tlu.edu.vn") -> "CBGV"
            email.endsWith("@e.tlu.edu.vn") -> "SV"
            else -> "UNKNOWN"
        }

        val userData = hashMapOf(
            "fullName" to fullName,
            "email" to email,
            "userCode" to userCode,
            "role" to role,
            "phone" to phone, // Thêm số điện thoại
            "classId" to classId, // Thêm mã lớp
            "isVerified" to true
        )

        db.collection("users").document(userId).set(userData)
            .addOnSuccessListener {
                addStudentContact(userId, fullName, email, userCode, role, phone, classId) // Truyền đầy đủ thông tin
            }
            .addOnFailureListener { e ->
                showToast("Lỗi khi lưu thông tin người dùng: ${e.message}")
            }
    }


    private fun addStudentContact(userId: String, fullName: String, email: String, userCode: String, role: String, phone: String, classId: String) {
        val contactData = hashMapOf(
            "fullName" to fullName,
            "email" to email,
            "userCode" to userCode,
            "phone" to "", // Chưa có số điện thoại, user có thể cập nhật sau
            "address" to "", // Chưa có địa chỉ, user có thể cập nhật sau
            "classId" to classId,
            "userId" to userId,
            "role" to role
        )

        db.collection("students").document(userId).set(contactData)
            .addOnSuccessListener {
                showToast("Xác thực thành công! Tài khoản và liên lạc đã được lưu.")
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
            .addOnFailureListener { e ->
                showToast("Lỗi khi lưu liên lạc vào danh bạ: ${e.message}")
            }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
