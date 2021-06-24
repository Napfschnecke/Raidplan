package com.raidplan.util

import android.content.Context
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.raidplan.R
import com.raidplan.data.Character

open class CustomArrayAdapter(var con: Context, var resource: Int, var items: List<Character>) :
    ArrayAdapter<Character?>(
        con, resource, items
    ) {
    var holder: ViewHolder? = null

    class ViewHolder {
        var level: TextView? = null
        var name: TextView? = null
        var realm: TextView? = null
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView: View? = convertView
        val inflater =
            context
                .getSystemService(
                    LAYOUT_INFLATER_SERVICE
                ) as LayoutInflater
        if (convertView == null) {
            convertView = inflater.inflate(
                resource, null
            )
            holder = ViewHolder()
            holder?.level = convertView
                .findViewById(R.id.char_level) as TextView
            holder?.name = convertView
                .findViewById(R.id.char_name)
            holder?.realm = convertView
                .findViewById(R.id.char_realm)
            convertView.tag = holder
        } else { // view already defined, retrieve view holder
            holder = convertView.tag as ViewHolder
        }

        holder?.level?.text = items[position].level.toString()
        holder?.name?.text = items[position].name
        holder?.realm?.text = items[position].server
        return convertView!!
    }
}