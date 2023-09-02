package com.example.storyapp.view.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityDetailBinding
import com.example.storyapp.model.ListStoryItem

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    companion object {
        const val EXTRA_STORY = "extra_story"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val story = intent.getParcelableExtra<ListStoryItem>(EXTRA_STORY)

        if(story != null) {
            binding.apply {
                tvStoryUsername.text = story.name
                tvStoryDate.text = story.createdAt.toString()
                tvStoryDescription.text = story.description

                Glide.with(this@DetailActivity)
                    .load(story.photoUrl)
                    .into(ivStoryImage)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}