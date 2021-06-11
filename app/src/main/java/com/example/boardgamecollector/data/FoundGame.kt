package com.example.boardgamecollector.data

class FoundGame(var type: String?, var id: String, var title: String?, var year: String?) {

    override fun toString(): String {
        return id
    }
}