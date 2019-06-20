package com.example.anton.idapplication.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.anton.idapplication.PersonListAdapter
import com.example.anton.idapplication.R
import com.example.anton.idapplication.RecyclerClickListener
import com.example.anton.idapplication.database.Person
import com.example.anton.idapplication.viewmodel.PersonViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider


@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class MainActivity : AppCompatActivity() {

    private val NEW_PERSON_ACTIVITY_REQUEST_CODE = 1
    private val PICTURE_CAPTURE_REQUEST_CODE = 2
    private val DATA_ADDING_REQUEST_CODE = 3

    private lateinit var personViewModel: PersonViewModel
    private var mUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        val adapter = PersonListAdapter(this)

        initRecycleView(recyclerView, adapter)

        fab.setOnClickListener {
            initFabClickListener()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode) {

            NEW_PERSON_ACTIVITY_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    data.let {
                        val fullName = data!!.getStringExtra(NewPersonActivity.EXTRA_REPLY)
                        val array = fullName.split(" ")
                        personViewModel.insert(Person(array.get(2), array.get(0), array.get(1), "22.44.5555", 123123))
                    }
                } else {
                    Toast.makeText(applicationContext, "Enter data before sending", Toast.LENGTH_LONG).show()
                }
            }

            PICTURE_CAPTURE_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    val intent = Intent(this@MainActivity, AddNewDataActivity::class.java)
                    intent.putExtra("imageURI", mUri.toString())
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    startActivityForResult(intent, DATA_ADDING_REQUEST_CODE)
                }
            }

            DATA_ADDING_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_CANCELED) {
                    Toast.makeText(applicationContext, "Something went wrong!", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(applicationContext, "It's ok!", Toast.LENGTH_LONG).show()
                }
            }
        }

    }


    private fun initRecycleView(recyclerView: RecyclerView, adapter: PersonListAdapter) {
        val layoutManager = LinearLayoutManager(applicationContext)
        val dividerItemDecoration = DividerItemDecoration(recyclerView.context, layoutManager.orientation)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(dividerItemDecoration)

        setClickListenerForRecycleView(recyclerView, adapter)

        personViewModel = ViewModelProviders.of(this).get(PersonViewModel::class.java)
        personViewModel.allPerson.observe(this, Observer { person ->
            person?.let { adapter.setPerson(it) }
        })
    }

    private fun setClickListenerForRecycleView(recyclerView: RecyclerView, adapter: PersonListAdapter) {
        recyclerView.addOnItemTouchListener(object : RecyclerClickListener(this) {
            override fun onItemClick(recyclerView: RecyclerView, itemView: View, position: Int) {
                val person = adapter.getPerson(position)
                val intent = Intent(this@MainActivity, PersonInformationActivity::class.java)
                intent.putExtra(Person::class.java.simpleName, person)
                startActivity(intent)
            }

            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
    }

    private fun initFabClickListener() {

        val fileName = "image.png"
        mUri = generateFileUri(fileName)

        if (mUri == null) {
            Toast.makeText(applicationContext, "SD card not available", Toast.LENGTH_LONG).show()
            return
        }

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mUri)
        startActivityForResult(intent, PICTURE_CAPTURE_REQUEST_CODE)
    }

    private fun generateFileUri(filename: String): Uri? {

        if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED)
            return null

        val path = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "IDapplication")
        if (!path.exists()) {
            if (!path.mkdirs()) {
                return null
            }
        }

        val newFile = File(path.path + File.separator + filename)
        mUri = FileProvider.getUriForFile(applicationContext, applicationContext.packageName + ".provider", newFile)
        return mUri
    }

}