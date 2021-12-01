package com.example.stockmaintenanceapp.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.stockmaintenanceapp.R

class CreatePasswordActivity : AppCompatActivity() {

    lateinit var etPassword: EditText
    lateinit var btnConfirm: Button
    lateinit var toolbar: Toolbar

    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_password)

        initView()

        btnConfirm.setOnClickListener {
            val text = etPassword.text.toString()

            if (text.equals("")) {
                Toast.makeText(this, "Enter password", Toast.LENGTH_SHORT).show()
            } else {
                val sharedPreferences = getSharedPreferences("PREFS", 0)
                val editor = sharedPreferences.edit()
                editor.putString("password", text)
                editor.apply()

                //enter the app
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

    }

    private fun initView() {
        toolbar = findViewById(R.id.toolbar)
        etPassword = findViewById(R.id.etPassword)
        btnConfirm = findViewById(R.id.btnConfirm)

        setSupportActionBar(toolbar)
        supportActionBar?.setTitle("Create Password")
    }
}