package com.example.mindchess.chess_mechanics

class Queen() {

    var id = ""

    constructor(id: String) : this() {
        this.id = id
    }

    constructor(other: Queen) : this() {
        this.id = other.id
    }

}



