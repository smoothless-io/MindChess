package com.example.mindchess.chess_mechanics

import android.widget.Toast

class ChessMatch(private var whiteAI: Boolean, private var blackAI: Boolean) {

    internal var whiteTurn = true
    internal var check = false
    internal var score = 0.0
    internal var result = ""

    internal var squares = Array(64) { 0 }

    internal var pieces = mapOf(
        "white" to arrayListOf(
            Piece("Pawn"),
            Piece("Bishop"),
            Piece("Knight")
        ),

        "black" to arrayListOf(
            Piece("Pawn"),
            Piece("Bishop"),
            Piece("Knight")
        )
    )


    fun makeMove() : Boolean {
        return true
    }



}

