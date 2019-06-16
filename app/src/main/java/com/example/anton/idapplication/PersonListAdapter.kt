package com.example.anton.idapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.anton.idapplication.database.Person

class PersonListAdapter internal constructor(context: Context) : RecyclerView.Adapter<PersonListAdapter.PersonViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var personList = emptyList<Person>()

    inner class PersonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val personItemView: TextView = itemView.findViewById(R.id.textView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : PersonViewHolder {
        val itemView = inflater.inflate(R.layout.recyclerview_item, parent, false)
        return PersonViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PersonViewHolder, position: Int) {
        val current = personList[position]
        holder.personItemView.text = current.fullName
    }

    internal fun setPerson(personList: List<Person>) {
        this.personList = personList
        notifyDataSetChanged()
    }

    override fun getItemCount() = personList.size

    fun getPerson(position: Int) : Person{
        return personList[position]
    }
}