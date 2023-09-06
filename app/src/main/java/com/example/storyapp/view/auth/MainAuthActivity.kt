package com.example.storyapp.view.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.paging.ExperimentalPagingApi
import com.example.storyapp.databinding.ActivityMainAuthBinding
import com.example.storyapp.view.auth.login.LoginActivity
import com.example.storyapp.view.auth.register.RegisterActivity
import dagger.hilt.android.AndroidEntryPoint

@ExperimentalPagingApi
@AndroidEntryPoint
class MainAuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainAuthBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.btnLogin.setOnClickListener {
            Intent (this, LoginActivity::class.java).also {
                startActivity(it)
            }
        }

        binding.btnRegister.setOnClickListener {
            Intent (this, RegisterActivity::class.java).also {
                startActivity(it)
            }
        }
    }
}