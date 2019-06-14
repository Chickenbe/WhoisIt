package com.example.anton.idapplication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.anton.idapplication.database.Person
import com.example.anton.idapplication.viewmodel.PersonViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private val newWordActivityRequestCode = 1
    private lateinit var personViewModel: PersonViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = PersonListAdapter(this)
        val fab = findViewById<FloatingActionButton>(R.id.fab)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        personViewModel = ViewModelProviders.of(this).get(PersonViewModel::class.java)
        personViewModel.allPerson.observe(this, Observer { person ->
            person?.let { adapter.setPerson(it) }
        })

        fab.setOnClickListener {
            val intent = Intent(this@MainActivity, NewPersonActivity::class.java)
            startActivityForResult(intent, newWordActivityRequestCode)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == newWordActivityRequestCode && resultCode == Activity.RESULT_OK) {
            data.let {
                val fullName = data!!.getStringExtra(NewPersonActivity.EXTRA_REPLY)
                val array = fullName.split(" ")
                personViewModel.insert(Person(array.get(1), array.get(0), array.get(2), "22.44.5555", 123123))
            }
        } else {
            Toast.makeText(applicationContext, "Enter data before sending", Toast.LENGTH_LONG).show()
        }
    }


}