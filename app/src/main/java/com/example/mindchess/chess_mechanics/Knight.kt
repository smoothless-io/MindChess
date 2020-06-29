package com.example.mindchess.chess_mechanics

class Knight() {

    var id = ""

    constructor(id: String) : this() {
        this.id = id
    }

    constructor(other: Knight) : this() {
        this.id = other.id
    }

}



