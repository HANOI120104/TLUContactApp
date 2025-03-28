package com.example.tlucontactapp.firestore

import android.util.Log
import com.example.tlucontactapp.models.Contact
import com.google.firebase.firestore.FirebaseFirestore

class FirestoreHelper {

    private val db = FirebaseFirestore.getInstance()

    // Thêm contact mới
    fun addContact(contact: Contact, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("contacts")
            .add(contact)
            .addOnSuccessListener {
                Log.d("Firestore", "Thêm contact thành công: ${it.id}")
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Lỗi khi thêm contact", e)
                onFailure(e)
            }
    }

    // Cập nhật contact
    fun updateContact(contact: Contact, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        if (contact.id.isEmpty()) {
            onFailure(Exception("Contact ID không hợp lệ"))
            return
        }

        db.collection("contacts").document(contact.id)
            .set(contact)
            .addOnSuccessListener {
                Log.d("Firestore", "Cập nhật contact thành công")
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Lỗi khi cập nhật contact", e)
                onFailure(e)
            }
    }

    // Xóa contact
    fun deleteContact(contactId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("contacts").document(contactId)
            .delete()
            .addOnSuccessListener {
                Log.d("Firestore", "Xóa contact thành công")
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Lỗi khi xóa contact", e)
                onFailure(e)
            }
    }
}
