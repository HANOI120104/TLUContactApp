package com.example.tlucontactapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tlucontactapp.adapters.DepartmentAdapter
import com.example.tlucontactapp.models.Department
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class DepartmentContactActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var btnAddDepartment: Button
    private lateinit var departmentAdapter: DepartmentAdapter
    private val db = FirebaseFirestore.getInstance()
    private val departmentAdapterList = mutableListOf<Department>()
    private var firestoreListener: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_department_contact)

        recyclerView = findViewById(R.id.recyclerViewUnits)
        btnAddDepartment = findViewById(R.id.btnAddDepartment)

        recyclerView.layoutManager = LinearLayoutManager(this)
        departmentAdapter = DepartmentAdapter(departmentAdapterList) { selectedDepartment ->
            val intent = Intent(this, DepartmentDetailActivity::class.java)
            intent.putExtra("DEPARTMENT_ID", selectedDepartment.departmentId)
            startActivity(intent)
        }
        recyclerView.adapter = departmentAdapter

        // Kiểm tra quyền truy cập và hiển thị nút "Thêm đơn vị"
        checkUserRoleAndUpdateUI()

        // Tự động cập nhật danh sách khi có thay đổi
        listenForDepartmentChanges()

        // Xử lý sự kiện thêm đơn vị
        btnAddDepartment.setOnClickListener {
            val intent = Intent(this, AddEditDepartmentActivity::class.java)
            startActivity(intent)
        }
    }

    private fun checkUserRoleAndUpdateUI() {
        val user = FirebaseAuth.getInstance().currentUser
        val userId = user?.uid

        val userRef = FirebaseFirestore.getInstance().collection("users").document(userId!!)
        userRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val role = document.getString("role")
                if (role == "CBGV") {
                    btnAddDepartment.visibility = View.VISIBLE // Hiển thị nút nếu là giảng viên
                } else {
                    btnAddDepartment.visibility = View.GONE // Ẩn nút nếu là sinh viên
                }
            }
        }
    }

    private fun listenForDepartmentChanges() {
        firestoreListener = db.collection("departments")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.e("FirestoreError", "Lỗi khi cập nhật dữ liệu", e)
                    return@addSnapshotListener
                }
                if (snapshots != null) {
                    val newList = mutableListOf<Department>()
                    for (document in snapshots.documents) {
                        val department = document.toObject(Department::class.java)?.copy(departmentId = document.id)
                        if (department != null) newList.add(department)
                    }
                    departmentAdapter.updateList(newList)
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        firestoreListener?.remove() // Ngừng lắng nghe khi đóng Activity
    }
}

