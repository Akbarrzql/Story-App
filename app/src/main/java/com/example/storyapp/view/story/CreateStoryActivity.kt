package com.example.storyapp.view.story

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ExifInterface
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.load.resource.bitmap.TransformationUtils.rotateImage
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityCreateStoryBinding
import com.example.storyapp.viewmodel.story.StoryViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

@AndroidEntryPoint
class CreateStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateStoryBinding
    private lateinit var currentPhotoPath: String

    private var getFile: File? = null
    private var token: String = ""

    private val viewModel: StoryViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        lifecycleScope.launchWhenCreated {
            launch {
                viewModel.getAuthToken().collect { authToken ->
                    if (!authToken.isNullOrEmpty()) token = authToken
                }
            }
        }

        binding.btnCamera.setOnClickListener {
            startIntentCamera()
        }

        binding.btnGallery.setOnClickListener {
            startIntentGallery()
        }

        binding.btnStory.setOnClickListener {
            uploadStory()
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val file = File(currentPhotoPath).also { getFile = it }
            val os: OutputStream

            // Rotate image to correct orientation
            val bitmap = BitmapFactory.decodeFile(getFile?.path)
            val exif = ExifInterface(currentPhotoPath)
            val orientation: Int = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED
            )

            val rotatedBitmap: Bitmap = when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 90)
                ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180)
                ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 270)
                ExifInterface.ORIENTATION_NORMAL -> bitmap
                else -> bitmap
            }

            // Convert rotated image to file
            try {
                os = FileOutputStream(file)
                rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, os)
                os.flush()
                os.close()

                getFile = file
            } catch (e: Exception) {
                e.printStackTrace()
            }

            binding.ivGambar.setImageBitmap(rotatedBitmap)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            uriToFile(selectedImg, this).also { getFile = it }

            binding.ivGambar.setImageURI(selectedImg)
        }
    }

    private fun uploadStory() {
        setLoadingState(true)

        val etDescription = binding.etDescStory
        var isValid = true

        if (etDescription.text.toString().isBlank()) {
            etDescription.error = getString(R.string.desc_valid)
            isValid = false
        }

        if (getFile == null) {
            Snackbar.make(
                binding.root,
                getString(R.string.img_valid),
                Snackbar.LENGTH_SHORT
            ).show()
            isValid = false
        }

        if (isValid) {
            val file = reduceFileImage(getFile as File)
            val description =
                etDescription.text.toString().toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )

            lifecycleScope.launchWhenStarted {
                launch {
                    viewModel.uploadStory(token, imageMultipart, description).collect { response ->
                        response.onSuccess {
                            Toast.makeText(
                                this@CreateStoryActivity,
                                getString(R.string.succes_upload),
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        }

                        response.onFailure {
                            setLoadingState(false)
                            Snackbar.make(
                                binding.root,
                                getString(R.string.failed_upload),
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        } else setLoadingState(false)
    }

    private fun startIntentGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, getString(R.string.choose_picture))
        launcherIntentGallery.launch(chooser)
    }

    private fun startIntentCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        createTempFile(this).also { file ->
            val uri: Uri = FileProvider.getUriForFile(
                this,
                "com.example.storyapp",
                file
            )

            currentPhotoPath = file.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
            launcherIntentCamera.launch(intent)
        }
    }

    private fun setLoadingState(isLoading: Boolean) {
        binding.apply {
            btnCamera.isEnabled = !isLoading
            btnGallery.isEnabled = !isLoading
            etDescStory.isEnabled = !isLoading

            if (isLoading){
                progressBar.visibility = View.VISIBLE
            } else {
                progressBar.visibility = View.GONE
            }
        }
    }
    private fun createTempFile(context: Context): File {
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${System.currentTimeMillis()}_",
            ".jpg",
            storageDir
        )
    }

    private fun uriToFile(selectedImg: Uri, context: Context): File {
        val contentResolver: ContentResolver = context.contentResolver
        val myFile = createTempFile(context)

        val inputStream = contentResolver.openInputStream(selectedImg) as InputStream
        val outputStream: OutputStream = FileOutputStream(myFile)
        val buf = ByteArray(1024)
        var len: Int
        while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
        outputStream.close()
        inputStream.close()

        return myFile
    }

    private fun reduceFileImage(file: File): File {
        val bitmap = BitmapFactory.decodeFile(file.path)

        var compressQuality = 100
        var streamLength: Int

        do {
            val bmpStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
            val bmpPicByteArray = bmpStream.toByteArray()
            streamLength = bmpPicByteArray.size
            compressQuality -= 5
        } while (streamLength > 1000000)

        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))

        return file
    }
}