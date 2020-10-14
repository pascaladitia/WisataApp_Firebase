package com.pascal.wisataappfirebase.ui.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.pascal.wisataappfirebase.R
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val navController = Navigation.findNavController(this, R.id.nav_host_home)
        NavigationUI.setupWithNavController(bottom_navigation, navController)
    }
}