package com.example.tlucontactapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tlucontactapp.databinding.ActivityDepartmentDetailBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth

class DepartmentDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDepartmentDetailBinding
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDepartmentDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val departmentId = intent.getStringExtra("DEPARTMENT_ID")
        val user = FirebaseAuth.getInstance().currentUser

        if (departmentId != null) {
            loadStaffDetails(departmentId)

            // Kiểm tra vai trò của người dùng
            checkUserRole(user?.uid)
        }

        // Thiết lập sự kiện cho nút sửa
        binding.btnEditDepartment.setOnClickListener {
            val intent = Intent(this, AddEditDepartmentActivity::class.java)
            intent.putExtra("DEPARTMENT_ID", departmentId)
            startActivity(intent)
        }

        // Thiết lập sự kiện cho nút xóa
        binding.btnDeleteDepartment.setOnClickListener {
            departmentId?.let {
                deleteDepartment(it)
            }
        }
    }

    private fun loadStaffDetails(departmentId: String) {
        db.collection("departments").document(departmentId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    binding.tvDepartmentName.text = document.getString("departmentName")
                    binding.tvDepartmentAddress.text = document.getString("departmentAddress")
                    binding.tvDepartmentPhone.text = document.getString("departmentPhone")
                    binding.tvDepartmentEmail.text = document.getString("departmentEmail")
                }
            }
    }

    private fun checkUserRole(userId: String?) {
        if (userId != null) {
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val role = document.getString("role") // Lấy role của người dùng
                        if (role == "CBGV") {
                            // Hiển thị các nút sửa và xóa nếu là giảng viên
                            binding.btnEditDepartment.visibility = View.VISIBLE
                            binding.btnDeleteDepartment.visibility = View.VISIBLE
                        } else {
                            // Ẩn các nút sửa và xóa nếu là sinh viên
                            binding.btnEditDepartment.visibility = View.GONE
                            binding.btnDeleteDepartment.visibility = View.GONE
                        }
                    }
                }
        }
    }

    private fun deleteDepartment(departmentId: String) {
        db.collection("departments").document(departmentId).delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Đơn vị đã bị xóa", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Lỗi khi xóa đơn vị", Toast.LENGTH_SHORT).show()
            }
    }
}
