package com.example.tlucontactapp

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class AddEditDepartmentActivity : AppCompatActivity() {
    private lateinit var etDepartmentName: EditText
    private lateinit var etDepartmentPhone: EditText
    private lateinit var etDepartmentEmail: EditText
    private lateinit var etDepartmentAddress: EditText
    private lateinit var btnSave: Button
    private lateinit var btnUpdate: Button
    private lateinit var btnDelete: Button
    private val db = FirebaseFirestore.getInstance()
    private var departmentId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_department)

        etDepartmentName = findViewById(R.id.etDepartmentName)
        etDepartmentPhone = findViewById(R.id.etDepartmentPhone)
        etDepartmentEmail = findViewById(R.id.etDepartmentEmail)
        etDepartmentAddress = findViewById(R.id.etDepartmentAddress)
        btnSave = findViewById(R.id.btnSaveDepartment)
        btnUpdate = findViewById(R.id.btnUpdateDepartment)
        btnDelete = findViewById(R.id.btnDeleteDepartment)

        departmentId = intent.getStringExtra("DEPARTMENT_ID")
        if (departmentId != null) {
            loadDepartmentData()
        }

        btnSave.setOnClickListener { saveDepartment() }
        btnUpdate.setOnClickListener { updateDepartment() }
        btnDelete.setOnClickListener { deleteDepartment() }
    }

    private fun loadDepartmentData() {
        db.collection("departments").document(departmentId!!).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    etDepartmentName.setText(document.getString("departmentName"))
                    etDepartmentPhone.setText(document.getString("departmentPhone"))
                    etDepartmentEmail.setText(document.getString("departmentEmail"))
                    etDepartmentAddress.setText(document.getString("departmentAddress"))
                }
            }
    }

    private fun saveDepartment() {
        val departmentData = mapOf(
            "departmentName" to etDepartmentName.text.toString().trim(),
            "departmentPhone" to etDepartmentPhone.text.toString().trim(),
            "departmentEmail" to etDepartmentEmail.text.toString().trim(),
            "departmentAddress" to etDepartmentAddress.text.toString().trim()
        )

        db.collection("departments").add(departmentData)
            .addOnSuccessListener {
                Toast.makeText(this, "Thêm đơn vị thành công", Toast.LENGTH_SHORT).show()
                setResult(Activity.RESULT_OK)
                finish()
            }
            .addOnFailureListener { e ->
                // Log lỗi
                Log.e("FirestoreError", "Lỗi khi thêm dữ liệu vào Firestore", e)

                // Thông báo lỗi cho người dùng
                Toast.makeText(this, "Lỗi khi thêm đơn vị: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }


    private fun updateDepartment() {
        if (departmentId == null) {
            Toast.makeText(this, "Không có đơn vị để cập nhật", Toast.LENGTH_SHORT).show()
            return
        }

        val departmentData = mapOf(
            "departmentName" to etDepartmentName.text.toString().trim(),
            "departmentPhone" to etDepartmentPhone.text.toString().trim(),
            "departmentEmail" to etDepartmentEmail.text.toString().trim(),
            "departmentAddress" to etDepartmentAddress.text.toString().trim()
        )

        db.collection("departments").document(departmentId!!).set(departmentData)
            .addOnSuccessListener {
                Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show()
                setResult(Activity.RESULT_OK)
                finish()
            }
    }

    private fun deleteDepartment() {
        if (departmentId == null) {
            Toast.makeText(this, "Không có đơn vị để xóa", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("departments").document(departmentId!!).delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Xóa thành công", Toast.LENGTH_SHORT).show()
                setResult(Activity.RESULT_OK)
                finish()
            }
    }
}
