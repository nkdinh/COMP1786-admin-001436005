package com.example.universalyoga

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CourseAdapter(private var courseList: List<Map<String, String>>,
                    private val onItemClick: (Map<String, String>) -> Unit) :
    RecyclerView.Adapter<CourseAdapter.CourseViewHolder>() {

    class CourseViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val textViewCourseName: TextView =
            itemView.findViewById(R.id.textViewCourseName)
    }

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): CourseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.course_item, parent, false)
        return CourseViewHolder(view)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        val course = courseList[position]
        holder.textViewCourseName.text = course["name"]

        holder.itemView.setOnClickListener {
            onItemClick(course)
        }
    }

    fun updateData(newCourseList: List<Map<String, String>>) {
        val oldSize = courseList.size
        val newSize = newCourseList.size

        courseList = newCourseList

        if (newSize > oldSize) {
            notifyItemRangeInserted(oldSize, newSize - oldSize)
        } else if (newSize < oldSize) {
            notifyItemRangeRemoved(newSize, oldSize - newSize)
        } else {
            notifyItemRangeChanged(0, newSize)
        }
    }

    override fun getItemCount(): Int = courseList.size
}

