package com.example.boardgamecollector.data

class Relation(var ID:String?,var firstId: Long, var secondId: Long) {

    override fun toString(): String {
        return secondId.toString()
    }

}