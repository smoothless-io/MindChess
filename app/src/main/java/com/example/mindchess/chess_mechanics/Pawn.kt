package com.example.mindchess.chess_mechanics

class Pawn() {

    var id = ""

    constructor(id: String) : this() {
        this.id = id
    }

    constructor(other: Pawn) : this() {
        this.id = other.id
    }

}



