package com.vasensio.media_gallery_app

import android.R.attr.data
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var selectButton: Button
    private lateinit var thumbnailButton: Button
    private lateinit var fullSizeCamera: Button

    lateinit var currentPhotoPath: String
    val REQUEST_IMAGE_CAPTURE = 1

    private val getContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            // Handle the returned Uri
            uri?.let {
                imageView.setImageURI(it)
            }
        }

    private val takePicturePreview = registerForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        // Display the bitmap here
        bitmap?.let {
            imageView.setImageBitmap(it)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data : Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val f = File(currentPhotoPath)
            val contentUri = Uri.fromFile(f)

            imageView.setImageURI(contentUri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        selectButton = findViewById(R.id.button)
        imageView = findViewById(R.id.imageView)
        thumbnailButton = findViewById(R.id.button2)
        fullSizeCamera = findViewById(R.id.button3)

        selectButton.setOnClickListener {
            // Pass in the mime type you want to let the user select
            // as the input
            getContent.launch("image/*")
        }

        thumbnailButton.setOnClickListener {
            takePicturePreview.launch(null)
        }

        fullSizeCamera.setOnClickListener {
            dispatchTakePictureIntent()
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->

                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    null
                }

                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "com.vasensio.media_gallery_app.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    try {
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                    } catch (e: Exception) {
                        e.printStackTrace() // Aquí podrías poner un Toast de error si fallara
                    }
                }
        }
    }


}
