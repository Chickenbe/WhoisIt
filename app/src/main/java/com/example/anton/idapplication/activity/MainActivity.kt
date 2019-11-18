package com.example.anton.idapplication.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.anton.idapplication.adapter.PersonListAdapter
import com.example.anton.idapplication.R
import com.example.anton.idapplication.database.Person
import com.example.anton.idapplication.viewmodel.PersonViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File
import android.net.Uri
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import androidx.core.content.FileProvider
import com.example.anton.idapplication.utils.RequestCode
import com.example.anton.idapplication.utils.ResultCode

class MainActivity : AppCompatActivity() {
    private lateinit var adapter: PersonListAdapter
    private lateinit var personViewModel: PersonViewModel
    private var mUri: Uri? = null
    private var isVisibleDoneItem = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.my_toolbar))
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val fab = findViewById<FloatingActionButton>(R.id.fab)

        adapter = PersonListAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        personViewModel = ViewModelProviders.of(this).get(PersonViewModel::class.java)
        personViewModel.allPerson.observe(this, Observer { person ->
            person?.let { adapter.setupNewPersonList(it) }

        })

        fab.setOnClickListener {
            initFabClickListener()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.appbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {

            R.id.delete_action -> {
                setSelectingState(true)
                return true
            }

            /*deleting selected items*/
            R.id.action_done -> {
                val elementStates = adapter.getCheckBoxState()
                if (!elementStates.contains(true)) {
                    setSelectingState(false)
                } else {
                    val personList = personViewModel.allPerson.value
                    AlertDialog.Builder(this@MainActivity)
                        .setTitle("Deleting items")
                        .setMessage("Are you sure you want to delete selected items?")
                        .setPositiveButton("Yes") { dialog, which ->
                            deletePerson(elementStates, personList)
                        }
                        .setNegativeButton("Cancel") { dialog, which ->
                            setSelectingState(false)
                        }.show()
                }
            }

            R.id.takePhoto_action -> {
                startActivity(Intent(applicationContext, PhotoActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun deletePerson(elementStates: BooleanArray, personList: List<Person>?) {
        for (i in elementStates.indices) {
            if (elementStates[i])
                personViewModel.deletePerson(personList!![i])
        }
        setSelectingState(false)
    }


    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val menuItemDone = menu?.findItem(R.id.action_done)
        val menuItemDelete = menu?.findItem(R.id.delete_action)
        if (isVisibleDoneItem) {
            menuItemDone?.isVisible = true
            menuItemDelete?.isVisible = false
        } else {
            menuItemDone?.isVisible = false
            menuItemDelete?.isVisible = true
        }
        return true
    }

    private fun setSelectingState(flag: Boolean) {
        adapter.setCheckBoxState(flag)
        isVisibleDoneItem = flag
        invalidateOptionsMenu()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode) {

            RequestCode.PICTURE_CAPTURE_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    val intent = Intent(this@MainActivity, AddNewDataActivity::class.java)
                    intent.putExtra("imageURI", mUri.toString())
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    startActivityForResult(intent, RequestCode.DATA_ADDING_REQUEST_CODE)
                }
            }

            RequestCode.DATA_ADDING_REQUEST_CODE -> {
                when (resultCode) {

                    Activity.RESULT_CANCELED -> {
                        Toast.makeText(applicationContext, "Something went wrong!", Toast.LENGTH_LONG).show()
                    }
                    Activity.RESULT_OK -> {
                        var person: Person? = null
                        val arguments: Bundle? = data!!.extras
                        if (arguments != null) {
                            person = arguments.getSerializable(Person::class.java.simpleName) as Person
                        }
                        personViewModel.insert(person as Person)
                    }

                    ResultCode.RESULT_RECOGNITION_ERROR -> {
                        Toast.makeText(applicationContext, "Couldn't recognize text. Please try again", Toast.LENGTH_LONG).show()
                    }
                    ResultCode.RESULT_TEXT_RECOGNITION_SUPPORT_ERROR -> {
                        Toast.makeText(applicationContext, "Your device doesn't support text recognition", Toast.LENGTH_LONG).show()
                    }
                    ResultCode.RESULT_FACE_DETECTOR_SUPPORT_ERROR -> {
                        Toast.makeText(applicationContext, "Your device doesn't support face detection", Toast.LENGTH_LONG).show()
                    }
                    ResultCode.RESULT_FACE_DETECTION_ERROR -> {
                        Toast.makeText(applicationContext, "Couldn't detect any face on photo", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

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
        startActivityForResult(intent, RequestCode.PICTURE_CAPTURE_REQUEST_CODE)
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