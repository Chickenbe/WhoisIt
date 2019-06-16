package com.example.anton.idapplication.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.anton.idapplication.R
import com.example.anton.idapplication.database.Person

class PersonInformationActivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_person_information)

        val fullName = findViewById<TextView>(R.id.full_name_textView)
        val last_name = findViewById<TextView>(R.id.last_name_textView)
        val birthday = findViewById<TextView>(R.id.birthday_textView)
        val id = findViewById<TextView>(R.id.id_textView)

        val arguments: Bundle? = intent.extras
        var person: Person? = null
        if (arguments != null) {
            person = arguments.getSerializable(Person::class.java.simpleName) as Person
        }

        fullName.text = person!!.middleName + " " + person.firstName
        last_name.text = person.secondName
        birthday.text = person.birthdayDate
        id.text = person.personalNum.toString()
    }

}