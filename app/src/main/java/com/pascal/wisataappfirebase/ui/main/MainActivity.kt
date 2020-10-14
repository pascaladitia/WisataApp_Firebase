package com.pascal.wisataappfirebase.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.pascal.wisataappfirebase.R
import com.pascal.wisataappfirebase.ui.login.LoginActivity

class MainActivity : AppCompatActivity() {

    private  lateinit var handler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        handler = Handler()
        handler.postDelayed({
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }, 2000)
    }
}