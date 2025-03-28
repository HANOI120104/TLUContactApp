package com.example.tlucontactapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tlucontactapp.adapters.StudentAdapter
import com.example.tlucontactapp.models.Student
import com.google.firebase.firestore.FirebaseFirestore

class StudentContactActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var studentAdapter: StudentAdapter
    private val db = FirebaseFirestore.getInstance()
    private val studentList = mutableListOf<Student>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_contact)

        recyclerView = findViewById(R.id.recyclerViewContacts)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Khởi tạo Adapter cho danh sách sinh viên
        studentAdapter = StudentAdapter(studentList) { selectedStudent ->
            val intent = Intent(this, StudentDetailActivity::class.java) // Đổi ContactDetailActivity thành DetailActivity
            intent.putExtra("STUDENT_ID", selectedStudent.studentId) // Truyền ID để xem chi tiết
            startActivity(intent)
        }

        recyclerView.adapter = studentAdapter

        loadStudentContacts()
    }

    private fun loadStudentContacts() {
        db.collection("students") // Lấy dữ liệu từ collection "students"
            .get()
            .addOnSuccessListener { result ->
                val newList = mutableListOf<Student>()
                for (document in result) {
                    val student = document.toObject(Student::class.java).copy(studentId = document.id)
                    Log.d("FirestoreData", "Student: ${student.studentId} - ${student.fullName} - ${student.phone}")
                    newList.add(student)
                }
                studentAdapter.updateList(newList) // Cập nhật danh sách mới
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreError", "Lỗi khi lấy dữ liệu", e)
            }
    }
}
