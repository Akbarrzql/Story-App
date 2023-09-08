package com.example.storyapp.utils
import com.example.storyapp.data.local.entity.Story
import com.example.storyapp.model.ListStoryItem
import com.example.storyapp.model.ResponseGetStory

object DataDummy {
    fun generateDummyStoriesResponse(): ResponseGetStory {
        val error = false
        val message = "Stories fetched successfully"
        val listStory = mutableListOf<ListStoryItem>()

        for (i in 0 until 10) {
            val story = ListStoryItem(
                id = "story-uS1NP4ZRJ4Oerk4Z",
                photoUrl = "https://story-api.dicoding.dev/images/stories/photos-1694050623779_magIATVy.jpg",
                createdAt = "2023-09-07T01:37:03.782Z",
                name = "haloo",
                description = "European",
                lon = -6.756756756756757,
                lat = 110.84191471020274
            )

            listStory.add(story)
        }

        return ResponseGetStory(listStory, error, message)
    }

    fun generateDummyListStory(): List<Story> {
        val items = arrayListOf<Story>()

        for (i in 0..100) {
            val story = Story(
                id = "story-uS1NP4ZRJ4Oerk4Z",
                photoUrl = "https://story-api.dicoding.dev/images/stories/photos-1694050623779_magIATVy.jpg",
                createdAt = "2023-09-07T01:37:03.782Z",
                name = "haloo",
                description = "European",
                lon = -6.756756756756757,
                lat = 110.84191471020274
            )

            items.add(story)
        }

        return items
    }

    fun generateEmptyDummyListStory(): List<Story> {
        return emptyList()
    }
}