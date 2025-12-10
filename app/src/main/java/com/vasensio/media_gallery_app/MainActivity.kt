package com.vasensio.media_gallery_app

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var selectButton: Button
    private lateinit var thumbnailButton: Button

    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
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


        selectButton.setOnClickListener {
            // Pass in the mime type you want to let the user select
            // as the input
            getContent.launch("image/*")
        }

        thumbnailButton.setOnClickListener {
            takePicturePreview.launch(null)
        }
    }
}
