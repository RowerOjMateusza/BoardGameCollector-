package com.example.boardgamecollector.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.boardgamecollector.BGG

class MyDBHandler(context: Context, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

        companion object{
            private const val DATABASE_NAME = "games9.db"
            private const val DATABASE_VERSION = 1
            const val TABLE_GAMES = "games"
            const val COLUMN_ID = "_id"
            const val COLUMN_GAMENAME = "game_name"
            const val COLUMN_GAMENAMEORG = "game_org"
            const val COLUMN_YEAR = "year"
            const val COLUMN_DESCRIPTION = "description"
            const val COLUMN_ORDERDATE = "order_date"
            const val COLUMN_DATADODANIA = "data_dodania"
            const val COLUMN_COST = "cost"
            const val COLUMN_SCD = "scd"
            const val COLUMN_KOD = "kod"
            const val COLUMN_BGGID = "bgg_id"
            const val COLUMN_PRODUCTIONCODE = "production_code"
            const val COLUMN_RANKING = "ranking"
            const val COLUMN_TYPE = "type"
            const val COLUMN_COMMENT = "comment"
            const val COLUMN_THUMBNAIL = "thumbnail"
            const val COLUMN_LOCATION = "location"

            const val TABLE_OSOBY = "osoby"
            const val COLUMN_NAME = "name"

            const val TABLE_ARTIST_RELATION ="artist_relation"
            const val COLUMN_GAMEID = "game_id"
            const val COLUMN_ARTISTID = "artist_id"

            const val TABLE_DESIGNER_RELATION ="designer_relation"
            const val COLUMN_DESIGNERID = "designer_id"

            const val TABLE_LOCATION="location"
            const val TABLE_DODATKI="dodatki"
            const val TABLE_HISTORY="history"
            const val COLUMN_DATE = "data"

        }


    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_GAMES_TABLE = ("CREATE TABLE " +
                TABLE_GAMES + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY," +
                COLUMN_GAMENAME + " TEXT," +
                COLUMN_GAMENAMEORG + " TEXT," +
                COLUMN_YEAR + " INTEGER," +
                COLUMN_DESCRIPTION + " TEXT," +
                COLUMN_ORDERDATE + " TEXT," +
                COLUMN_DATADODANIA + " TEXT," +
                COLUMN_COST + " TEXT," +
                COLUMN_SCD + " TEXT," +
                COLUMN_KOD+ " TEXT," +
                COLUMN_BGGID + " INTEGER," +
                COLUMN_PRODUCTIONCODE + " TEXT," +
                COLUMN_RANKING + " INTEGER," +
                COLUMN_TYPE + " TEXT," +
                COLUMN_COMMENT + " TEXT," +
                COLUMN_THUMBNAIL + " TEXT," +
                COLUMN_LOCATION + " INTEGER" + ")")

        db.execSQL(CREATE_GAMES_TABLE)

        val CREATE_PERSONS_TABLE=("CREATE TABLE " +
                TABLE_OSOBY + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY," +
                COLUMN_NAME + " TEXT," +
                COLUMN_BGGID+ " INTEGER" +")")

        db.execSQL(CREATE_PERSONS_TABLE)

        val CREATE_ARTIST_RELATION_TABLE=("CREATE TABLE " +
                TABLE_ARTIST_RELATION + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY," +
                COLUMN_GAMEID + " TEXT," +
                COLUMN_ARTISTID + " INTEGER" +")")

        db.execSQL(CREATE_ARTIST_RELATION_TABLE)

        val CREATE_DESIGNER_RELATION_TABLE=("CREATE TABLE " +
                TABLE_DESIGNER_RELATION + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY," +
                COLUMN_GAMEID + " TEXT," +
                COLUMN_DESIGNERID + " INTEGER" +")")

        db.execSQL(CREATE_DESIGNER_RELATION_TABLE)
        val CREATE_LOCATION_TABLE=("CREATE TABLE " +
                TABLE_LOCATION + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY," +
                COLUMN_NAME + " TEXT," +
                COLUMN_COMMENT + " TEXT" + ")")

        db.execSQL(CREATE_LOCATION_TABLE)

        val CREATE_DODATKI_TABLE=("CREATE TABLE " +
                TABLE_DODATKI + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY," +
                COLUMN_GAMEID + " TEXT," +
                COLUMN_BGGID + " INTEGER," +
                COLUMN_NAME + " TEXT" + ")")

        db.execSQL(CREATE_DODATKI_TABLE)

        val CREATE_HISTORY_TABLE=("CREATE TABLE " +
                TABLE_HISTORY + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY," +
                COLUMN_GAMEID + " TEXT," +
                COLUMN_DATE + " INTEGER," +
                COLUMN_RANKING + " TEXT" + ")")

        db.execSQL(CREATE_HISTORY_TABLE)
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_GAMES")
        onCreate(db)
    }
    fun clear()
    {   val db = this.writableDatabase
        db.execSQL("DELETE FROM $TABLE_GAMES")
        db.execSQL("DELETE FROM $TABLE_OSOBY")
        db.execSQL("DELETE FROM $TABLE_ARTIST_RELATION")
        db.execSQL("DELETE FROM $TABLE_DESIGNER_RELATION")
        db.execSQL("DELETE FROM $TABLE_LOCATION")
        db.execSQL("DELETE FROM $TABLE_DODATKI")
        db.execSQL("DELETE FROM $TABLE_HISTORY")
    }
    fun addGame(game: Game):Long{
        val values = ContentValues()
        values.put(COLUMN_GAMENAME, game.title)
        values.put(COLUMN_GAMENAMEORG, game.titleOriginal)
        values.put(COLUMN_YEAR, game.year)
        values.put(COLUMN_DESCRIPTION, game.description)
        values.put(COLUMN_ORDERDATE, game.orderDate)
        values.put(COLUMN_DATADODANIA, game.datadodania)
        values.put(COLUMN_COST, game.koszt)
        values.put(COLUMN_SCD, game.scd)
        values.put(COLUMN_KOD, game.kod)
        values.put(COLUMN_BGGID, game.bggId)
        values.put(COLUMN_PRODUCTIONCODE, game.productionCode)
        values.put(COLUMN_RANKING, game.ranking)
        values.put(COLUMN_TYPE, game.type)
        values.put(COLUMN_COMMENT, game.comment)
        values.put(COLUMN_THUMBNAIL, game.img)
        values.put(COLUMN_LOCATION, game.location)
        val db = this.writableDatabase
        val id=db.insert(TABLE_GAMES, null, values)
        db.close()
        return id;
    }

    fun addPerson(person:Person):Long
    {
        if(person.bggId>0)
        {
            val query = "SELECT * FROM $TABLE_OSOBY WHERE $COLUMN_BGGID LIKE \"${person.bggId}\""
            val db = this.writableDatabase
            val cursor = db.rawQuery(query, null)
            if(cursor.moveToFirst()){
                return cursor.getLong(0)
            }
        }

        val values = ContentValues()
        values.put(COLUMN_NAME,person.name)
        values.put(COLUMN_BGGID,person.bggId)
        val db = this.writableDatabase
        val id = db.insert(TABLE_OSOBY, null, values)
        db.close()
        return id;
    }

    fun addArtistRelation(relation: Relation)
    {

        val values = ContentValues()
        values.put(COLUMN_ARTISTID,relation.firstId)
        values.put(COLUMN_GAMEID,relation.secondId)
        val db = this.writableDatabase
        db.insert(TABLE_ARTIST_RELATION, null, values)
        db.close()
    }

    fun addDesignerRelation(relation: Relation)
    {

        val values = ContentValues()
        values.put(COLUMN_DESIGNERID,relation.firstId)
        values.put(COLUMN_GAMEID,relation.secondId)
        val db = this.writableDatabase
        db.insert(TABLE_DESIGNER_RELATION, null, values)
        db.close()
    }
    fun addLocation(location: Location)
    {
        val values = ContentValues()
        values.put(COLUMN_NAME,location.name)
        values.put(COLUMN_COMMENT,location.comment)
        val db = this.writableDatabase
        db.insert(TABLE_LOCATION, null, values)
        db.close()
    }
    fun addDodatek(dodatek: Dodatek)
    {
        val values = ContentValues()
        values.put(COLUMN_GAMEID,dodatek.GameID)
        values.put(COLUMN_BGGID,dodatek.bggID)
        values.put(COLUMN_NAME,dodatek.name)
        val db = this.writableDatabase
        db.insert(TABLE_DODATKI, null, values)
        db.close()
    }
    fun addHistory(historia: Historia)
    {
        val values = ContentValues()
        values.put(COLUMN_GAMEID,historia.GameID)
        values.put(COLUMN_DATE,historia.date)
        values.put(COLUMN_RANKING,historia.ranking)
        val db = this.writableDatabase
        db.insert(TABLE_HISTORY, null, values)
        db.close()
    }



    fun findGame(id: String): Game?{
        val query = "SELECT * FROM $TABLE_GAMES WHERE $COLUMN_ID LIKE \"$id\""
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)
        var game: Game? = null

        if(cursor.moveToFirst()){
            val ID = cursor.getString(0)
            val title = cursor.getString(1)
            val titleOriginal = cursor.getString(2)
            val year = cursor.getInt(3)
            val description = cursor.getString(4)
            val orderDate = cursor.getString(5)
            val addedDate = cursor.getString(6)
            val cost = cursor.getString(7)
            val scd = cursor.getString(8)
            val code = cursor.getString(9)
            val gameId = cursor.getInt(10)
            val productionCode = cursor.getString(11)
            val rank = cursor.getInt(12)
            val type = cursor.getString(13)
            val comment = cursor.getString(14)
            val img = cursor.getString(15)
            val locationId = cursor.getInt(16)

            game = Game(ID, title,titleOriginal, year,
                description, orderDate, addedDate, cost, scd, code, gameId,
                productionCode, rank, type, comment, img, locationId)
            cursor.close()
        }
        db.close()
        return game
    }


    fun getGameArtists(id: String): String
    {
        val query = "SELECT $TABLE_OSOBY.$COLUMN_NAME from $TABLE_ARTIST_RELATION join $TABLE_OSOBY ON $TABLE_ARTIST_RELATION.$COLUMN_ARTISTID = $TABLE_OSOBY.$COLUMN_ID WHERE $TABLE_ARTIST_RELATION.$COLUMN_GAMEID LIKE \"$id\""
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)
        var string :String = ""
        if(cursor.moveToFirst()){
            var next = true
            while(next) {
                val name = cursor.getString(0)
               // Log.i("artist", name)
                string= string +name+", "
                next = cursor.moveToNext()
            }
            cursor.close()
        }
        db.close()
        return string
    }

    fun getGameDesigner(id: String): String
    {
        Log.i("Designers","elo")
        val query = "SELECT $TABLE_OSOBY.$COLUMN_NAME from $TABLE_DESIGNER_RELATION join $TABLE_OSOBY ON $TABLE_DESIGNER_RELATION.$COLUMN_DESIGNERID = $TABLE_OSOBY.$COLUMN_ID WHERE $TABLE_DESIGNER_RELATION.$COLUMN_GAMEID LIKE \"$id\""
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)
        var string :String = ""
        if(cursor.moveToFirst()){
            var next = true
            while(next) {
                val name = cursor.getString(0)
                string= string +name+", "
                next = cursor.moveToNext()
            }
            cursor.close()
        }
        db.close()
        return string
    }

    fun get_all_short(sort:String): MutableList<Game>{
        var games: MutableList<Game> = arrayListOf()
        val query = "SELECT * FROM $TABLE_GAMES order by $sort"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)

        if(cursor.moveToFirst()){
            var next = true
            while(next){
                val ID = cursor.getString(0)
                val title = cursor.getString(1)
                val titleOriginal = cursor.getString(2)
                val year = cursor.getInt(3)
                val description = cursor.getString(4)
                val orderDate = cursor.getString(5)
                val addedDate = cursor.getString(6)
                val cost = cursor.getString(7)
                val scd = cursor.getString(8)
                val code = cursor.getString(9)
                val gameId = cursor.getInt(10)
                val productionCode = cursor.getString(11)
                val rank = cursor.getInt(12)
                val type = cursor.getString(13)
                val comment = cursor.getString(14)
                val img = cursor.getString(15)
                val locationId = cursor.getInt(16)

                games.add(Game(ID,title,titleOriginal, year,
                    description, orderDate, addedDate, cost, scd, code, gameId,
                    productionCode, rank, type, comment, img, locationId))

                next = cursor.moveToNext()
            }
            cursor.close()
        }
        db.close()
        return games
    }
    fun getAllByLocation(location:String):MutableList<Game>
    {
        var games: MutableList<Game> = arrayListOf()
        val query = "SELECT * FROM $TABLE_GAMES WHERE $COLUMN_LOCATION LIKE \"$location\" order by 1"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)

        if(cursor.moveToFirst()){
            var next = true
            while(next){
                val ID = cursor.getString(0)
                val title = cursor.getString(1)
                val titleOriginal = cursor.getString(2)
                val year = cursor.getInt(3)
                val description = cursor.getString(4)
                val orderDate = cursor.getString(5)
                val addedDate = cursor.getString(6)
                val cost = cursor.getString(7)
                val scd = cursor.getString(8)
                val code = cursor.getString(9)
                val gameId = cursor.getInt(10)
                val productionCode = cursor.getString(11)
                val rank = cursor.getInt(12)
                val type = cursor.getString(13)
                val comment = cursor.getString(14)
                val img = cursor.getString(15)
                val locationId = cursor.getInt(16)

                games.add(Game(ID,title,titleOriginal, year,
                    description, orderDate, addedDate, cost, scd, code, gameId,
                    productionCode, rank, type, comment, img, locationId))

                next = cursor.moveToNext()
            }
            cursor.close()
        }
        db.close()
        return games
    }

    fun get_all_location(): MutableList<Location>
    {
        var locations: MutableList<Location> = arrayListOf()
        val query = "SELECT * FROM $TABLE_LOCATION order by 1"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)

        if(cursor.moveToFirst()){
            var next = true
            while(next){
                val ID = cursor.getString(0)
                val name = cursor.getString(1)
                val comment = cursor.getString(2)

                locations.add(Location(ID.toString(),name.toString(),comment.toString()))

                next = cursor.moveToNext()
            }
            cursor.close()
        }
        db.close()
        return locations
    }
    fun get_all_location_name(): MutableList<String>
    {
        var locations: MutableList<String> = arrayListOf()
        val query = "SELECT * FROM $TABLE_LOCATION order by 1"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)

        if(cursor.moveToFirst()){
            var next = true
            while(next){

                val name = cursor.getString(1)
                locations.add(name)
                next = cursor.moveToNext()
            }
            cursor.close()
        }
        db.close()
        return locations
    }

    fun get_dodatki(id:String): MutableList<Dodatek>
    {
        var dodatki: MutableList<Dodatek> = arrayListOf()
        val query = "SELECT * FROM $TABLE_DODATKI WHERE  $COLUMN_GAMEID LIKE \"$id\"order by 1"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)

        if(cursor.moveToFirst()){
            var next = true
            while(next){
                val ID = cursor.getString(0)
                val gameId = cursor.getString(1)
                val bggId = cursor.getString(2)
                val name = cursor.getString(3)


                dodatki.add(Dodatek(ID.toString(),gameId.toString(),bggId.toString(),name.toString()))

                next = cursor.moveToNext()
            }
            cursor.close()
        }
        db.close()
        return dodatki
    }

    fun get_history(id: String) :MutableList<Historia>
    {
        var historie: MutableList<Historia> = arrayListOf()
        val query = "SELECT * FROM $TABLE_HISTORY WHERE  $COLUMN_GAMEID LIKE \"$id\"order by 1"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)

        if(cursor.moveToFirst()){
            var next = true
            while(next){
                val ID = cursor.getString(0)
                val gameId = cursor.getString(1)
                val date = cursor.getString(2)
                val ranking = cursor.getString(3)


                historie.add(Historia(ID.toString(),gameId.toString(),date.toString(),ranking.toString()))

                next = cursor.moveToNext()
            }
            cursor.close()
        }
        db.close()
        return historie
    }
    fun findLocation(id: String): Location?
    {
        val query = "SELECT * FROM $TABLE_LOCATION WHERE $COLUMN_ID LIKE \"$id\""
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)
        var location: Location? = null

        if(cursor.moveToFirst()){
            val ID = cursor.getString(0)
            val name = cursor.getString(1)
            val comment = cursor.getString(2)


            location = Location(ID.toString(),name.toString(),comment.toString())
            cursor.close()
        }
        db.close()
        return location
    }
    fun updateGame(gameId: String?, game: Game?): Boolean{
        val values = ContentValues()
        if(game != null && gameId != null){
            values.put(COLUMN_GAMENAME, game.title)
            values.put(COLUMN_GAMENAMEORG, game.titleOriginal)
            values.put(COLUMN_YEAR, game.year)
            values.put(COLUMN_DESCRIPTION, game.description)
            values.put(COLUMN_ORDERDATE, game.orderDate)
            values.put(COLUMN_DATADODANIA, game.datadodania)
            values.put(COLUMN_COST, game.koszt)
            values.put(COLUMN_SCD, game.scd)
            values.put(COLUMN_KOD, game.kod)
            values.put(COLUMN_BGGID, game.bggId)
            values.put(COLUMN_PRODUCTIONCODE, game.productionCode)
            values.put(COLUMN_RANKING, game.ranking)
            values.put(COLUMN_TYPE, game.type)
            values.put(COLUMN_COMMENT, game.comment)
            values.put(COLUMN_THUMBNAIL, game.img)
            values.put(COLUMN_LOCATION, game.location)

            val db = this.writableDatabase
            db.update(MyDBHandler.TABLE_GAMES, values,
                "${MyDBHandler.COLUMN_ID}=?", arrayOf(gameId.toString()))
            db.close()
            return true
        }
        return false
    }
    fun updateRank(gameId: String?, rank:Int): Boolean{
        val values = ContentValues()

        values.put(COLUMN_RANKING, rank)
        val db = this.writableDatabase
        db.update(MyDBHandler.TABLE_GAMES, values,
                "${MyDBHandler.COLUMN_ID}=?", arrayOf(gameId.toString()))
        db.close()
        return true

        return false
    }

    fun updateLocation(gameId: String?, location:Location?): Boolean{
        val values = ContentValues()
        if(location != null && gameId != null){
            values.put(COLUMN_NAME, location.name)
            values.put(COLUMN_COMMENT, location.comment)

            val db = this.writableDatabase
            db.update(MyDBHandler.TABLE_LOCATION, values,
                "${MyDBHandler.COLUMN_ID}=?", arrayOf(gameId.toString()))
            db.close()
            return true
        }
        return false
    }


    fun deleteGame(id: String): Boolean{
        var result = false
        val query = "SELECT * FROM $TABLE_GAMES WHERE $COLUMN_ID LIKE $id"

        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)
        if(cursor.moveToFirst()){
            val id = cursor.getInt(0)
            db.delete(TABLE_GAMES, COLUMN_ID+ " = ?", arrayOf(id.toString()))
            cursor.close()
            result = true
        }
        db.close()
        return result
    }

    fun deleteLocation(id: String): Boolean{
        var result = false
        val query = "SELECT * FROM $TABLE_LOCATION WHERE $COLUMN_ID LIKE $id"

        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)
        if(cursor.moveToFirst()){
            val id = cursor.getInt(0)
            db.delete(TABLE_LOCATION, COLUMN_ID+ " = ?", arrayOf(id.toString()))
            cursor.close()
            result = true
        }
        db.close()
        return result
    }
}
