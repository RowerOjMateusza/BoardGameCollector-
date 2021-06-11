package com.example.boardgamecollector

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import com.example.boardgamecollector.data.*
import com.example.boardgamecollector.data.Location

class Locations : AppCompatActivity() {
    private lateinit var lista : ListView
    private lateinit var tmpList: MutableList<Location>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)
        lista = findViewById(R.id.locationList)
        lista.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val i = Intent(this,LocationDetails::class.java)

            i.putExtra("lokacja",tmpList[position].ID)
            startActivity(i)

        }
    }

    fun addLocationButton(v: View)
    {
        val i = Intent(this,AddLocation::class.java)
        startActivity(i)
    }
    override fun onStart() {
        super.onStart()
        reload()
    }

    fun reload()
    {
        lista = findViewById(R.id.locationList)
        val db = MyDBHandler(this, null, null, 1)
        tmpList = db.get_all_location()
        val adapter =LocationAdapter(this, tmpList)
        lista.adapter = adapter
    }
}