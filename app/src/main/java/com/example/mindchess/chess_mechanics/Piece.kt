package com.example.mindchess.chess_mechanics

class Piece() {

    var id = ""

    constructor(id: String) : this() {
        this.id = id
    }

    constructor(other: Piece) : this() {
        this.id = other.id
    }

}



