package com.example.anton.idapplication.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.example.anton.idapplication.R


class AddNewDataActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_data)

        val imageUri = intent.getStringExtra("imageURI")
        val fileNameTextView = findViewById<TextView>(R.id.file_name_textView)
        val imageView = findViewById<ImageView>(R.id.imageView)
        val replyIntent = Intent()


        if (TextUtils.isEmpty(imageUri)) {
            setResult(Activity.RESULT_CANCELED, replyIntent)
            finish()
        } else {

            val uri: Uri = imageUri.toUri()
            imageView.setImageURI(uri)

            fileNameTextView.text = imageUri
            setResult(Activity.RESULT_OK, replyIntent)
        }
    }
}