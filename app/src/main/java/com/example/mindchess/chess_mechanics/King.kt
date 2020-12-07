package com.example.mindchess.chess_mechanics

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.mindchess.Coordinate
import com.example.mindchess.R
import com.example.mindchess.common.BOARD_TILES
import com.example.mindchess.common.toInt
import com.example.mindchess.isOnBoard

class King(
    override val team: Int,
    override var coordinate: Coordinate,

    override var move_count: Int = 0,
    override val legal_moves: ArrayList<Coordinate> = arrayListOf()
) : Piece() {

    override val name = "KING"
    override val value = 4

    override fun findPossibleMoves(piece_setup: Array<MutableMap<Coordinate, Piece>>, last_moved_piece: Piece?) : Boolean {

        var yields_check = super.findPossibleMoves(piece_setup, last_moved_piece)

        val piece_setup_mixed = getPieceSetupMixed(piece_setup)

        for (i in 0..7) {
            val step = Coordinate(
                Math.round(Math.cos(i * Math.PI / 4)).toInt(),
                Math.round(Math.sin(i * Math.PI / 4)).toInt()
            )

            val temp_coordinate = Coordinate(coordinate.x + step.x, coordinate.y + step.y)

            if (temp_coordinate.isOnBoard() && (piece_setup_mixed[temp_coordinate] == null || piece_setup_mixed[temp_coordinate]!!.team * team == -1)) { //&& piece_setup_mixed[temp_coordinate]!!.id != "KING"
                if (piece_setup_mixed[temp_coordinate] != null && piece_setup_mixed[temp_coordinate]!!.team * team == -1 && piece_setup_mixed[temp_coordinate]!!.name == "KING") {
                    yields_check = true
                }
                legal_moves.add(temp_coordinate)
            }
        }


        if (move_count == 0) {
            if (!piece_setup_mixed.keys.contains(Coordinate(coordinate.x + 1, coordinate.y)) &&
                !piece_setup_mixed.keys.contains(Coordinate(coordinate.x + 2, coordinate.y)) &&
                piece_setup_mixed[Coordinate(coordinate.x + 3, coordinate.y)] != null &&
                piece_setup_mixed[Coordinate(coordinate.x + 3, coordinate.y)]!!.name == "ROOK" &&
                piece_setup_mixed[Coordinate(coordinate.x + 3, coordinate.y)]!!.move_count == 0) {
                    legal_moves.add(Coordinate(coordinate.x + 2, coordinate.y))
            } else if (
                !piece_setup_mixed.keys.contains(Coordinate(coordinate.x - 1, coordinate.y)) &&
                !piece_setup_mixed.keys.contains(Coordinate(coordinate.x - 2, coordinate.y)) &&
                !piece_setup_mixed.keys.contains(Coordinate(coordinate.x - 3, coordinate.y)) &&
                piece_setup_mixed[Coordinate(coordinate.x - 4, coordinate.y)] != null &&
                piece_setup_mixed[Coordinate(coordinate.x - 4, coordinate.y)]!!.name == "ROOK" &&
                piece_setup_mixed[Coordinate(coordinate.x - 4, coordinate.y)]!!.move_count == 0) {
                    legal_moves.add(Coordinate(coordinate.x - 2, coordinate.y))
            }
        }

        return yields_check


    }


    override fun move(
        piece_setup: Array<MutableMap<Coordinate, Piece>>,
        destination_coordinate: Coordinate
    ) {

        val team_index = (team < 0).toInt()

        println("Coordinate: " + coordinate.toString())
        println("Destination coordinate: " + destination_coordinate.toString())

        if (destination_coordinate.x == coordinate.x + 2) {
            println("Im about to castle kingside")
            super.move(piece_setup, destination_coordinate)

            piece_setup[team_index][Coordinate(coordinate.x - 1, coordinate.y)] = piece_setup[team_index].remove(Coordinate(coordinate.x + 1, coordinate.y))!!
            piece_setup[team_index][Coordinate(coordinate.x - 1, coordinate.y)]!!.coordinate = Coordinate(coordinate.x - 1, coordinate.y)


        } else if (destination_coordinate.x == coordinate.x - 2) {

            println("Im about to caste queenside.")
            super.move(piece_setup, destination_coordinate)
            
            piece_setup[team_index][Coordinate(coordinate.x + 1, coordinate.y)] = piece_setup[team_index].remove(Coordinate(coordinate.x - 2, coordinate.y))!!
            piece_setup[team_index][Coordinate(coordinate.x + 1, coordinate.y)]!!.coordinate = Coordinate(coordinate.x + 1, coordinate.y)
        } else {

            super.move(piece_setup, destination_coordinate)
        }

    }

    override fun copy(): Piece {
        return King(this.team, this.coordinate, this.move_count, this.legal_moves)
    }
}


