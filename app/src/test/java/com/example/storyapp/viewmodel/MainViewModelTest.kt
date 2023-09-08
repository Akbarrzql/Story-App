package com.example.storyapp.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import com.example.storyapp.adapter.ListStoryAdapter
import com.example.storyapp.data.local.entity.Story
import com.example.storyapp.respository.AuthRepository
import com.example.storyapp.respository.StoryRepository
import com.example.storyapp.utils.DataDummy
import com.example.storyapp.utils.MainDispatcherRule
import com.example.storyapp.utils.PagedTestDataSource
import com.example.storyapp.utils.getOrAwaitValue
import com.example.storyapp.viewmodel.home.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@ExperimentalPagingApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest{

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    private lateinit var mainViewModel: MainViewModel
    @Mock private lateinit var storyRepository: StoryRepository
    @Mock private lateinit var authRepository: AuthRepository

    private val token = "auth_token"


    @Test
    fun `when Get Quote Should Not Null and Return Data`() = runTest {
        val dummyStory = DataDummy.generateDummyListStory()
        val data: PagingData<Story> = PagedTestDataSource.snapshot(dummyStory)
        val stories = MutableLiveData<PagingData<Story>>()
        stories.value = data

        `when`(storyRepository.getAllStories(token)).thenReturn(stories)

        mainViewModel = MainViewModel(storyRepository, authRepository)
        val actualStory = mainViewModel.getAllStories(token).getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = ListStoryAdapter.DiffCallback,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = mainDispatcherRules.testDispatcher,
            mainDispatcher = mainDispatcherRules.testDispatcher
        )

        differ.submitData(actualStory)

        advanceUntilIdle()

        assertNotNull(differ.snapshot())
        assertEquals(dummyStory.size, differ.snapshot().size)
        assertEquals(dummyStory[0], differ.snapshot()[0])
    }


    @Test
    fun `Get all stories successfully`() = runTest {
        val dummyStory = DataDummy.generateDummyListStory()
        val data: PagingData<Story> = PagedTestDataSource.snapshot(dummyStory)
        val stories = MutableLiveData<PagingData<Story>>()
        stories.value = data

        `when`(storyRepository.getAllStories(token)).thenReturn(stories)

        mainViewModel = MainViewModel(storyRepository, authRepository)
        val actualStory = mainViewModel.getAllStories(token).getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = ListStoryAdapter.DiffCallback,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = mainDispatcherRules.testDispatcher,
            mainDispatcher = mainDispatcherRules.testDispatcher
        )

        differ.submitData(actualStory)

        advanceUntilIdle()
        assertNotNull(differ.snapshot())
        assertEquals(dummyStory.size, differ.snapshot().size)
        assertEquals(dummyStory[0], differ.snapshot()[0])
    }

    @Test
    fun `Get all stories failed`() = runTest {
        val dummyStory = DataDummy.generateDummyListStory()
        val data: PagingData<Story> = PagedTestDataSource.snapshot(dummyStory)
        val stories = MutableLiveData<PagingData<Story>>()
        stories.value = data

        `when`(storyRepository.getAllStories(token)).thenReturn(stories)

        mainViewModel = MainViewModel(storyRepository, authRepository)
        val actualStory = mainViewModel.getAllStories(token).getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = ListStoryAdapter.DiffCallback,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = mainDispatcherRules.testDispatcher,
            mainDispatcher = mainDispatcherRules.testDispatcher
        )

        differ.submitData(actualStory)

        advanceUntilIdle()
        assertNotNull(differ.snapshot())
        assertNotEquals(dummyStory.size + 1, differ.snapshot().size)
    }

    @Test
    fun `Get first story successfully`() = runTest {
        val dummyStory = DataDummy.generateDummyListStory()
        val data: PagingData<Story> = PagedTestDataSource.snapshot(dummyStory)
        val stories = MutableLiveData<PagingData<Story>>()
        stories.value = data

        `when`(storyRepository.getAllStories(token)).thenReturn(stories)

        mainViewModel = MainViewModel(storyRepository, authRepository)
        val actualStory = mainViewModel.getAllStories(token).getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = ListStoryAdapter.DiffCallback,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = mainDispatcherRules.testDispatcher,
            mainDispatcher = mainDispatcherRules.testDispatcher
        )

        differ.submitData(actualStory)

        advanceUntilIdle()
        assertNotNull(differ.snapshot())
        assertEquals(dummyStory[0].id, differ.snapshot()[0]?.id)
    }

    @Test
    fun `Get first story failed`() = runTest {
        val dummyStory = DataDummy.generateDummyListStory()
        val data: PagingData<Story> = PagedTestDataSource.snapshot(dummyStory)
        val stories = MutableLiveData<PagingData<Story>>()
        stories.value = data

        `when`(storyRepository.getAllStories(token)).thenReturn(stories)

        mainViewModel = MainViewModel(storyRepository, authRepository)
        val actualStory = mainViewModel.getAllStories(token).getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = ListStoryAdapter.DiffCallback,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = mainDispatcherRules.testDispatcher,
            mainDispatcher = mainDispatcherRules.testDispatcher
        )

        differ.submitData(actualStory)

        advanceUntilIdle()
        assertNotNull(differ.snapshot())
        assertNotEquals(dummyStory[0].id + 1, differ.snapshot()[0]?.id)
    }

    @Test
    fun `when Get Quote Empty Should Return No Data`() = runTest {
        val dummyStory = DataDummy.generateEmptyDummyListStory()
        val data: PagingData<Story> = PagedTestDataSource.snapshot(dummyStory)
        val stories = MutableLiveData<PagingData<Story>>()
        stories.value = data

        `when`(storyRepository.getAllStories(token)).thenReturn(stories)

        mainViewModel = MainViewModel(storyRepository, authRepository)
        val actualStory = mainViewModel.getAllStories(token).getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = ListStoryAdapter.DiffCallback,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = mainDispatcherRules.testDispatcher,
            mainDispatcher = mainDispatcherRules.testDispatcher
        )

        differ.submitData(actualStory)
        assertEquals(0, differ.snapshot().size)
    }
}

    private val noopListUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }
