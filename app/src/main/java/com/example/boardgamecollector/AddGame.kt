package com.example.boardgamecollector

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.example.boardgamecollector.data.*
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.lang.NumberFormatException
import java.net.MalformedURLException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory

class AddGame : AppCompatActivity() {
    private lateinit var searchView: SearchView
    private lateinit var searchButton: Button
    private lateinit var list :ListView
    private lateinit var searchText: CharSequence
    private lateinit var bar: ProgressBar
    private lateinit var selectedGame: CharSequence
    private var found_games :MutableList<FoundGame>?=null;
    private var title: String?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_game)

        list = findViewById(R.id.searchList)
        searchView=findViewById(R.id.searchView)
        bar = findViewById(R.id.pasek)

        list.onItemClickListener = AdapterView.OnItemClickListener {
                parent, view, position, id ->
            val message = AlertDialog.Builder(this)
            message.setMessage("Czy chcesz pobrać tą grę do twojej kolekcji")
            message.setTitle("Potwierdzenie")

            val positiveButtonClick = { dialog: DialogInterface, which: Int ->
                title = found_games?.get(position)?.title
                selectedGame = found_games?.get(position)!!.id
                downloadGame()
            }
            val negativeButtonClick = { dialog: DialogInterface, which: Int ->
            }
            message.setPositiveButton(android.R.string.yes, positiveButtonClick)
            message.setNegativeButton(android.R.string.no, negativeButtonClick)
            message.show()

        }



    }

    fun downloadGame(){
        val gd = GameDownload2()
        gd.execute()
    }


    fun searchButton(v:View)
    {
        val gd = GameDownloader()
        searchText = searchView.query
        if(searchText.isNotEmpty())
            gd.execute()
    }

    fun getActivity(): Activity {
        return this
    }

    private inner class GameDownloader: AsyncTask<String, Int, String>(){

        override fun onPreExecute() {
            super.onPreExecute()
            bar.visibility = View.VISIBLE
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            loadData()

            var tmpList: MutableList<FoundGame>
            tmpList = found_games!!
            val adapter = GameSummaryAdapter(getActivity(), tmpList)
            list.adapter = adapter
            bar.visibility = View.INVISIBLE

        }

        override fun doInBackground(vararg p0: String?): String {
            try{
                val url = URL("https://boardgamegeek.com/xmlapi2/search?type=boardgame,boardgameexpansion&query=$searchText")
                val connection = url.openConnection()
                connection.connect()

                val lengthOfFile = connection.contentLength
                val isStream = url.openStream()
                val testDirectory = File("$filesDir/XML")
                if(!testDirectory.exists()) testDirectory.mkdir()
                val fos = FileOutputStream("$testDirectory/games.xml")
                val data = ByteArray(1024)
                var count = 0
                var total: Long = 0
                var progress = 0
                count = isStream.read(data)
                while(count != -1){
                    total += count.toLong()
                    val progressTemp = total.toInt() * 100 / lengthOfFile
                    if(progressTemp % 10 == 0 && progress != progressTemp) progress = progressTemp
                    fos.write(data, 0, count)
                    count = isStream.read(data)
                }
                isStream.close()
                fos.close()
            } catch(e: MalformedURLException){
                return "Malformed URL"
            } catch(e: FileNotFoundException){
                return "File not found"
            } catch(e: IOException){
                return "IO Exception"
            }
            return "Success"

        }
    }

    private inner class GameDownload2: AsyncTask<String, Int, String>(){

        override fun onPreExecute() {
            super.onPreExecute()
            bar.visibility = View.VISIBLE
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            loadAllGameData()
           // val db = MyDBHandler(getActivity(), null, null, 1)
            //db.addGame(game)

            bar.visibility = View.INVISIBLE
            //close()
        }

        override fun doInBackground(vararg p0: String?): String {
            try{
                val url = URL("https://boardgamegeek.com/xmlapi2/thing?id=$selectedGame&stats=1")
                val connection = url.openConnection()
                connection.connect()
                val lengthOfFile = connection.contentLength
                val isStream = url.openStream()
                val testDirectory = File("$filesDir/XML")
                if(!testDirectory.exists()) testDirectory.mkdir()
                val fos = FileOutputStream("$testDirectory/game_details.xml")
                val data = ByteArray(1024)
                var count = 0
                var total: Long = 0
                var progress = 0
                count = isStream.read(data)
                while(count != -1){
                    total += count.toLong()
                    val progressTemp = total.toInt() * 100 / lengthOfFile
                    if(progressTemp % 10 == 0 && progress != progressTemp) progress = progressTemp
                    fos.write(data, 0, count)
                    count = isStream.read(data)
                }
                isStream.close()
                fos.close()
            } catch(e: MalformedURLException){
                return "Malformed URL"
            } catch(e: FileNotFoundException){
                return "File not found"
            } catch(e: IOException){
                return "IO Exception"
            }
            Log.i("pobieranie gry","Success")
            return "Success"
        }
    }



    fun loadData(){
        val filename = "games.xml"
        val path = filesDir
        val inDir = File(path, "XML")
        found_games= mutableListOf()
        var current: MutableList<FoundGame>? =null
        current = found_games
        if(inDir.exists()){
            val file = File(inDir, filename)
            if(file.exists()){
                val xmlDoc: Document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file)

                xmlDoc.documentElement.normalize()
                val items: NodeList = xmlDoc.getElementsByTagName("item")

                for(i in 0..items.length - 1){
                    val itemNode: Node = items.item(i)

                    if(itemNode.getNodeType() == Node.ELEMENT_NODE){
                        val elem = itemNode as Element
                        val children = elem.childNodes

                        var type: String? = null
                        var id: String? = null
                        var title: String? = null
                        var year: String? = null

                        when (elem.getAttribute("type")){
                            "boardgame" -> type = elem.getAttribute("type")
//                            "boardgameexpansion" -> type = elem.getAttribute("type")
                            else -> type = null
                        }
                        id = elem.getAttribute("id")

                        for(j in 0..children.length - 1){
                            val node = children.item(j)
                            if(node is Element){
                                when(node.nodeName){
                                    "yearpublished" -> year = node.getAttribute("value")
                                    "name" -> title = node.getAttribute("value")
                                }
                            }
                        }

                        if(type != null)
                            if (current != null) {
                                current.add(FoundGame(type, id, title, year))
                            }
                    }
                }
            }
        }

    }

    fun loadAllGameData(){
        val db = MyDBHandler(getActivity(), null, null, 1)
        val filename = "game_details.xml"
        val path = filesDir
        val inDir = File(path, "XML")

        var titleOriginal: String = ""
        //var titleAlternatives: MutableList<String> = arrayListOf()
        var year: Int? = null
        var designers: MutableList<Long> = arrayListOf()
        var artists: MutableList<Long> = arrayListOf()
        var dodatki: MutableList<Dodatek> = arrayListOf()
        var person:Person
        var relation :Relation
        var description: String? = null
        var orderDate: String? = null
        var addedDate: String? = null
        var cost: String? = null
        var scd: String? = null
        var code: String? = null
        var gameId: Int = 0
        var productionCode: String? = null
        var rank: Int? = null
        var type: String? = null
        var comment: String? = null
        var img: String? = null

        if(inDir.exists()){
            val file = File(inDir, filename)
            if(file.exists()){
                val xmlDoc: Document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file)

                xmlDoc.documentElement.normalize()
                val items: NodeList = xmlDoc.getElementsByTagName("item")

                val itemNode: Node = items.item(0)

                if(itemNode.getNodeType() == Node.ELEMENT_NODE){
                    val elem = itemNode as Element
                    val children = elem.childNodes

                    gameId = elem.getAttribute("id").toInt()
                    type = elem.getAttribute("type")

                    for(j in 0..children.length - 1){
                        val node = children.item(j)
                        if(node is Element){
                            when(node.nodeName){
                                "yearpublished" -> year = node.getAttribute("value").toInt()
                                "thumbnail" -> img = node.textContent
                                "name" -> {
                                    when (node.getAttribute("type")){
                                        "primary" -> titleOriginal = node.getAttribute("value")
                                        //"alternate" -> titleAlternatives.add(node.getAttribute("value"))
                                    }
                                }
                                "description" -> description = node.textContent
                                "link" -> {
                                    when (node.getAttribute("type")){
                                        "boardgameartist" -> {
                                            var id =db.addPerson(Person("0",node.getAttribute("value"),node.getAttribute("id").toInt()))
                                            artists.add(id)
                                        }

                                        "boardgamedesigner" -> {
                                            var id =db.addPerson(Person("0",node.getAttribute("value"),node.getAttribute("id").toInt()))
                                            designers.add(id)
                                        }

                                        "boardgameexpansion" -> {
                                            dodatki.add(Dodatek("0","0",node.getAttribute("id"),node.getAttribute("value")))
                                        }
                                    }
                                }
                                "statistics" -> {
                                    val childrenStats = node.childNodes

                                    for(s in 0..childrenStats.length-1){
                                        val stat = childrenStats.item(s)

                                        if(stat is Element) {

                                            when (stat.nodeName) {
                                                "ratings" -> {
                                                    val childrenRatings = stat.childNodes

                                                    for (r in 0..childrenRatings.length - 1) {
                                                        val ratings = childrenRatings.item(r)

                                                        if(ratings is Element) {

                                                            when (ratings.nodeName) {
                                                                "ranks" -> {
                                                                    val childrenRanks =
                                                                        ratings.childNodes

                                                                    for (rk in 0..childrenRanks.length - 1) {
                                                                        val ranks =
                                                                            childrenRanks.item(rk)

                                                                        if(ranks is Element) {

                                                                            when (ranks.nodeName) {
                                                                                "rank" -> {
                                                                                    if (ranks.getAttribute("type") ==
                                                                                        "subtype" &&
                                                                                        ranks.getAttribute("name") ==
                                                                                        "boardgame"){

                                                                                        try{
                                                                                            rank = ranks.getAttribute("value").
                                                                                            toString().toInt()
                                                                                        } catch(e: NumberFormatException){
                                                                                            rank = 0
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

           var game_id= db.addGame(Game(null,title,titleOriginal, year,
            description, orderDate, addedDate, cost, scd, code, gameId,
            productionCode, rank, type, comment, img, 0))

        for(artist in artists)
        {
            db.addArtistRelation(Relation("0",artist,game_id))
        }
        for(designer in designers)
        {
            db.addDesignerRelation(Relation("0",designer,game_id))
        }

        for(dodatek in dodatki)
        {
            db.addDodatek(Dodatek("0",game_id.toString(),dodatek.bggID,dodatek.name))
        }
        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        val currentDate = sdf.format(Date())
        db.addHistory(Historia("0",game_id.toString(),currentDate.toString(),rank.toString()))
    }
    fun CreateNewButton(v:View)
    {
        val i = Intent(this,Form::class.java)
        startActivity(i)
    }
}

