package com.example.storyapp.view.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.storyapp.R
import com.example.storyapp.adapter.ListStoryAdapter
import com.example.storyapp.adapter.LoadingStateAdapter
import com.example.storyapp.data.local.entity.Story
import com.example.storyapp.databinding.ActivityMainBinding
import com.example.storyapp.view.auth.MainAuthActivity
import com.example.storyapp.view.location.MapsActivity
import com.example.storyapp.view.story.CreateStoryActivity
import com.example.storyapp.viewmodel.home.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@ExperimentalPagingApi
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var token: String = ""
    private val viewModel: MainViewModel by viewModels()

    private lateinit var recyclerView: RecyclerView
    private lateinit var listAdapter: ListStoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        token = intent.getStringExtra(EXTRA_TOKEN).toString()


        lifecycleScope.launch {
            launch {
                viewModel.getAuthToken().collect { authToken ->
                    if (!authToken.isNullOrEmpty()) token = authToken
                    getAllStories(token)
                    setRecyclerView()
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
            R.id.maps -> {
                startActivity(Intent(this, MapsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun refreshLayout(){
        binding.refreshLayout.setOnRefreshListener {
            getAllStories(token)
        }
    }

    private fun getAllStories(token: String) {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.getAllStories(token).observe(this@MainActivity) { listStory ->
                    updateRecylerView(listStory)
                }
            }
        }
    }

    private fun updateRecylerView(listStory: PagingData<Story>) {
        recyclerView = binding.listMateri
        val recyclerViewState = recyclerView.layoutManager?.onSaveInstanceState()
        listAdapter.submitData(lifecycle, listStory)
        recyclerView.layoutManager?.onRestoreInstanceState(recyclerViewState)
    }
    
    private fun setRecyclerView() {
        recyclerView = binding.listMateri
        listAdapter = ListStoryAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this)

        listAdapter.addLoadStateListener { loadState ->
            if ((loadState.source.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && listAdapter.itemCount < 1) || loadState.source.refresh is LoadState.Error) {
                binding.apply {
                    lottieNotFound.isVisible = true
                    tvNotFound.isVisible = true
                    recyclerView.isVisible = false
                }
            } else {
                binding.apply {
                    lottieNotFound.isVisible = false
                    tvNotFound.isVisible = false
                    recyclerView.isVisible = true
                }
            }

            binding.refreshLayout.isRefreshing = loadState.source.refresh is LoadState.Loading
        }

        try {
            recyclerView = binding.listMateri
            recyclerView.apply {
                adapter = listAdapter.withLoadStateFooter(
                    footer = LoadingStateAdapter { listAdapter.retry() }
                )
                recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
            }
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    companion object {
        const val EXTRA_TOKEN = "extra_token"
    }

}