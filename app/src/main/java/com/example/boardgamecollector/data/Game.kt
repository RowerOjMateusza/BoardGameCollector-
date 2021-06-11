package com.example.boardgamecollector.data

class Game(var ID:String?,var title: String?,var titleOriginal: String?, var year: Int?, var description: String?, var orderDate: String?, var datadodania: String?, var koszt: String?, var scd: String?,
           var kod: String?, var bggId: Int?, var productionCode: String?, var ranking: Int?, var type: String?, var comment: String?, var img: String?, var location: Int?) {
    override fun toString(): String {
        return bggId.toString()
    }
}