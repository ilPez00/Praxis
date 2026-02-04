package com.praxis.app.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import com.praxis.app.R
import com.praxis.app.goals.GoalTreeActivity

class InterestsPage3Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interests_page3)

        findViewById<Button>(R.id.btn_finish).setOnClickListener {
            startActivity(Intent(this, GoalTreeActivity::class.java))
        }
    }
}
