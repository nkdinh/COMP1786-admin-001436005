package com.example.universalyoga

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ClassListAdapter(private val classList: List<Map<String, String>>) :
    RecyclerView.Adapter<ClassListAdapter.ClassViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_class, parent, false)
        return ClassViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClassViewHolder, position: Int) {
        val classInstance = classList[position]
        holder.bind(classInstance)
    }

    override fun getItemCount(): Int = classList.size

    class ClassViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val teacherNameText: TextView = itemView.findViewById(R.id.teacherName)
        private val startDateText: TextView = itemView.findViewById(R.id.startDate)

        fun bind(classInstance: Map<String, String>) {
            teacherNameText.text = "Teacher: ${classInstance["Teacher"]}"
            startDateText.text = "Start date: ${classInstance["date"]}"
        }
    }
}
