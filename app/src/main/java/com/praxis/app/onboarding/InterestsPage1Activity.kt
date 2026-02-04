package com.praxis.app.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import com.praxis.app.R

class InterestsPage1Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interests_page1)

        findViewById<Button>(R.id.btn_next).setOnClickListener {
            startActivity(Intent(this, InterestsPage2Activity::class.java))
        }
    }
}
