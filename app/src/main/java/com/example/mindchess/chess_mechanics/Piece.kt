package com.example.mindchess.chess_mechanics

import android.graphics.Bitmap
import com.example.mindchess.Coordinate

abstract class Piece {

    abstract val team: Int
    abstract val name: String
    abstract val value: Int
    abstract val image: Bitmap?
    abstract var coordinate: Coordinate

    val legal_moves: ArrayList<Coordinate> = arrayListOf()
    var move_count: Int = 0


    companion object {
        var last_moved_piece: Piece? = null
    }

    open fun findPossibleMoves(piece_setup: MutableMap<Coordinate, Piece>) {
        legal_moves.clear()
    }
    abstract fun removeIllegalMoves(possible_moves: ArrayList<Coordinate>)

    open fun move(piece_setup: MutableMap<Coordinate, Piece>, destination_coordinate: Coordinate) {
        piece_setup.remove(this.coordinate)
        piece_setup[destination_coordinate] = this


        coordinate = destination_coordinate
        move_count += 1
        last_moved_piece = this
    }


}