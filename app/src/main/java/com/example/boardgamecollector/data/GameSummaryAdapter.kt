package com.example.boardgamecollector.data

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.boardgamecollector.R

import java.lang.Double.parseDouble
import java.lang.NumberFormatException

class GameSummaryAdapter(private val context: Activity, private val gameList: MutableList<FoundGame>)
    : ArrayAdapter<FoundGame>(context, R.layout.row2, gameList) {


    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val rowView = inflater.inflate(R.layout.row2, null, true)

        val titleText = rowView.findViewById(R.id.title) as TextView
        val yearText = rowView.findViewById(R.id.year) as TextView

        titleText.text = gameList[position].title

        if(gameList[position].year != null){
            yearText.text = "(" + gameList[position].year + ")"
        }
        else gameList[position].year = "nieznany"

        return rowView
    }
}