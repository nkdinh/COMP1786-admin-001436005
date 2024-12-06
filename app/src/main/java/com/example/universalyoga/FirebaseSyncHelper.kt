package com.example.universalyoga

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class FirebaseSyncHelper {
    private val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val yogaClassRef: DatabaseReference = firebaseDatabase.getReference("yoga_classes")
    private val InstructorRef: DatabaseReference = firebaseDatabase.getReference("Instructors")
    private val courseRef: DatabaseReference = firebaseDatabase.getReference("courses")

    // Sync yoga classes
    fun syncYogaClasses(dbHelper: DBHelper) {
        val yogaClasses = dbHelper.getAllClasses()
        for (yogaClass in yogaClasses) {
            val classId = yogaClass["id"].toString()
            yogaClassRef.child(classId).setValue(yogaClass)
        }
    }

    // Sync Instructors
    fun syncInstructors(dbHelper: DBHelper) {
        val Instructors = dbHelper.getAllInstructors()
        for (Instructor in Instructors) {
            val InstructorId = Instructor["id"].toString()
            InstructorRef.child(InstructorId).setValue(Instructor)
        }
    }

    // Sync courses
    fun syncCourses(dbHelper: DBHelper) {
        val courses = dbHelper.getAllCourses()
        for (course in courses) {
            val courseId = course["id"].toString()
            courseRef.child(courseId).setValue(course)
        }
    }
}