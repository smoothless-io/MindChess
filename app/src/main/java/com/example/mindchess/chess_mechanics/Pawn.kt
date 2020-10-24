package com.example.mindchess.chess_mechanics

import android.graphics.Bitmap
import com.example.mindchess.Coordinate

class Pawn(
    override val team: Int,
    override var coordinate: Coordinate,
    override val image: Bitmap?
) : Piece() {

    override val name = "PAWN"
    override val value = 1

    val en_passant = false

    override fun findPossibleMoves(piece_setup: MutableMap<Coordinate, Piece>) {

        super.findPossibleMoves(piece_setup)

        if (piece_setup[Coordinate(coordinate.x, coordinate.y + team)] == null) {
            legal_moves.add(Coordinate(coordinate.x, coordinate.y + team))
        }

        if (move_count == 0 && piece_setup[Coordinate(coordinate.x, coordinate.y + team)] == null &&
            piece_setup[Coordinate(coordinate.x, coordinate.y + 2 * team)] == null) {
            legal_moves.add(Coordinate(coordinate.x, coordinate.y + 2 * team))
        }

        if (piece_setup[Coordinate(coordinate.x + 1, coordinate.y + team)] != null &&
            piece_setup[Coordinate(coordinate.x + 1, coordinate.y + team)]!!.team * team == -1) {
            legal_moves.add(Coordinate(coordinate.x, coordinate.y + 2 * team))
        }

        if (piece_setup[Coordinate(coordinate.x - 1, coordinate.y + team)] != null &&
            piece_setup[Coordinate(coordinate.x - 1, coordinate.y + team)]!!.team * team == -1) {
            legal_moves.add(Coordinate(coordinate.x, coordinate.y + 2 * team))
        }

        if (coordinate.y == 3 || coordinate.y == 4)
            for (i in arrayOf(-1, 1)) {
                if (piece_setup[Coordinate(coordinate.x + i, coordinate.y)] != null &&
                piece_setup[Coordinate(coordinate.x + i, coordinate.y)]!!.team * team == -1 &&
                piece_setup[Coordinate(coordinate.x + i, coordinate.y)]!!.name == "PAWN" &&
                piece_setup[Coordinate(coordinate.x + i, coordinate.y)]!!.move_count == 1 &&
                piece_setup[Coordinate(coordinate.x + i, coordinate.y)] == last_moved_piece) {
                    legal_moves.add(Coordinate(coordinate.x + i, coordinate.y + team))
                }
        }


    }


    override fun removeIllegalMoves(possible_moves: ArrayList<Coordinate>) {
        TODO("Not yet implemented")
    }

    override fun move(
        piece_setup: MutableMap<Coordinate, Piece>,
        destination_coordinate: Coordinate
    ) {

        if (Math.abs(destination_coordinate.x - coordinate.x) == 1 && destination_coordinate.y == coordinate.y + team && piece_setup[destination_coordinate] == null) {
            piece_setup.remove(Coordinate(destination_coordinate.x, destination_coordinate.y - team))
        }


        super.move(piece_setup, destination_coordinate)



    }

}