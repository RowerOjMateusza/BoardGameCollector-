package com.example.boardgamecollector

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.text.Html
import com.example.boardgamecollector.data.MyDBHandler
import com.squareup.picasso.Picasso
import android.view.View
import android.widget.*
import com.example.boardgamecollector.data.Game
import kotlinx.android.synthetic.main.activity_details.*

class Details : AppCompatActivity() {
    private var ID: String ="0"
    private lateinit var img: ImageView
    private lateinit var game: Game
    private lateinit var location: Spinner
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        val extras = intent.extras ?: return
        ID = extras.getString("gra")!!;
        img = findViewById(R.id.imageView)
        location = findViewById(R.id.locationEdit)
        loadDetails()

    }
    override fun onStart() {
        super.onStart()
        loadDetails()
    }

    fun loadDetails()
    {
        val db = MyDBHandler(this, null, null, 1)
        game = db.findGame(ID)!!
        title1.setText(game.title)
        editText1.setText(game.titleOriginal)
        editText2.setText(game.year.toString())
        //editText5.setText(game.description?.replace("&#10;","\n")?.replace("&ldquo;","\"")?.replace("&rdquo;","\"")?.replace("&rsquo;","\'"))
        editText5.setText(Html.fromHtml(game.description,Html.FROM_HTML_MODE_LEGACY).toString())
        editText6.setText(game.orderDate)
        editText7.setText(game.datadodania)
        editText8.setText(game.koszt)
        editText9.setText(game.scd)
        editText10.setText(game.kod)
        editText11.setText(game.bggId.toString())
        editText12.setText(game.productionCode)
        editText13.setText(game.ranking.toString())
        editText14.setText(game.type)
        editText15.setText(game.comment)
        var string = db.getGameDesigner(ID)
        editText3.setText(string)
        string = db.getGameArtists(ID)
        editText4.setText(string)
        try{
            Picasso.get().load(game.img).into(imageView)
        } catch (e: Throwable){}

        var tmp = db.get_all_location()
        var items = arrayListOf<String>("No location")
        items.addAll(db.get_all_location_name())
        val spinnerAdapter = ArrayAdapter(this,
            android.R.layout.simple_spinner_dropdown_item, items)
        location.adapter = spinnerAdapter
        for(i in tmp.indices)
            if(game.location.toString()==tmp[i].ID) {
                location.setSelection(i +1)
                break
            }
    }

    fun saveData(v:View)
    {
        val db = MyDBHandler(this, null, null, 1)
        var tmp = db.get_all_location()
        var x: Int?
        if(location.selectedItemPosition!=0) {
            x = tmp[location.selectedItemPosition - 1].ID?.toInt()
        }
        else {
            x = 0;
        }
        db.updateGame(ID,Game(ID,title1.text.toString(),editText1.text.toString(), editText2.text.toString().toInt(),
            editText5.text.toString(), editText6.text.toString(),editText7.text.toString(),editText8.text.toString(), editText9.text.toString(), editText10.text.toString(), editText11.text.toString().toInt(),
            editText12.text.toString(), editText13.text.toString().toInt(), editText14.text.toString(), editText15.text.toString(), game.img,
            x
        ))

        val i = Intent(this,MainActivity::class.java)
        startActivity(i)
    }
    fun DeleteData(v:View)
    {
        val message = AlertDialog.Builder(this)
        message.setMessage("Czy na pewno chcesz usunąć tą grę do twojej kolekcji")
        message.setTitle("Potwierdzenie")

        val positiveButtonClick = { dialog: DialogInterface, which: Int ->
            val db = MyDBHandler(this, null, null, 1)
            db.deleteGame(ID)
            val i = Intent(this,MainActivity::class.java)
            startActivity(i)
        }
        val negativeButtonClick = { dialog: DialogInterface, which: Int ->
        }
        message.setPositiveButton(android.R.string.yes, positiveButtonClick)
        message.setNegativeButton(android.R.string.no, negativeButtonClick)
        message.show()

    }

    fun dodatki(v:View)
    {
        val i = Intent(this,Dodatki::class.java)

        i.putExtra("dodatek",ID)
        startActivity(i)
    }

    fun historiyButton(v:View)
    {
        val i = Intent(this,History::class.java)

        i.putExtra("history",ID)
        startActivity(i)
    }
}