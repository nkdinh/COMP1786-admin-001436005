// Languages: Kotlin + Java
package com.example.universalyoga

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    // Defining table for classes and Instructors
    companion object {
        const val DATABASE_NAME = "YogaClass.db"
        const val DATABASE_VERSION = 13
        const val TABLE_NAME = "yoga_class"
        const val COLUMN_ID = "id"
        const val COLUMN_DAY = "day_of_week"
        const val COLUMN_TIME = "time_of_class"
        const val COLUMN_CAPACITY = "capacity"
        const val COLUMN_DURATION = "duration"
        const val COLUMN_PRICE = "price"
        const val COLUMN_TYPE = "class_type"
        const val COLUMN_DESCRIPTION = "description"
        const val COLUMN_INSTRUCTOR = "instructor"
        const val COLUMN_COMMENTS = "additional_comments"
        const val COLUMN_DATE = "date"
        const val COLUMN_NAME = "name"
        const val INSTRUCTOR_TABLE_NAME = "instructor"
        const val COLUMN_INSTRUCTOR_ID = "instructor_id"
        const val COLUMN_INSTRUCTOR_NAME = "instructor_name"
        const val COLUMN_INSTRUCTOR_EMAIL = "email"
        const val COLUMN_INSTRUCTOR_COMMENT = "comment_instructor"

        // Defining table for the courses
        const val COURSE_TABLE_NAME = "course"
        const val COLUMN_COURSE_ID = "course_id"
        const val COLUMN_COURSE_NAME = "name"
        const val COLUMN_COURSE_DEADLINE = "deadline"
        const val COLUMN_COURSE_COMMENT = "comment"
    }

 // onCreate method to create the tables
    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
        CREATE TABLE $TABLE_NAME (
            $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COLUMN_DAY TEXT,
            $COLUMN_TIME TEXT,
            $COLUMN_CAPACITY INTEGER,
            $COLUMN_DURATION INTEGER,
            $COLUMN_PRICE REAL,
            $COLUMN_TYPE TEXT,
            $COLUMN_DESCRIPTION TEXT,
            $COLUMN_INSTRUCTOR TEXT,
            $COLUMN_COMMENTS TEXT,
            $COLUMN_NAME TEXT,  
            $COLUMN_DATE TEXT
        )
    """
        db.execSQL(createTable)


        // Create the INSTRUCTOR table
        val createInstructorTable = ("CREATE TABLE $INSTRUCTOR_TABLE_NAME (" +
                "$COLUMN_INSTRUCTOR_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_INSTRUCTOR_NAME TEXT NOT NULL," +
                "$COLUMN_INSTRUCTOR_EMAIL TEXT," +
                "$COLUMN_INSTRUCTOR_COMMENT TEXT)")
        db.execSQL(createInstructorTable)

        // Create the course table
        val createCourseTable = """
        CREATE TABLE $COURSE_TABLE_NAME (
            $COLUMN_COURSE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COLUMN_COURSE_NAME TEXT NOT NULL,
            $COLUMN_COURSE_DEADLINE TEXT,
            $COLUMN_COURSE_COMMENT TEXT
        )
    """
        db.execSQL(createCourseTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < DATABASE_VERSION) {
            // If older versions < current version => drop table and re-create
            db.execSQL("DROP TABLE IF EXISTS $INSTRUCTOR_TABLE_NAME")
            val createInstructorTable = """
            CREATE TABLE $INSTRUCTOR_TABLE_NAME (
                $COLUMN_INSTRUCTOR_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_INSTRUCTOR_NAME TEXT NOT NULL,
                $COLUMN_INSTRUCTOR_EMAIL TEXT,
                $COLUMN_INSTRUCTOR_COMMENT TEXT
            )
        """
            db.execSQL(createInstructorTable)
        }

        if (oldVersion < DATABASE_VERSION) {
            // If the database is upgrading, delete all course data within
            db.execSQL("ALTER TABLE $TABLE_NAME ADD COLUMN $COLUMN_NAME TEXT")
            onCreate(db)  // Recall the onCreate method for re-making the tables
        }
    }


    // Add a new class
    fun insertClass(
        dayOfWeek: String,
        course: String,
        timeOfCourse: String,
        capacity: Int,
        duration: Int,
        price: Float,
        typeOfClass: String,
        instructor: String,
        description: String,
        additionalComments: String,
        date: String
    ): Long {
        val db = writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_DAY, dayOfWeek)
            put(COLUMN_TIME, timeOfCourse)
            put(COLUMN_CAPACITY, capacity)
            put(COLUMN_DURATION, duration)
            put(COLUMN_PRICE, price)
            put(COLUMN_TYPE, typeOfClass)
            put(COLUMN_INSTRUCTOR, instructor)
            put(COLUMN_DESCRIPTION, description)
            put(COLUMN_COMMENTS, additionalComments)
            put(COLUMN_DATE, date)
            put(COLUMN_NAME, course)
        }

        return db.insert(TABLE_NAME, null, contentValues)
    }

    // Updating a class from Database
    fun updateClass(classId: Long, contentValues: ContentValues): Int {
        val db = writableDatabase
        return db.update(TABLE_NAME, contentValues, "$COLUMN_ID = ?", arrayOf(classId.toString()))
    }

    // Removing a class from Database
    fun deleteClass(classId: Long): Int {
        val db = writableDatabase
        return db.delete(TABLE_NAME, "$COLUMN_ID = ?", arrayOf(classId.toString()))
    }



    // Retrieving the types of courses
    fun getAllTypes(): List<String> {
        val types = mutableListOf<String>()
        val db = readableDatabase
        val cursor: Cursor = db.query(true, TABLE_NAME, arrayOf(COLUMN_TYPE), null, null, null, null, null, null)

        while (cursor.moveToNext()) {
            val type = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE))
            if (!types.contains(type)) {
                types.add(type)
            }
        }
        cursor.close()
        return types
    }

    // Retrieving Instructors' data
    fun getInstructors(): List<String> {
        val instructors = mutableListOf<String>()
        val db = readableDatabase
        val cursor: Cursor = db.query(true, TABLE_NAME, arrayOf(COLUMN_INSTRUCTOR), null, null, null, null, null, null)

        while (cursor.moveToNext()) {
            val instructor = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INSTRUCTOR))
            if (!instructors.contains(instructor)) {
                instructors.add(instructor)
            }
        }
        cursor.close()
        return instructors
    }

    // Retrieving classes based on course types
    fun getClassesByType(type: String): List<Pair<Long, String>> {
        val classList = mutableListOf<Pair<Long, String>>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            arrayOf(COLUMN_ID, COLUMN_DAY, COLUMN_TIME),
            "$COLUMN_TYPE = ?",
            arrayOf(type),
            null,
            null,
            null
        )

        while (cursor.moveToNext()) {
            val classId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val day = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DAY))
            val time = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIME))
            val classDetails = "$day - $time"
            classList.add(Pair(classId, classDetails))
        }
        cursor.close()

        return classList
    }

    // Retrieving classes using Instructor's name
    fun getClassesByInstructor(instructor: String): List<Pair<Long, String>> {
        val classList = mutableListOf<Pair<Long, String>>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            arrayOf(COLUMN_ID, COLUMN_DAY, COLUMN_TIME),
            "$COLUMN_INSTRUCTOR = ?",
            arrayOf(instructor),
            null,
            null,
            null
        )

        while (cursor.moveToNext()) {
            val classId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val day = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DAY))
            val time = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIME))
            val classDetails = "$day - $time"
            classList.add(Pair(classId, classDetails))
        }
        cursor.close()

        return classList
    }

    // Retrieving classes based on the dates
    fun getClassesByDate(date: String): List<Pair<Long, String>> {
        val db = this.readableDatabase
        val query = "SELECT $COLUMN_ID, $COLUMN_NAME FROM $TABLE_NAME WHERE $COLUMN_DATE = ?"
        val cursor = db.rawQuery(query, arrayOf(date))
        val classes = mutableListOf<Pair<Long, String>>()

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val className = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
                classes.add(Pair(id, className))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return classes
    }

    // Insert a new Instructor
    fun insertInstructor(name: String, email: String, commentInstructor: String): Long {
        val db = writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_INSTRUCTOR_NAME, name)
            put(COLUMN_INSTRUCTOR_EMAIL, email)
            put(COLUMN_INSTRUCTOR_COMMENT, commentInstructor)
        }
        return db.insert(INSTRUCTOR_TABLE_NAME, null, contentValues)
    }

    // Search Instructors by Name
    fun searchInstructorByName(name: String): List<Map<String, String>> {
        val instructorList = mutableListOf<Map<String, String>>()
        val db = readableDatabase
        val cursor = db.query(
            INSTRUCTOR_TABLE_NAME,
            arrayOf(COLUMN_INSTRUCTOR_NAME, COLUMN_INSTRUCTOR_EMAIL, COLUMN_INSTRUCTOR_COMMENT),
            "$COLUMN_INSTRUCTOR_NAME LIKE ?",
            arrayOf("%$name%"),
            null,
            null,
            null
        )
        while (cursor.moveToNext()) {
            val instructorData = mapOf(
                "name" to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INSTRUCTOR_NAME)),
                "email" to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INSTRUCTOR_EMAIL)),
                "comment" to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INSTRUCTOR_COMMENT))
            )
            instructorList.add(instructorData)
        }
        cursor.close()
        return instructorList
    }
    // Retrieving Instructors' data
    fun getAllInstructors(): List<Map<String, Any>> {
        val instructorList = mutableListOf<Map<String, Any>>()
        val db = readableDatabase
        val cursor = db.query(
            INSTRUCTOR_TABLE_NAME,
            arrayOf(COLUMN_INSTRUCTOR_ID, COLUMN_INSTRUCTOR_NAME, COLUMN_INSTRUCTOR_EMAIL, COLUMN_INSTRUCTOR_COMMENT),
            null,
            null,
            null,
            null,
            null
        )
        while (cursor.moveToNext()) {
            val instructorData = mapOf(
                "id" to cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_INSTRUCTOR_ID)),
                "name" to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INSTRUCTOR_NAME)),
                "email" to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INSTRUCTOR_EMAIL)),
                "comment" to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INSTRUCTOR_COMMENT))
            )
            instructorList.add(instructorData)
        }
        cursor.close()
        return instructorList
    }


    fun getInstructorById(id: Long): Map<String, String>? {
        val db = readableDatabase
        val cursor = db.query(
            INSTRUCTOR_TABLE_NAME,
            arrayOf(COLUMN_INSTRUCTOR_NAME, COLUMN_INSTRUCTOR_EMAIL, COLUMN_INSTRUCTOR_COMMENT),
            "$COLUMN_INSTRUCTOR_ID = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )
        return if (cursor.moveToFirst()) {
            val instructorData = mapOf(
                "name" to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INSTRUCTOR_NAME)),
                "email" to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INSTRUCTOR_EMAIL)),
                "comment" to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INSTRUCTOR_COMMENT))
            )
            cursor.close()
            instructorData
        } else {
            cursor.close()
            null
        }
    }

    // Updating Instructors' data in the Database
    fun updateInstructor(id: Long, name: String, email: String, comment: String): Boolean {
        val db = writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_INSTRUCTOR_NAME, name)
            put(COLUMN_INSTRUCTOR_EMAIL, email)
            put(COLUMN_INSTRUCTOR_COMMENT, comment)
        }
        val rowsAffected = db.update(INSTRUCTOR_TABLE_NAME, contentValues, "$COLUMN_INSTRUCTOR_ID = ?", arrayOf(id.toString()))
        return rowsAffected > 0
    }

    // Deleting a Instructor from the Database
    fun deleteInstructor(id: Long): Boolean {
        val db = writableDatabase
        val rowsAffected = db.delete(INSTRUCTOR_TABLE_NAME, "$COLUMN_INSTRUCTOR_ID = ?", arrayOf(id.toString()))
        return rowsAffected > 0
    }

    // Adding a course to the Database
    fun insertCourse(name: String, deadline: String, comment: String): Long {
        val db = writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_COURSE_NAME, name)
            put(COLUMN_COURSE_DEADLINE, deadline)
            put(COLUMN_COURSE_COMMENT, comment)
        }
        return db.insert(COURSE_TABLE_NAME, null, contentValues)
    }

    // Retrieving courses from the Database
    fun getAllCourses(): List<Map<String, String>> {
        val courseList = mutableListOf<Map<String, String>>()
        val db = readableDatabase
        val cursor: Cursor = db.query(
            COURSE_TABLE_NAME,
            arrayOf(COLUMN_COURSE_ID, COLUMN_COURSE_NAME, COLUMN_COURSE_DEADLINE, COLUMN_COURSE_COMMENT),
            null,
            null,
            null,
            null,
            null
        )

        while (cursor.moveToNext()) {
            val courseData = mapOf(
                "id" to cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_COURSE_ID)).toString(),
                "name" to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COURSE_NAME)),
                "deadline" to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COURSE_DEADLINE)),
                "comment" to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COURSE_COMMENT))
            )
            courseList.add(courseData)
        }
        cursor.close()
        return courseList
    }

    fun getClassById(courseId: Long): Map<String, String>? {
        val db = readableDatabase
        val cursor = db.query(
            COURSE_TABLE_NAME,
            arrayOf(COLUMN_COURSE_ID, COLUMN_COURSE_NAME, COLUMN_COURSE_DEADLINE, COLUMN_COURSE_COMMENT),
            "$COLUMN_COURSE_ID = ?",
            arrayOf(courseId.toString()),
            null,
            null,
            null
        )

        return if (cursor.moveToFirst()) {
            val courseData = mapOf(
                "id" to cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_COURSE_ID)).toString(),
                "name" to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COURSE_NAME)),
                "deadline" to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COURSE_DEADLINE)),
                "comment" to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COURSE_COMMENT))
            )
            cursor.close()
            courseData
        } else {
            cursor.close()
            null
        }
    }

    // Update courses in the database
    fun updateCourse(courseId: Long, name: String, deadline: String, comment: String): Int {
        val db = writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_COURSE_NAME, name)
            put(COLUMN_COURSE_DEADLINE, deadline)
            put(COLUMN_COURSE_COMMENT, comment)
        }
        return db.update(COURSE_TABLE_NAME, contentValues, "$COLUMN_COURSE_ID = ?", arrayOf(courseId.toString()))
    }

    // Delete courses in the database
    fun deleteCourse(courseId: Long): Int {
        val db = writableDatabase
        return db.delete(COURSE_TABLE_NAME, "$COLUMN_COURSE_ID = ?", arrayOf(courseId.toString()))
    }

    // Retrieve all classes
    fun getAllClasses(): List<Map<String, String>> {
        val classList = mutableListOf<Map<String, String>>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            arrayOf(COLUMN_INSTRUCTOR, COLUMN_DATE),
            null,
            null,
            null,
            null,
            null
        )

        while (cursor.moveToNext()) {
            val classInstance = mapOf(
                "instructor" to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INSTRUCTOR)),
                "date" to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE))
            )
            classList.add(classInstance)
        }

        cursor.close()
        db.close()
        return classList
    }


}