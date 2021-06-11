package com.example.boardgamecollector

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.boardgamecollector.data.Location
import com.example.boardgamecollector.data.MyDBHandler
import kotlinx.android.synthetic.main.activity_add_location.*
import kotlinx.android.synthetic.main.activity_details.editText6
import kotlinx.android.synthetic.main.activity_details.editText7
import kotlinx.android.synthetic.main.activity_details.editText8
import kotlinx.android.synthetic.main.activity_details.editText9
import kotlinx.android.synthetic.main.activity_details.title1
import kotlinx.android.synthetic.main.activity_form.*
import java.lang.NumberFormatException

class AddLocation : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_location)
    }
    fun saveData(v: View) {
        val db = MyDBHandler(this, null, null, 1)

        db.addLocation(
            Location("0",editTextAddLocation.text.toString(),"")
            )


        val i = Intent(this, Locations::class.java)
        startActivity(i)
    }
}