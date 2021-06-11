package com.example.boardgamecollector

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import com.example.boardgamecollector.data.*

class History : AppCompatActivity() {
    private var ID: String ="0"
    private var tmpList :MutableList<Historia>?=null;
    private lateinit var list : ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        list = findViewById(R.id.historyList)
        val extras = intent.extras ?: return
        ID = extras.getString("history")!!;
        val db = MyDBHandler(this, null, null, 1)

        tmpList = db.get_history(ID)
        val adapter = HistoryAdapter(this, tmpList!!)
        list.adapter = adapter
    }
}