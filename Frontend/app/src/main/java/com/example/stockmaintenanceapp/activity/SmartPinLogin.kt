package com.example.stockmaintenanceapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.appcompat.widget.Toolbar
import com.example.stockmaintenanceapp.R

class SmartPinLogin : AppCompatActivity() {

    lateinit var etPasswordLogin: EditText
    lateinit var txtForgotPassword: TextView
    lateinit var btnEnter: Button
    lateinit var toolbar: Toolbar

    var password: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_smart_pin_login)

        intiView()
        loadPassword()

        btnEnter.setOnClickListener {
            val text = etPasswordLogin.text.toString()
            if (text.equals(password)) {
                //enter the app
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Wrong password", Toast.LENGTH_SHORT).show()
            }
        }

        txtForgotPassword.setOnClickListener {
            val intent = Intent(this, CreatePasswordActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun loadPassword() {
        val sharedPreferences = getSharedPreferences("PREFS", 0)
        password = sharedPreferences.getString("password", "")
    }

    private fun intiView() {
        toolbar = findViewById(R.id.toolbar)
        etPasswordLogin = findViewById(R.id.etPasswordLogin)
        txtForgotPassword = findViewById(R.id.txtForgotPassword)
        btnEnter = findViewById(R.id.btnEnter)

        setSupportActionBar(toolbar)
        supportActionBar?.setTitle("Login")
    }
}