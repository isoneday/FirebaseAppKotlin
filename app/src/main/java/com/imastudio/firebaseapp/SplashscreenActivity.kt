package com.imastudio.firebaseandroid

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.imastudio.firebaseapp.R
import com.imastudio.firebaseapp.authentication.LoginActivity

class SplashscreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView (R.layout.activity_splashscreen)
        var auth = FirebaseAuth.getInstance()
        Handler().postDelayed(Runnable {
            if (auth.currentUser == null) {
                startActivity(Intent(this@SplashscreenActivity, LoginActivity::class.java))
            } else {
                startActivity(Intent(this@SplashscreenActivity, MainActivity::class.java))
            }
        }, 3000)
    }
}