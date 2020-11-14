package com.example.mindchess.chess_mechanics

import android.graphics.Bitmap
import com.example.mindchess.Coordinate
import com.example.mindchess.isOnBoard
import com.example.mindchess.move

class Queen(
    override val team: Int,
    override var coordinate: Coordinate,
    override val image: Bitmap?
) : Piece() {

    override val name = "QUEEN"
    override val value = 9

    override fun findPossibleMoves(piece_setup: Array<MutableMap<Coordinate, Piece>>) {

        super.findPossibleMoves(piece_setup)

        val piece_setup_mixed = getPieceSetupMixed(piece_setup)

        for (i in 0..7) {
            val step = Coordinate(
                Math.round(Math.cos(i * Math.PI / 4)).toInt(),
                Math.round(Math.sin(i * Math.PI / 4)).toInt()
            )

            val temp_coordinate = Coordinate(coordinate.x + step.x, coordinate.y + step.y)

            while (temp_coordinate.isOnBoard() && (piece_setup_mixed[temp_coordinate] == null || piece_setup_mixed[temp_coordinate]!!.team * team == -1)) {
                legal_moves.add(temp_coordinate.copy())

                if (piece_setup_mixed[temp_coordinate] != null) {

                    if (piece_setup_mixed[temp_coordinate]!!.name == "KING") {
                        // Figure out what
                        break
                    }

                    break
                }

                temp_coordinate.move(step)
            }

        }
    }

    override fun removeIllegalMoves(possible_moves: ArrayList<Coordinate>) {
        TODO("Not yet implemented")
    }

}



