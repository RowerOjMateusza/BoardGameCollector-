package com.example.boardgamecollector

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.example.boardgamecollector.data.Game
import com.example.boardgamecollector.data.MyDBHandler
import com.example.boardgamecollector.data.Person
import com.example.boardgamecollector.data.Relation
import kotlinx.android.synthetic.main.activity_details.editText1
import kotlinx.android.synthetic.main.activity_details.editText10
import kotlinx.android.synthetic.main.activity_details.editText11
import kotlinx.android.synthetic.main.activity_details.editText12
import kotlinx.android.synthetic.main.activity_details.editText13
import kotlinx.android.synthetic.main.activity_details.editText14
import kotlinx.android.synthetic.main.activity_details.editText15
import kotlinx.android.synthetic.main.activity_details.editText2
import kotlinx.android.synthetic.main.activity_details.editText5
import kotlinx.android.synthetic.main.activity_details.editText6
import kotlinx.android.synthetic.main.activity_details.editText7
import kotlinx.android.synthetic.main.activity_details.editText8
import kotlinx.android.synthetic.main.activity_details.editText9
import kotlinx.android.synthetic.main.activity_details.title1
import kotlinx.android.synthetic.main.activity_form.*
import java.lang.NumberFormatException

class Form : AppCompatActivity() {
    private var ID: String = "0"
    private lateinit var img: ImageView
    private lateinit var game: Game
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)

    }


    fun saveData(v: View) {
        val db = MyDBHandler(this, null, null, 1)
        var text1:Int?
        var text2:Int?
        var text3:Int?
        try{
           text1 = editText2.text.toString().toInt()
        } catch(e: NumberFormatException){
            text1 =  null
        }
        try{
            text2= editText11.text.toString().toInt()
        } catch(e: NumberFormatException){
            text2= null
        }
        try{
            text3= editText13.text.toString().toInt()
        } catch(e: NumberFormatException){
            text3= null
        }

       var id =db.addGame(
            Game(
                ID,
                title1.text.toString(),
                editText1.text.toString(),
                text1,
                editText5.text.toString(),
                editText6.text.toString(),
                editText7.text.toString(),
                editText8.text.toString(),
                editText9.text.toString(),
                editText10.text.toString(),
                0,
                editText12.text.toString(),
                0,
                editText14.text.toString(),
                editText15.text.toString(),
                editText0.text.toString(),
                0
            )
        )
        val designers= editText3.text.split(";").toTypedArray()
        val artists = editText4.text.split(";").toTypedArray()
        for (designer in designers)
        {
             var P_id =db.addPerson(Person("0",designer.toString(),0))
                db.addDesignerRelation(Relation("0",P_id,id))
        }
        for (artist in artists)
        {
            var A_id =db.addPerson(Person("0",artist.toString(),0))
            db.addArtistRelation(Relation("0",A_id,id))
        }
        val i = Intent(this, MainActivity::class.java)
        startActivity(i)
    }
}