package com.example.storyapp.view.auth.splash_screen

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivitySpalshScreenBinding
import com.example.storyapp.view.auth.MainAuthActivity
import com.example.storyapp.view.auth.login.LoginActivity
import com.example.storyapp.view.home.MainActivity
import com.example.storyapp.viewmodel.auth.splash_screen.SplashScreenViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
@AndroidEntryPoint
class SpalshScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySpalshScreenBinding
    private val viewModel: SplashScreenViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySpalshScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        splashScreen()
    }

    private fun splashScreen() {
        lifecycleScope.launchWhenCreated {
            launch {
                viewModel.getAuthToken().collect { token ->
                    if (token.isNullOrEmpty()) {
                        binding.ivSplashScreen.alpha = 0f
                        binding.ivSplashScreen.animate().setDuration(6500).alpha(1f).withEndAction {
                            Intent(this@SpalshScreenActivity, MainAuthActivity::class.java).also { intent ->
                                startActivity(intent)
                                finish()
                            }
                        }
                    } else {
                        binding.ivSplashScreen.alpha = 0f
                        binding.ivSplashScreen.animate().setDuration(6500).alpha(1f).withEndAction {
                            Intent(this@SpalshScreenActivity, MainActivity::class.java).also { intent ->
                                startActivity(intent)
                                finish()
                            }
                        }
                    }
                }
            }
        }
    }
}