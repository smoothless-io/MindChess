package com.example.mindchess.chess_mechanics

class Bishop() {

    var id = ""

    constructor(id: String) : this() {
        this.id = id
    }

    constructor(other: Bishop) : this() {
        this.id = other.id
    }

}



