package com.example.storyapp.view.auth.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityRegisterBinding
import com.example.storyapp.view.auth.login.LoginActivity
import com.example.storyapp.viewmodel.auth.login.LoginViewModel
import com.example.storyapp.viewmodel.auth.register.RegisterViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.regex.Pattern

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

    }

    private fun register() {
        val name = binding.etNamaDaftar.text.toString().trim()
        val email = binding.etEmailDaftar.text.toString().trim()
        val password = binding.etPasswordDaftar.text.toString()
        setLoadingState(true)

        lifecycleScope.launchWhenResumed {
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
                        setLoadingState(false)
                    }
                }
            }
        }

    }

    private fun setLoadingState(isLoading: Boolean) {
        binding.apply {
            etNamaDaftar.isEnabled = !isLoading
            etEmailDaftar.isEnabled = !isLoading
            etPasswordDaftar.isEnabled = !isLoading
            btnDaftar.isEnabled = !isLoading

            if (isLoading){
                progressBar.visibility = View.VISIBLE
            } else {
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun isValidPassword(password: String): Boolean {
        val pattern = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$")

        return pattern.matcher(password).matches()
    }

    private fun isValidEmail(email: String): Boolean {
        val pattern = Pattern.compile(
            "^\\w+([.-]?\\w+)*@\\w+([.-]?\\w+)*(\\.\\w{2,3})+$"
        )

        return pattern.matcher(email).matches()
    }
    fun masuk(view: View) {
        Intent (this, LoginActivity::class.java).also {
            startActivity(it)
            finish()
        }
    }
}