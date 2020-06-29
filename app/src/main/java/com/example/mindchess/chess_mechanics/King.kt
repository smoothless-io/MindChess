package com.example.mindchess.chess_mechanics

class King() {

    var id = ""

    constructor(id: String) : this() {
        this.id = id
    }

    constructor(other: King) : this() {
        this.id = other.id
    }

}



