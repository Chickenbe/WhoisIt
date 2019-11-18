package com.example.anton.idapplication.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.anton.idapplication.R
import com.example.anton.idapplication.activity.PersonInformationActivity
import com.example.anton.idapplication.database.Person
import de.hdodenhof.circleimageview.CircleImageView

class PersonListAdapter internal constructor(context: Context) :
    RecyclerView.Adapter<PersonListAdapter.PersonViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var personList = emptyList<Person>()
    private val mContext = context

    private var isActive = false
    private lateinit var checkBoxState: BooleanArray

    inner class PersonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val personItemView: TextView = itemView.findViewById(R.id.textView)
        val personTime: TextView = itemView.findViewById(R.id.person_time_tw)
        val checkBox: CheckBox = itemView.findViewById(R.id.checkBox2)
        val circularImageView: CircleImageView = itemView.findViewById(R.id.circularImageView)
        val progressBar: ProgressBar = itemView.findViewById(R.id.progressBarThumbnail)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position in 0 until itemCount) {
                    val person = getPerson(position)
                    val intent = Intent(mContext, PersonInformationActivity::class.java)
                    intent.putExtra(Person::class.java.simpleName, person)
                    startActivity(mContext, intent, null)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonViewHolder {
        val itemView = inflater.inflate(R.layout.recyclerview_item, parent, false)

        return PersonViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PersonViewHolder, position: Int) {
        val current = personList[position]
        holder.personItemView.text = current.fullName
        holder.personTime.text = current.date
        setThumbnail(holder, current)


        if (isActive) {
            holder.checkBox.visibility = View.VISIBLE
            holder.checkBox.isChecked = checkBoxState[position]
            holder.checkBox.setOnClickListener {
                checkBoxState[position] = !checkBoxState[position]
            }
        } else {
            holder.checkBox.visibility = View.INVISIBLE
        }

    }

    internal fun setupNewPersonList(personList: List<Person>) {
        this.personList = personList
        notifyDataSetChanged()


//        val changedList = listOf<Person>()
//        if (this.personList.size > personList.size) {
//            for (person in this.personList) {
//                if (personList.contains(person)) {
//
//                }
//            }
//        } else {
//
//        }


    }

    override fun getItemCount() = personList.size

    fun getPerson(position: Int): Person {
        return personList[position]
    }


    private fun setThumbnail(holder: PersonViewHolder, current: Person) {
        val handler = Handler()
        holder.progressBar.visibility = ProgressBar.VISIBLE

        val bitmap = BitmapFactory.decodeStream(mContext.openFileInput(current.pictureTag))

        holder.circularImageView.setImageBitmap(bitmap)
        holder.progressBar.visibility = ProgressBar.INVISIBLE


    }

    fun getCheckBoxState(): BooleanArray = checkBoxState

    fun setCheckBoxState(state: Boolean) {
        isActive = state
        if (isActive) checkBoxState = BooleanArray(personList.size) { false }
        notifyDataSetChanged()
    }
}