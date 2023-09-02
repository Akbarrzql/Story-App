package com.example.storyapp.view.auth.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.storyapp.view.home.MainActivity
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityLoginBinding
import com.example.storyapp.view.auth.register.RegisterActivity
import com.example.storyapp.view.home.MainActivity.Companion.EXTRA_TOKEN
import com.example.storyapp.viewmodel.auth.login.LoginViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.regex.Pattern

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private var loginJob: Job = Job()
    private val loginViewModel: LoginViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.btnMasuk.setOnClickListener {
            login()
        }

    }

    private fun login() {
        val email = binding.etEmailLogin.text.toString().trim()
        val password = binding.etPasswordLogin.text.toString()
        setLoadingState(true)

        lifecycleScope.launchWhenResumed {

            if(loginJob.isActive) loginJob.cancel()

            loginJob = launch {
                loginViewModel.login(email, password).collect{ result ->
                    result.onSuccess { credentials ->
                        credentials.loginResult?.token?.let { token ->
                            loginViewModel.saveAuthToken(token)
                            Intent(this@LoginActivity, MainActivity::class.java).also {
                                it.putExtra(EXTRA_TOKEN, token)
                                startActivity(it)
                                finish()
                            }
                        }
                        Toast.makeText(this@LoginActivity, getString(R.string.succes_login), Toast.LENGTH_SHORT).show()
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
            etEmailLogin.isEnabled = !isLoading
            etPasswordLogin.isEnabled = !isLoading
            btnMasuk.isEnabled = !isLoading

            if (isLoading){
                progressBar.visibility = View.VISIBLE
            }else{
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

    fun register(view: View) {
        Intent (this, RegisterActivity::class.java).also {
            startActivity(it)
            finish()
        }
    }
}