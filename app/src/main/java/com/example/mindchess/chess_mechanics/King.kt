package com.example.mindchess.chess_mechanics

import android.graphics.Bitmap
import com.example.mindchess.Coordinate
import com.example.mindchess.common.BOARD_TILES
import com.example.mindchess.isOnBoard

class King(
    override val team: Int,
    override var coordinate: Coordinate,
    override val image: Bitmap?
) : Piece() {

    override val name = "KING"
    override val value = 4

    override fun findPossibleMoves(piece_setup: MutableMap<Coordinate, Piece>) {


        for (i in 0..8) {
            val temp_coordinate = Coordinate(
                Math.round(Math.cos(i * Math.PI / 4)).toInt(),
                Math.round(Math.sin(i * Math.PI / 4)).toInt()
            )

            if (temp_coordinate.isOnBoard() && (piece_setup[temp_coordinate] == null || piece_setup[temp_coordinate]!!.team * team == -1)) { //&& piece_setup[temp_coordinate]!!.id != "KING"
                legal_moves.add(temp_coordinate)
            }
        }


        if (move_count == 0) {
            if (!piece_setup.keys.contains(Coordinate(coordinate.x + 1, coordinate.y)) &&
                !piece_setup.keys.contains(Coordinate(coordinate.x + 2, coordinate.y)) &&
                piece_setup[Coordinate(coordinate.x + 3, coordinate.y)] != null &&
                piece_setup[Coordinate(coordinate.x + 3, coordinate.y)]!!.name == "ROOK" &&
                piece_setup[Coordinate(coordinate.x + 3, coordinate.y)]!!.move_count == 0) {
                    legal_moves.add(Coordinate(coordinate.x + 2, coordinate.y))
            } else if (
                !piece_setup.keys.contains(Coordinate(coordinate.x - 1, coordinate.y)) &&
                !piece_setup.keys.contains(Coordinate(coordinate.x - 2, coordinate.y)) &&
                !piece_setup.keys.contains(Coordinate(coordinate.x - 3, coordinate.y)) &&
                piece_setup[Coordinate(coordinate.x - 4, coordinate.y)] != null &&
                piece_setup[Coordinate(coordinate.x - 4, coordinate.y)]!!.name == "ROOK" &&
                piece_setup[Coordinate(coordinate.x - 4, coordinate.y)]!!.move_count == 0) {
                    legal_moves.add(Coordinate(coordinate.x - 3, coordinate.y))
            }
        }


    }

    override fun removeIllegalMoves(possible_moves: ArrayList<Coordinate>) {

        // simulate a move, does it cause a check? if yes, remove move
        TODO("Not yet implemented")
    }

    override fun move(
        piece_setup: MutableMap<Coordinate, Piece>,
        destination_coordinate: Coordinate
    ) {

        if (destination_coordinate.x == coordinate.x + 2) {
            super.move(piece_setup, destination_coordinate)
            piece_setup[Coordinate(coordinate.x + 3, coordinate.y)]?.coordinate = Coordinate(coordinate.x + 1, coordinate.y)
        } else if (destination_coordinate.x == coordinate.x - 3) {
            super.move(piece_setup, destination_coordinate)
            piece_setup[Coordinate(coordinate.x - 4, coordinate.y)]?.coordinate = Coordinate(coordinate.x - 2, coordinate.y)
        }

    }
}


