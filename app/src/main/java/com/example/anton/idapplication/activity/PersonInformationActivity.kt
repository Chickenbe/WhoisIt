package com.example.anton.idapplication.activity

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.anton.idapplication.R
import com.example.anton.idapplication.database.Person

class PersonInformationActivity : AppCompatActivity() {

    private var progressBar: ProgressBar? = null
    private var person: Person? = null
    private var imageView: ImageView? = null
    private var bitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_person_information)
        setSupportActionBar(findViewById(R.id.my_child_toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val name = findViewById<TextView>(R.id.name_tv)
        val lastName = findViewById<TextView>(R.id.last_name_tv)
        val middleName = findViewById<TextView>(R.id.middle_name_tv)
        val birthday = findViewById<TextView>(R.id.birthday_tv)
        val id = findViewById<TextView>(R.id.id_tv)

        imageView = findViewById<ImageView>(R.id.person_iv)
        person = null
        val arguments: Bundle? = intent.extras
        if (arguments != null) {
            person = arguments.getSerializable(Person::class.java.simpleName) as Person
        }

        name.text = person?.firstName
        middleName.text = person?.middleName
        lastName.text = person?.secondName
        birthday.text = person?.birthdayDate
        id.text = person?.personalNum.toString()

        progressBar = findViewById(R.id.progressBarID)
        setPhoto(person, imageView)
    }

    private fun setPhoto(person: Person?, imageView: ImageView?) {
        val handler = Handler()
        progressBar?.visibility = ProgressBar.VISIBLE
        Thread(Runnable {
            bitmap = BitmapFactory.decodeStream(application.openFileInput(person?.pictureTag))
            handler.post {
                imageView?.setImageBitmap(bitmap)
                progressBar?.visibility = ProgressBar.INVISIBLE
            }
        }).start()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        bitmap?.recycle()
    }

}