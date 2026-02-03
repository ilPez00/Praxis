class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Start blinking cursor animation
        (btn_register.compoundDrawablesRelative[2] as? AnimationDrawable)?.start()

        // Fade-in animations (staggered like HTML)
        val viewsToAnimate = listOf(
            title to 0L,
            subtitle to 100L,
            btn_google to 200L,
            divider_container to 300L,
            til_email_phone to 400L,
            til_password to 500L,
            til_confirm to 600L,
            btn_register to 700L,
            tv_back to 800L
        )

        viewsToAnimate.forEach { (view, delay) ->
            view.animate()
                .alpha(1f)
                .translationX(0f)
                .setStartDelay(delay)
                .setDuration(800)
                .start()
        }

        // Google button click simulation
        btn_google.setOnClickListener {
            btn_google.text = "Connecting to Google..."
            btn_google.isEnabled = false
            Handler(Looper.getMainLooper()).postDelayed({
                btn_google.text = "Continue with Google"
                btn_google.isEnabled = true
                // TODO: Launch real Google Sign-In Intent
                Toast.makeText(this, "Google Sign-In placeholder", Toast.LENGTH_SHORT).show()
            }, 2000)
        }

        // Register button
        btn_register.setOnClickListener {
            val emailPhone = et_email_phone.text.toString().trim()
            val pass = et_password.text.toString()
            val confirm = et_confirm.text.toString()

            if (pass != confirm) {
                til_confirm.error = "Passwords do not match"
                return@setOnClickListener
            }
            if (emailPhone.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btn_register.text = "Creating Account"
            btn_register.isEnabled = false

            Handler(Looper.getMainLooper()).postDelayed({
                btn_register.text = "Account Created"
                Handler(Looper.getMainLooper()).postDelayed({
                    btn_register.text = "REGISTER"
                    btn_register.isEnabled = true
                    // TODO: Proceed to onboarding / main screen
                    Toast.makeText(this, "Registration success (placeholder)", Toast.LENGTH_SHORT).show()
                }, 1500)
            }, 1500)
        }

        // Back to Login
        tv_back.setOnClickListener {
            finish()  // or navigate back
        }
    }
}
