package com.example.boardgamecollector

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast
import com.example.boardgamecollector.data.Game
import com.example.boardgamecollector.data.HomeAdapter
import com.example.boardgamecollector.data.Location
import com.example.boardgamecollector.data.MyDBHandler
import kotlinx.android.synthetic.main.activity_details.*

class LocationDetails : AppCompatActivity() {
    private var ID: String ="0"
    private lateinit var location: Location
    private lateinit var lista : ListView
    private lateinit var tmpList: MutableList<Game>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_details)
        val extras = intent.extras ?: return
        ID = extras.getString("lokacja")!!;
        lista = findViewById(R.id.gameInLocationList)
        loadDetails()
        reload()
        lista.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val i = Intent(this,Details::class.java)

            i.putExtra("gra",tmpList[position].ID)
            startActivity(i)

        }
    }

    override fun onStart() {
        super.onStart()
        loadDetails()
        reload()
    }

    fun reload()
    {
        //var tmpList: MutableList<Game>
        val db = MyDBHandler(this, null, null, 1)
        tmpList = db.getAllByLocation(ID)
        val adapter = HomeAdapter(this, tmpList)
        lista.adapter = adapter
    }
    fun loadDetails()
    {
        val db = MyDBHandler(this, null, null, 1)
        location = db.findLocation(ID)!!
        editText1.setText(location.name)
        editText2.setText(location.comment)
    }

    fun saveData(v: View)
    {
        val db = MyDBHandler(this, null, null, 1)
        db.updateLocation(ID,Location(ID,editText1.text.toString(),editText2.text.toString()))

        val i = Intent(this,Locations::class.java)
        startActivity(i)
    }
    fun DeleteData(v: View)
    {
        if(tmpList.size!=0) {
            Toast.makeText(this, "W lokacji znajdują się jeszcze gry", Toast.LENGTH_SHORT).show()
            return
        }
        val message = AlertDialog.Builder(this)
        message.setMessage("Czy na pewno chcesz usunąć tą lokację z twojej kolekcji")
        message.setTitle("Potwierdzenie")

        val positiveButtonClick = { dialog: DialogInterface, which: Int ->
            val db = MyDBHandler(this, null, null, 1)
            db.deleteLocation(ID)
            val i = Intent(this,Locations::class.java)
            startActivity(i)
        }
        val negativeButtonClick = { dialog: DialogInterface, which: Int ->
        }
        message.setPositiveButton(android.R.string.yes, positiveButtonClick)
        message.setNegativeButton(android.R.string.no, negativeButtonClick)
        message.show()

    }
}