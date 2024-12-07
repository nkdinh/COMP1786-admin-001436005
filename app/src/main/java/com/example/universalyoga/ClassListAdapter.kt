package com.example.universalyoga

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.Locale

class ClassListAdapter(private val classList: List<Map<String, String>>) :
    RecyclerView.Adapter<ClassListAdapter.ClassViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_class, parent, false)
        return ClassViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClassViewHolder, position: Int) {
        val classInstance = classList[position]
        holder.bind(holder.itemView.context, classInstance)
    }

    override fun getItemCount(): Int = classList.size

    class ClassViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val instructorNameText: TextView = itemView.findViewById(R.id.InstructorName)
        private val startDateText: TextView = itemView.findViewById(R.id.startDate)

        fun bind(context: Context, classInstance: Map<String, String>) {
            instructorNameText.text = String.format(
                Locale("vi", "VN"),
                context.getString(R.string.instructor_text),
                classInstance["instructor"]
            )
            startDateText.text = String.format(
                Locale("vi", "VN"),
                context.getString(R.string.start_date_text),
                classInstance["date"]
            )
        }
    }
}
