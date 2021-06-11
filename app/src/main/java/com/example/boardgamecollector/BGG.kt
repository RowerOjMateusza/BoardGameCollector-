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

class BGG : AppCompatActivity() {
    private lateinit var searchView: SearchView
    private lateinit var searchButton: Button
    private lateinit var searchText: CharSequence
    private lateinit var tmpList: MutableList<Game>
    private lateinit var bar: ProgressBar
    private lateinit var selectedGame: CharSequence
    private lateinit var lista: MutableList<FoundGame>
    private var ID: String = "0"
    private var title: String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bgg)
        bar = findViewById(R.id.pasek)
        searchView = findViewById(R.id.searchView2)
    }

    fun refresh(v: View) {
        val gd = GameDownload2()
        gd.execute()
    }

    fun searchButton(v: View) {
        val gd = GameDownloader()
        searchText = searchView.query
        if (searchText.isNotEmpty())
            gd.execute()
    }

    fun getActivity(): Activity {
        return this
    }

    private inner class GameDownloader : AsyncTask<String, Int, String>() {

        override fun onPreExecute() {
            super.onPreExecute()
            bar.visibility = View.VISIBLE
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            lista=loadData()

            val message = AlertDialog.Builder(getActivity())
            message.setMessage("Znaleziono listę ${lista.size} gier. Czy chcesz dodać je do  twojej kolekcji?")
            message.setTitle("Potwierdzenie")

            val positiveButtonClick = { dialog: DialogInterface, which: Int ->
                val gd = GameDownload3()
                gd.execute()
                val i = Intent(getActivity(),MainActivity::class.java)
                startActivity(i)
            }
            val negativeButtonClick = { dialog: DialogInterface, which: Int ->
            }
            message.setPositiveButton(android.R.string.yes, positiveButtonClick)
            message.setNegativeButton(android.R.string.no, negativeButtonClick)
            message.show()

            bar.visibility = View.INVISIBLE

        }

        override fun doInBackground(vararg p0: String?): String {
            try {
                val url =
                    URL("https://www.boardgamegeek.com/xmlapi2/collection?username=$searchText")
                val connection = url.openConnection()
                connection.connect()

                val lengthOfFile = connection.contentLength
                val isStream = url.openStream()
                val testDirectory = File("$filesDir/XML")
                if (!testDirectory.exists()) testDirectory.mkdir()
                val fos = FileOutputStream("$testDirectory/user2.xml")
                val data = ByteArray(1024)
                var count = 0
                var total: Long = 0
                var progress = 0
                count = isStream.read(data)
                while (count != -1) {
                    total += count.toLong()
                    val progressTemp = total.toInt() * 100 / lengthOfFile
                    if (progressTemp % 10 == 0 && progress != progressTemp) progress = progressTemp
                    fos.write(data, 0, count)
                    count = isStream.read(data)
                }
                isStream.close()
                fos.close()
            } catch (e: MalformedURLException) {
                return "Malformed URL"
            } catch (e: FileNotFoundException) {
                return "File not found"
            } catch (e: IOException) {
                return "IO Exception"
            }
            return "Success"

        }
    }


    private inner class GameDownload2 : AsyncTask<String, Int, String>() {

        override fun onPreExecute() {
            super.onPreExecute()
            //bar.visibility = View.VISIBLE
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            // bar.visibility = View.INVISIBLE
        }

        override fun doInBackground(vararg p0: String?): String {
            val db = MyDBHandler(getActivity(), null, null, 1)
            tmpList = db.get_all_short("1")
            for (i in tmpList.indices) {
                if (tmpList[i].bggId == 0)
                    continue

                selectedGame = tmpList[i].bggId.toString()
                ID = tmpList[i].ID.toString()
                try {
                    val url =
                        URL("https://boardgamegeek.com/xmlapi2/thing?id=$selectedGame&stats=1")
                    val connection = url.openConnection()
                    connection.connect()
                    val lengthOfFile = connection.contentLength
                    val isStream = url.openStream()
                    val testDirectory = File("$filesDir/XML")
                    if (!testDirectory.exists()) testDirectory.mkdir()
                    val fos = FileOutputStream("$testDirectory/game_details_rank.xml")
                    val data = ByteArray(1024)
                    var count = 0
                    var total: Long = 0
                    var progress = 0
                    count = isStream.read(data)
                    while (count != -1) {
                        total += count.toLong()
                        val progressTemp = total.toInt() * 100 / lengthOfFile
                        if (progressTemp % 10 == 0 && progress != progressTemp) progress =
                            progressTemp
                        fos.write(data, 0, count)
                        count = isStream.read(data)
                    }
                    isStream.close()
                    fos.close()
                } catch (e: MalformedURLException) {
                    return "Malformed URL"
                } catch (e: FileNotFoundException) {
                    return "File not found"
                } catch (e: IOException) {
                    return "IO Exception"
                }
                Log.i("pobieranie gry", "Success")
                loadAllGameData()
                Thread.sleep(1_000)
            }
            return "Success"
        }
    }

    private inner class GameDownload3: AsyncTask<String, Int, String>(){

        override fun onPreExecute() {
            super.onPreExecute()
            bar.visibility = View.VISIBLE
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            loadAllGameData()
            bar.visibility = View.INVISIBLE

        }

        override fun doInBackground(vararg p0: String?): String {
            for (i in lista.indices) {
                if (lista[i].id == "0")
                    continue

                selectedGame = lista[i].id
                title = lista[i].title
                try {
                    val url =
                        URL("https://boardgamegeek.com/xmlapi2/thing?id=$selectedGame&stats=1")
                    val connection = url.openConnection()
                    connection.connect()
                    val lengthOfFile = connection.contentLength
                    val isStream = url.openStream()
                    val testDirectory = File("$filesDir/XML")
                    if (!testDirectory.exists()) testDirectory.mkdir()
                    val fos = FileOutputStream("$testDirectory/game_details.xml")
                    val data = ByteArray(1024)
                    var count = 0
                    var total: Long = 0
                    var progress = 0
                    count = isStream.read(data)
                    while (count != -1) {
                        total += count.toLong()
                        val progressTemp = total.toInt() * 100 / lengthOfFile
                        if (progressTemp % 10 == 0 && progress != progressTemp) progress =
                            progressTemp
                        fos.write(data, 0, count)
                        count = isStream.read(data)
                    }
                    isStream.close()
                    fos.close()
                } catch (e: MalformedURLException) {
                    return "Malformed URL"
                } catch (e: FileNotFoundException) {
                    return "File not found"
                } catch (e: IOException) {
                    return "IO Exception"
                }
                loadAllGameData2()
                Log.i("pobieranie gry", "Success")
            }
            return "Success"
        }
    }

    fun loadAllGameData() {
        val db = MyDBHandler(this, null, null, 1)
        val filename = "game_details_rank.xml"
        val path = filesDir
        val inDir = File(path, "XML")

        var titleOriginal: String = ""
        //var titleAlternatives: MutableList<String> = arrayListOf()
        var year: Int? = null
        var designers: MutableList<Long> = arrayListOf()
        var artists: MutableList<Long> = arrayListOf()
        var dodatki: MutableList<Dodatek> = arrayListOf()
        var person: Person
        var relation: Relation
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

        if (inDir.exists()) {
            val file = File(inDir, filename)
            if (file.exists()) {
                val xmlDoc: Document =
                    DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file)

                xmlDoc.documentElement.normalize()
                val items: NodeList = xmlDoc.getElementsByTagName("item")

                val itemNode: Node = items.item(0)

                if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
                    val elem = itemNode as Element
                    val children = elem.childNodes

                    gameId = elem.getAttribute("id").toInt()
                    type = elem.getAttribute("type")

                    for (j in 0..children.length - 1) {
                        val node = children.item(j)
                        if (node is Element) {
                            when (node.nodeName) {
                                "yearpublished" -> year = node.getAttribute("value").toInt()
                                "thumbnail" -> img = node.textContent
                                "name" -> {
                                    when (node.getAttribute("type")) {
                                        "primary" -> titleOriginal = node.getAttribute("value")
                                        //"alternate" -> titleAlternatives.add(node.getAttribute("value"))
                                    }
                                }
                                "description" -> description = node.textContent
                                "link" -> {
                                    when (node.getAttribute("type")) {
                                        "boardgameartist" -> {
                                            var id = db.addPerson(
                                                Person(
                                                    "0",
                                                    node.getAttribute("value"),
                                                    node.getAttribute("id").toInt()
                                                )
                                            )
                                            artists.add(id)
                                        }

                                        "boardgamedesigner" -> {
                                            var id = db.addPerson(
                                                Person(
                                                    "0",
                                                    node.getAttribute("value"),
                                                    node.getAttribute("id").toInt()
                                                )
                                            )
                                            designers.add(id)
                                        }

                                        "boardgameexpansion" -> {
                                            dodatki.add(
                                                Dodatek(
                                                    "0",
                                                    "0",
                                                    node.getAttribute("id"),
                                                    node.getAttribute("value")
                                                )
                                            )
                                        }
                                    }
                                }
                                "statistics" -> {
                                    val childrenStats = node.childNodes

                                    for (s in 0..childrenStats.length - 1) {
                                        val stat = childrenStats.item(s)

                                        if (stat is Element) {

                                            when (stat.nodeName) {
                                                "ratings" -> {
                                                    val childrenRatings = stat.childNodes

                                                    for (r in 0..childrenRatings.length - 1) {
                                                        val ratings = childrenRatings.item(r)

                                                        if (ratings is Element) {

                                                            when (ratings.nodeName) {
                                                                "ranks" -> {
                                                                    val childrenRanks =
                                                                        ratings.childNodes

                                                                    for (rk in 0..childrenRanks.length - 1) {
                                                                        val ranks =
                                                                            childrenRanks.item(rk)

                                                                        if (ranks is Element) {

                                                                            when (ranks.nodeName) {
                                                                                "rank" -> {
                                                                                    if (ranks.getAttribute(
                                                                                            "type"
                                                                                        ) ==
                                                                                        "subtype" &&
                                                                                        ranks.getAttribute(
                                                                                            "name"
                                                                                        ) ==
                                                                                        "boardgame"
                                                                                    ) {

                                                                                        try {
                                                                                            rank =
                                                                                                ranks.getAttribute(
                                                                                                    "value"
                                                                                                )
                                                                                                    .toString()
                                                                                                    .toInt()
                                                                                        } catch (e: NumberFormatException) {
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


        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        val currentDate = sdf.format(Date())
        db.addHistory(Historia("0", ID.toString(), currentDate.toString(), rank.toString()))

        db.updateRank(ID.toString(), rank.toString().toInt())

    }

    fun loadAllGameData2(){
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

    fun check(xmlDoc: Document): Boolean {
        val items: NodeList = xmlDoc.getElementsByTagName("message")

        if (items.length > 0) {
            val itemNode: Node = items.item(0)

            if (itemNode.nodeType == Node.ELEMENT_NODE) {
                val elem = itemNode as Element

                Toast.makeText(getActivity(), elem.textContent, Toast.LENGTH_SHORT).show()
            }
            return false
        }
        return true
    }

    fun loadData(): MutableList<FoundGame> {
        val filename = "user2.xml"
        val path = filesDir
        val inDir = File(path, "XML")
        var current: MutableList<FoundGame> = mutableListOf()

        var titleOriginal: String = ""
        var titleAlternatives: MutableList<String> = arrayListOf()
        var year: Int? = null
        var designers: MutableList<String> = arrayListOf()
        var artists: MutableList<String> = arrayListOf()
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

        if (inDir.exists()) {
            val file = File(inDir, filename)
            if (file.exists()) {
                val xmlDoc: Document =
                    DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file)

                xmlDoc.documentElement.normalize()

                if (check(xmlDoc)) {
                    val items = xmlDoc.getElementsByTagName("item")

                    for (i in 0..items.length - 1) {
                        val itemNode: Node = items.item(i)

                        if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
                            val elem = itemNode as Element
                            val children = elem.childNodes

                            when (elem.getAttribute("subtype")) {
                                "boardgame" -> type = elem.getAttribute("subtype")
                                //"boardgameexpansion" -> type = elem.getAttribute("subtype")
                                else -> type = null
                            }
                            gameId = elem.getAttribute("objectid").toInt()

                            for (j in 0..children.length - 1) {
                                val node = children.item(j)
                                if (node is Element) {
                                    when (node.nodeName) {
                                        "yearpublished" -> year = node.textContent.toInt()
                                        "name" -> titleOriginal = node.textContent
                                        "thumbnail" -> img = node.textContent
                                        "stats" -> {
                                            val childrenStats = node.childNodes

                                            for (s in 0..childrenStats.length - 1) {
                                                val stat = childrenStats.item(s)

                                                if (stat is Element) {

                                                    when (stat.nodeName) {
                                                        "rating" -> {
                                                            val childrenRatings = stat.childNodes

                                                            for (r in 0..childrenRatings.length - 1) {
                                                                val ratings =
                                                                    childrenRatings.item(r)

                                                                if (ratings is Element) {

                                                                    when (ratings.nodeName) {
                                                                        "ranks" -> {
                                                                            val childrenRanks =
                                                                                ratings.childNodes

                                                                            for (rk in 0..childrenRanks.length - 1) {
                                                                                val ranks =
                                                                                    childrenRanks.item(
                                                                                        rk
                                                                                    )

                                                                                if (ranks is Element) {

                                                                                    when (ranks.nodeName) {
                                                                                        "rank" -> {
                                                                                            if (ranks.getAttribute(
                                                                                                    "type"
                                                                                                ) ==
                                                                                                "subtype" &&
                                                                                                ranks.getAttribute(
                                                                                                    "name"
                                                                                                ) ==
                                                                                                "boardgame"
                                                                                            ) {

                                                                                                try {
                                                                                                    rank =
                                                                                                        ranks.getAttribute(
                                                                                                            "value"
                                                                                                        )
                                                                                                            .toString()
                                                                                                            .toInt()
                                                                                                } catch (e: NumberFormatException) {
                                                                                                    Log.i(
                                                                                                        "Exception: ",
                                                                                                        gameId.toString()
                                                                                                    )
                                                                                                    rank =
                                                                                                        0
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

                            if (type != null) {
                                current.add(
                                    FoundGame(
                                        type, gameId.toString(),
                                        titleOriginal, year.toString()
                                    )
                                )

                            }
                        }
                    }
                }
            }
        }

        return current
    }
}