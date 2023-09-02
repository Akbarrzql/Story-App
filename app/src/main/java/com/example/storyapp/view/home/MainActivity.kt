package com.example.storyapp.view.home

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.storyapp.R
import com.example.storyapp.adapter.ListStoryAdapter
import com.example.storyapp.databinding.ActivityMainBinding
import com.example.storyapp.model.ListStoryItem
import com.example.storyapp.view.auth.MainAuthActivity
import com.example.storyapp.view.story.CreateStoryActivity
import com.example.storyapp.viewmodel.home.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var token: String = ""
    private val viewModel: MainViewModel by viewModels()

    private lateinit var recyclerView: RecyclerView
    private lateinit var listAdapter: ListStoryAdapter

    companion object {
        const val EXTRA_TOKEN = "extra_token"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        token = intent.getStringExtra(EXTRA_TOKEN).toString()


        lifecycleScope.launchWhenCreated {
            launch {
                viewModel.getAuthToken().collect { authToken ->
                    if (!authToken.isNullOrEmpty()) token = authToken
                    getAllStories(token)
                    refreshLayout()
                }
            }
        }


        binding.fabCreateStory.setOnClickListener {
            Intent(this, CreateStoryActivity::class.java).also { intent ->
                startActivity(intent)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_item, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_logout -> {
                viewModel.saveAuthToken("")
                Intent(this, MainAuthActivity::class.java).also { intent ->
                    startActivity(intent)
                    finish()
                }
                true
            }
            R.id.menu_setting -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun refreshLayout(){
        binding.refreshLayout.setOnRefreshListener {
            getAllStories(token)
            binding.refreshLayout.isRefreshing = false
        }
    }

    private fun getAllStories(token: String) {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.getAllStories(token).collect { result ->
                    result.onSuccess {
                        val response = result.getOrNull()
                        val listStory = response?.listStory as List<ListStoryItem>
                        setRecyclerView()
                        updateRecylerView(listStory)
                    }

                    result.onFailure {
                        val response = result.exceptionOrNull()
                        response?.printStackTrace()
                    }
                }
            }
        }
    }

    private fun updateRecylerView(listStory: List<ListStoryItem>) {
        val recyclerViewState = recyclerView.layoutManager?.onSaveInstanceState()
        listAdapter.submitList(listStory)
        recyclerView.layoutManager?.onRestoreInstanceState(recyclerViewState)
    }
    
    private fun setRecyclerView() {
        recyclerView = binding.listMateri
        recyclerView.layoutManager = LinearLayoutManager(this)
        listAdapter = ListStoryAdapter()
        recyclerView.apply {
            adapter = listAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }
}