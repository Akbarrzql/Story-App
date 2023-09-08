package com.example.storyapp.view.auth.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityRegisterBinding
import com.example.storyapp.view.auth.login.LoginActivity
import com.example.storyapp.viewmodel.auth.register.RegisterViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@ExperimentalPagingApi
@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private var registerJob: Job = Job()
    private val registerViewModel: RegisterViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.btnDaftar.setOnClickListener {
            register()
        }

        binding.tvMasuk.setOnClickListener {
            Intent (this, LoginActivity::class.java).also {
                startActivity(it)
                finish()
            }
        }

    }

    private fun register() {
        val name = binding.etNamaDaftar.text.toString().trim()
        val email = binding.etEmailDaftar.text.toString().trim()
        val password = binding.etPasswordDaftar.text.toString()
        binding.progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            if (registerJob.isActive) registerJob.cancel()

            registerJob = launch {
                registerViewModel.register(name, email, password).collect { result ->
                    result.onSuccess {
                        Toast.makeText(this@RegisterActivity, getString(R.string.succes_register), Toast.LENGTH_SHORT).show()

                        Intent(this@RegisterActivity, LoginActivity::class.java).also {
                            startActivity(it)
                            finish()
                        }
                    }

                    result.onFailure {
                        Snackbar.make(binding.root, it.message.toString(), Snackbar.LENGTH_SHORT).show()
                        binding.progressBar.visibility = View.GONE
                    }
                }
            }
        }

    }
}