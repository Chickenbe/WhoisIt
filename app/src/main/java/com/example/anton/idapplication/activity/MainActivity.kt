package com.example.anton.idapplication.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
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



class MainActivity : AppCompatActivity() {

    private val newWordActivityRequestCode = 1
    private lateinit var personViewModel: PersonViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        val adapter = PersonListAdapter(this)

        initRecycleView(recyclerView, adapter)

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
                personViewModel.insert(Person(array.get(2), array.get(0), array.get(1), "22.44.5555", 123123))
            }
        } else {
            Toast.makeText(applicationContext, "Enter data before sending", Toast.LENGTH_LONG).show()
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
}