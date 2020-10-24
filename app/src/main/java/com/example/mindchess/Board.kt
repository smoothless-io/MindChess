package com.example.mindchess

import com.example.mindchess.chess_mechanics.Piece

interface Board {

    var turn: Int

    fun playMove(piece_name: String, origin_coordinate: Coordinate?, destination_coordinate: Coordinate)

    fun getPieces() : Collection<Piece>
}