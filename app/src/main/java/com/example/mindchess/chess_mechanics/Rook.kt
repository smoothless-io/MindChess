package com.example.mindchess.chess_mechanics

class Rook() {

    var id = ""

    constructor(id: String) : this() {
        this.id = id
    }

    constructor(other: Rook) : this() {
        this.id = other.id
    }

}



