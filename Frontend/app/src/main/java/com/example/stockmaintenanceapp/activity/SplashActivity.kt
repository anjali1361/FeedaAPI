package com.example.stockmaintenanceapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.stockmaintenanceapp.R

class SplashActivity : AppCompatActivity() {

    var password: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val sharedPreferences = getSharedPreferences("PREFS", 0)
        password = sharedPreferences.getString("password", "")

        val handler = Handler()
        handler.postDelayed(Runnable {
            if (password == "") {
                kotlin.run {
                    val intent = Intent(this, CreatePasswordActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            } else {
                val intent = Intent(this, SmartPinLogin::class.java)
                startActivity(intent)
                finish()
            }

        }, 2000)
    }
}