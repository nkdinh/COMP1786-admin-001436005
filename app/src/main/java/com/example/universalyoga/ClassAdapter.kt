package com.example.universalyoga

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ClassAdapter(
    private val yogaClasses: List<String>,
    private val onItemClick: (Int) -> Unit) : RecyclerView
        .Adapter<ClassAdapter.YogaClassViewHolder>() {

    override fun getItemCount(): Int {
        return yogaClasses.size
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YogaClassViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)
        return YogaClassViewHolder(view)
    }

    override fun onBindViewHolder(holder: YogaClassViewHolder, position: Int) {
        holder.bind(yogaClasses[position])

        holder.itemView.setOnClickListener {
            onItemClick(position)
        }
    }

    class YogaClassViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(android.R.id.text1)

        fun bind(text: String) {
            textView.text = text
        }
    }
}
