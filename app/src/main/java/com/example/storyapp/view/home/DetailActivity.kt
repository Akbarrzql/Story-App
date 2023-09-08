package com.example.storyapp.view.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.storyapp.data.local.entity.Story
import com.example.storyapp.databinding.ActivityDetailBinding
import com.example.storyapp.utils.DateFormatter
import java.util.TimeZone

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val story = intent.getParcelableExtra<Story>(EXTRA_STORY)

        if(story != null) {
            binding.apply {
                tvStoryUsername.text = story.name
                tvStoryDate.text = DateFormatter.formatDate(story.createdAt, TimeZone.getDefault().id)
                tvStoryDescription.text = story.description

                story.photoUrl?.let {
                    ivStoryImage.loadImage(
                        url = it
                    )
                }
            }
        }
    }

    fun ImageView.loadImage(url: String) {
        Glide.with(this.context)
            .load(url)
            .centerCrop()
            .into(this)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        const val EXTRA_STORY = "extra_story"
    }
}