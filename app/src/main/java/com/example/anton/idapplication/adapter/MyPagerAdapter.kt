package com.example.anton.idapplication.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import com.example.anton.idapplication.R

class MyPagerAdapter(fr: FragmentManager, private val propertyNameList: ArrayList<String>)
    : FragmentStatePagerAdapter(fr) {

    private var dataMap = HashMap<String, String>()

    init {
        for (property in propertyNameList) {
            dataMap[property] = ""
        }
    }

    override fun getItem(position: Int): Fragment {
        return if (position in 0..4) {
            MyFragment.newFragment(propertyNameList[position], dataMap[propertyNameList[position]])
        } else{
            MySecondFragment.newFragment()
        }
    }

    override fun getCount(): Int {
        return dataMap.size + 1
    }

    override fun getItemPosition(`object`: Any): Int {
        return PagerAdapter.POSITION_NONE
    }

    fun update(propertyName: String, value: String) {
        dataMap.replace(propertyName, value)
        notifyDataSetChanged()
    }

    fun getDataMap() : HashMap<String, String> {
        return dataMap
    }
}



class MyFragment : Fragment() {

    var text: String? = null

    companion object {
        private const val EXTRA_MESSAGE_1 = "EXTRA_MESSAGE_1"
        private const val EXTRA_MESSAGE_2 = "EXTRA_MESSAGE_2"

        fun newFragment(propertyName: String, data: String?) : MyFragment {
            val myFragment = MyFragment()
            val bundle = Bundle(2)
            bundle.putString(EXTRA_MESSAGE_1, propertyName)
            bundle.putString(EXTRA_MESSAGE_2, data)
            myFragment.arguments = bundle
            return myFragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.new_data_page, container, false)
        val propertyName = arguments!!.getString(EXTRA_MESSAGE_1)
        val data = arguments!!.getString(EXTRA_MESSAGE_2)
        val propNameTextView = view.findViewById<TextView>(R.id.personPropTextView)
        val dataTextView = view.findViewById<EditText>(R.id.data_edit)
        dataTextView.setText(data)
        propNameTextView.text = propertyName
        return view
    }

    override fun onResume() {
        val editTextData = arguments!!.getString(EXTRA_MESSAGE_2)
        val dataTextView = view?.findViewById<EditText>(R.id.data_edit)
        dataTextView?.setText(editTextData)
        super.onResume()
    }
}


class MySecondFragment : Fragment() {
    companion object {
        fun newFragment() : MySecondFragment = MySecondFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.new_data_page_submit, container, false)
    }
}
