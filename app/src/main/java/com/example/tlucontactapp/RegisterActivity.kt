package com.example.tlucontactapp

import android.app.ProgressDialog
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
    private lateinit var etClassId: EditText
    private lateinit var etPhone: EditText
    private lateinit var btnRegister: Button
    private lateinit var btnCheckVerification: Button
    private lateinit var progressDialog: ProgressDialog
    private lateinit var btnBack: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etFullName = findViewById(R.id.etFullName)
        etCode = findViewById(R.id.etCode)
        etClassId = findViewById(R.id.etClass)
        etPhone = findViewById(R.id.etPhone)
        btnRegister = findViewById(R.id.btnRegister)
        btnCheckVerification = findViewById(R.id.btnCheckVerification)
        btnBack = findViewById(R.id.btnBack)

        progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)
        progressDialog.setMessage("Đang xử lý...")

        btnRegister.setOnClickListener { registerUser() }
        btnCheckVerification.setOnClickListener { checkEmailVerification() }
        btnBack.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun registerUser() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val fullName = etFullName.text.toString().trim()
        val userCode = etCode.text.toString().trim()
        val classId = etClassId.text.toString().trim()
        val phone = etPhone.text.toString().trim()

        if (email.isEmpty() || password.isEmpty() || fullName.isEmpty() || userCode.isEmpty() || classId.isEmpty() || phone.isEmpty()) {
            showToast("Vui lòng nhập đầy đủ thông tin!")
            return
        }

        if (!email.endsWith("@tlu.edu.vn") && !email.endsWith("@e.tlu.edu.vn") && !email.endsWith("@gmail.com")) {
            showToast("Bạn phải sử dụng email của trường hoặc Gmail!")
            return
        }

        if (password.length < 6) {
            showToast("Mật khẩu phải có ít nhất 6 ký tự!")
            return
        }

        progressDialog.show()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                val user = authResult.user ?: return@addOnSuccessListener

                user.sendEmailVerification().addOnSuccessListener {
                    progressDialog.dismiss()
                    showToast("Mã xác thực đã gửi! Kiểm tra email rồi nhấn 'Kiểm tra xác thực'.")
                }.addOnFailureListener {
                    progressDialog.dismiss()
                    showToast("Lỗi khi gửi email xác thực: ${it.message}")
                }
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                showToast("Lỗi khi đăng ký: ${e.message}")
            }
    }

    private fun checkEmailVerification() {
        val user = auth.currentUser
        if (user == null) {
            showToast("Không tìm thấy tài khoản!")
            return
        }

        progressDialog.show()

        user.reload().addOnSuccessListener {
            if (user.isEmailVerified) {
                saveUserToFirestore(
                    user.uid,
                    etFullName.text.toString(),
                    etEmail.text.toString(),
                    etCode.text.toString(),
                    etPhone.text.toString(),
                    etClassId.text.toString()
                )
            } else {
                progressDialog.dismiss()
                showToast("Email chưa được xác thực!")
            }
        }.addOnFailureListener {
            progressDialog.dismiss()
            showToast("Lỗi khi kiểm tra xác thực: ${it.message}")
        }
    }

    private fun saveUserToFirestore(userId: String, fullName: String, email: String, userCode: String, phone: String, classId: String) {
        val role = when {
            email.endsWith("@tlu.edu.vn") || email.endsWith("@gmail.com") -> "CBGV"
            email.endsWith("@e.tlu.edu.vn") -> "SV"
            else -> "UNKNOWN"
        }

        val userData = hashMapOf(
            "fullName" to fullName,
            "email" to email,
            "userCode" to userCode,
            "role" to role,
            "phone" to phone,
            "classId" to classId,
            "isVerified" to true
        )

        db.collection("users").document(userId).set(userData)
            .addOnSuccessListener {
                if (role == "SV") {
                    addStudentContact(userId, fullName, email, userCode, role, phone, classId)
                } else {
                    addStaffContact(userId, fullName, email, userCode, role, phone, classId)
                }
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                showToast("Lỗi khi lưu thông tin: ${e.message}")
            }
    }

    private fun addStudentContact(userId: String, fullName: String, email: String, userCode: String, role: String, phone: String, classId: String) {
        val contactData = hashMapOf(
            "fullName" to fullName,
            "email" to email,
            "userCode" to userCode,
            "phone" to phone,
            "address" to "",
            "classId" to classId,
            "userId" to userId,
            "role" to role
        )

        db.collection("students").document(userId).set(contactData)
            .addOnSuccessListener {
                progressDialog.dismiss()
                showToast("Tạo tài khoản sinh viên thành công!")
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                showToast("Lỗi khi lưu sinh viên: ${e.message}")
            }
    }

    private fun addStaffContact(userId: String, fullName: String, email: String, userCode: String, role: String, phone: String, classId: String) {
        val contactData = hashMapOf(
            "fullName" to fullName,
            "email" to email,
            "userCode" to userCode,
            "phone" to phone,
            "address" to "",
            "classId" to classId,
            "userId" to userId,
            "role" to role
        )

        db.collection("staffs").document(userId).set(contactData)
            .addOnSuccessListener {
                progressDialog.dismiss()
                showToast("Tạo tài khoản cán bộ/giảng viên thành công!")
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                showToast("Lỗi khi lưu cán bộ/giảng viên: ${e.message}")
            }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
