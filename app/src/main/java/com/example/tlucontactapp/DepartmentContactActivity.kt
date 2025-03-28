package com.example.tlucontactapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tlucontactapp.adapters.StaffAdapter
import com.example.tlucontactapp.adapters.DepartmentAdapter
import com.example.tlucontactapp.models.Department
import com.google.firebase.firestore.FirebaseFirestore

class DepartmentContactActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var departmentAdapter: DepartmentAdapter
    private val db = FirebaseFirestore.getInstance()
    private val departmentAdapterList = mutableListOf<Department>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_department_contact)

        recyclerView = findViewById(R.id.recyclerViewUnits)
        recyclerView.layoutManager = LinearLayoutManager(this)


        departmentAdapter = DepartmentAdapter(departmentAdapterList) { selectedDepartment ->
            val intent = Intent(this, DepartmentDetailActivity::class.java)
            intent.putExtra("DEPARTMENT_ID", selectedDepartment.departmentId) // Truyền ID để xem chi tiết
            startActivity(intent)
        }
        recyclerView.adapter = departmentAdapter

        loadDepartmentContacts()
    }

    private fun loadDepartmentContacts() {
        db.collection("departments")
            .get()
            .addOnSuccessListener { result ->
                val newList = mutableListOf<Department>()
                for (document in result) {
                    val department = document.toObject(Department::class.java).copy(departmentId = document.id)
                    newList.add(department)
                }
                departmentAdapter.updateList(newList)
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreError", "Lỗi khi lấy dữ liệu", e)
            }
    }
}
