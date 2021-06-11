package com.example.boardgamecollector.data

class Location(var ID:String?,var name: String, var comment: String) {

    override fun toString(): String {
        return name
    }

}