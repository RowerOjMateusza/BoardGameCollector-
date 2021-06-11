package com.example.boardgamecollector.data

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.boardgamecollector.R
import com.squareup.picasso.Picasso

import java.lang.Double.parseDouble
import java.lang.NumberFormatException
class HomeAdapter(private val context: Activity, private val gameList: MutableList<Game>)
    : ArrayAdapter<Game>(context, R.layout.row, gameList) {


    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val rowView = inflater.inflate(R.layout.row, null, true)

        val titleText = rowView.findViewById(R.id.title) as TextView
        val yearText = rowView.findViewById(R.id.year) as TextView
        val ranking = rowView.findViewById(R.id.rank) as TextView
        val imageView = rowView.findViewById(R.id.img) as ImageView

        titleText.text = gameList[position].title
        ranking.text = gameList[position].ranking.toString()
        try{
            Picasso.get().load(gameList[position].img).into(imageView)
        } catch (e: Throwable){
        }
        //if(gameList[position].year != null){
            yearText.text = "(" + gameList[position].year + ")"
        //}
        //else gameList[position].year = "(nieznany)"

        return rowView
    }
}