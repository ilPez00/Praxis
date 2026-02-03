// InterestsPage1Activity.kt
package com.example.praxis

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button

class InterestsPage1Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interests_page1)

        findViewById<Button>(R.id.btn_next).setOnClickListener {
            startActivity(Intent(this, InterestsPage2Activity::class.java))
        }
    }
}

// InterestsPage2Activity.kt
class InterestsPage2Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interests_page2)

        findViewById<Button>(R.id.btn_next).setOnClickListener {
            startActivity(Intent(this, InterestsPage3Activity::class.java))
        }
    }
}

// InterestsPage3Activity.kt
class InterestsPage3Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interests_page3)

        findViewById<Button>(R.id.btn_finish).setOnClickListener {
            startActivity(Intent(this, `GoalTreeActivity.kt`::class.java))
        }
    }
}
