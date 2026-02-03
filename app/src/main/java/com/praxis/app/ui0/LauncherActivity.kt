package com.praxis.app.ui0


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.praxis.app.R
import com.praxis.app.auth.LoginActivity

class LauncherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)

        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
