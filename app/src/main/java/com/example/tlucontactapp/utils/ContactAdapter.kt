package com.example.tlucontactapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tlucontactapp.R
import com.example.tlucontactapp.models.Contact

class ContactAdapter(
    private var contactList: List<Contact>,
    private val onItemClick: (Contact) -> Unit  // Thêm hàm callback
) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvContactName)
        val tvPhone: TextView = itemView.findViewById(R.id.tvContactPhone)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contact, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contactList[position]
        holder.tvName.text = contact.name
        holder.tvPhone.text = contact.phone

        // Bắt sự kiện khi click vào item
        holder.itemView.setOnClickListener { onItemClick(contact) }
    }
    fun updateList(newList: List<Contact>) {
        contactList = newList
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = contactList.size
}
