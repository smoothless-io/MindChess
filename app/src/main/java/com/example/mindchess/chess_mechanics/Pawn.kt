package com.example.mindchess.chess_mechanics

import android.graphics.Bitmap
import com.example.mindchess.Coordinate
import com.example.mindchess.common.toInt

class Pawn(
    override val team: Int,
    override var coordinate: Coordinate,
    override val image: Bitmap?,
    override var move_count: Int = 0,
    override val legal_moves: ArrayList<Coordinate> = arrayListOf()
) : Piece() {

    override val name = "PAWN"
    override val value = 1

    override fun findPossibleMoves(piece_setup: Array<MutableMap<Coordinate, Piece>>) : Boolean {
        var yields_check = super.findPossibleMoves(piece_setup)

        val piece_setup_mixed = getPieceSetupMixed(piece_setup)


        if (piece_setup_mixed[Coordinate(coordinate.x, coordinate.y + team)] == null) {
            legal_moves.add(Coordinate(coordinate.x, coordinate.y + team))
        }

        if (move_count == 0 && piece_setup_mixed[Coordinate(coordinate.x, coordinate.y + team)] == null &&
            piece_setup_mixed[Coordinate(coordinate.x, coordinate.y + 2 * team)] == null) {
            legal_moves.add(Coordinate(coordinate.x, coordinate.y + 2 * team))
        }

        if (piece_setup_mixed[Coordinate(coordinate.x + 1, coordinate.y + team)] != null &&
            piece_setup_mixed[Coordinate(coordinate.x + 1, coordinate.y + team)]!!.team * team == -1) {
            if (piece_setup_mixed[Coordinate(coordinate.x + 1, coordinate.y + team)]!!.name == "KING") {
                yields_check = true
            }
            legal_moves.add(Coordinate(coordinate.x + 1, coordinate.y + team))
        }

        if (piece_setup_mixed[Coordinate(coordinate.x - 1, coordinate.y + team)] != null &&
            piece_setup_mixed[Coordinate(coordinate.x - 1, coordinate.y + team)]!!.team * team == -1) {

            if (piece_setup_mixed[Coordinate(coordinate.x - 1, coordinate.y + team)]!!.name == "KING") {
                yields_check = true
            }

            legal_moves.add(Coordinate(coordinate.x - 1, coordinate.y + team))
        }

        if (coordinate.y == 3 || coordinate.y == 4)
            for (i in arrayOf(-1, 1)) {
                if (piece_setup_mixed[Coordinate(coordinate.x + i, coordinate.y)] != null &&
                piece_setup_mixed[Coordinate(coordinate.x + i, coordinate.y)]!!.team * team == -1 &&
                piece_setup_mixed[Coordinate(coordinate.x + i, coordinate.y)]!!.name == "PAWN" &&
                piece_setup_mixed[Coordinate(coordinate.x + i, coordinate.y)]!!.move_count == 1 &&
                piece_setup_mixed[Coordinate(coordinate.x + i, coordinate.y)] == last_moved_piece) {
                    legal_moves.add(Coordinate(coordinate.x + i, coordinate.y + team))
                }
        }

        return yields_check
    }



    override fun move(
        piece_setup: Array<MutableMap<Coordinate, Piece>>,
        destination_coordinate: Coordinate
    ) {

        val piece_setup_mixed = getPieceSetupMixed(piece_setup)
        val team_index = (team < 0).toInt()

        println("I dont wanna kill that mofcka")
        if (Math.abs(destination_coordinate.x - coordinate.x) == 1 && destination_coordinate.y == coordinate.y + team && piece_setup_mixed[destination_coordinate] == null) {
            println("Well Im trying to kill that mofck")
            piece_setup[1 - team_index].remove(Coordinate(destination_coordinate.x, destination_coordinate.y - team))
        }


        super.move(piece_setup, destination_coordinate)

    }

    override fun copy(): Piece {
        return Pawn(this.team, this.coordinate, this.image, this.move_count, this.legal_moves)
    }

}