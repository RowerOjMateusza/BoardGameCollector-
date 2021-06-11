package com.example.boardgamecollector

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RadioGroup
import android.content.Intent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.example.boardgamecollector.data.*


class MainActivity : AppCompatActivity() {
    private lateinit var radioGroup: RadioGroup
    private lateinit var lista :ListView
    private lateinit var tmpList: MutableList<Game>
    var sort_column = "2"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        radioGroup = findViewById(R.id.sort)
        reload()

        radioGroup.setOnCheckedChangeListener {radioGroup, i ->
            var rb: RadioButton  = findViewById(i)
            if(rb!=null) {
                when(rb.text.toString()){
                    "tytuł" -> sort_column = "2"
                    "ranking" -> sort_column = "13"
                    else -> sort_column = "4"
                }
                reload()
            }
        }

        lista = findViewById(R.id.lista)

        lista.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val i = Intent(this,Details::class.java)

            i.putExtra("gra",tmpList[position].ID)
            startActivity(i)

        }

    }

    override fun onStart() {
        super.onStart()
       reload()
    }

    fun reload()
    {
        lista = findViewById(R.id.lista)
        //var tmpList: MutableList<Game>
        val db = MyDBHandler(this, null, null, 1)
        tmpList = db.get_all_short(sort_column)
        val adapter = HomeAdapter(this, tmpList)
        lista.adapter = adapter
    }
    fun addButton(v:View)
    {
        val i = Intent(this,AddGame::class.java)
        startActivity(i)
    }
    fun LocationButton(v:View)
    {
        val i = Intent(this,Locations::class.java)
        startActivity(i)
    }
    fun bggButton(v:View)
    {
        val i = Intent(this,BGG::class.java)
        startActivity(i)
    }

    fun DropButton(v:View)
    {
        val db = MyDBHandler(this, null, null, 1)
        val message = AlertDialog.Builder(this)
        message.setMessage("Czy na pewno chcesz usunąć wszystkie dane?")
        message.setTitle("Potwierdzenie")

        val positiveButtonClick = { dialog: DialogInterface, which: Int ->
            val i = Intent(this,MainActivity::class.java)
            startActivity(i)
            db.clear()
        }
        val negativeButtonClick = { dialog: DialogInterface, which: Int ->
        }
        message.setPositiveButton(android.R.string.yes, positiveButtonClick)
        message.setNegativeButton(android.R.string.no, negativeButtonClick)
        message.show()
        db.clear()

    }

}
