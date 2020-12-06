package com.example.mindchess.chess_mechanics

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.mindchess.Coordinate
import com.example.mindchess.R
import com.example.mindchess.common.toInt
import com.example.mindchess.isOnBoard

class Knight(

    override val team: Int,
    override var coordinate: Coordinate,

    override var move_count: Int = 0,
    override val legal_moves: ArrayList<Coordinate> = arrayListOf()
) : Piece() {

    override val name = "KNIGHT"
    override val value = 3


    override fun findPossibleMoves(piece_setup: Array<MutableMap<Coordinate, Piece>>) : Boolean {
        var yields_check = super.findPossibleMoves(piece_setup)

        val piece_setup_mixed = getPieceSetupMixed(piece_setup)

        for (i in 0..3) {

            for (j in 0..1) {
                val step = Coordinate(
                    Math.round(Math.cos(Math.PI / 4 + i * Math.PI / 2)).toInt() * ((j == 0).toInt() + 1),
                    Math.round(Math.sin(Math.PI / 4 + i * Math.PI / 2)).toInt() * ((j == 1).toInt() + 1)
                )

                val temp_coordinate = Coordinate(coordinate.x + step.x, coordinate.y + step.y)


                if (temp_coordinate.isOnBoard() && (piece_setup_mixed[temp_coordinate] == null || piece_setup_mixed[temp_coordinate]!!.team * team == -1)) {

                    legal_moves.add(temp_coordinate)

                    if (piece_setup_mixed[temp_coordinate] != null && piece_setup_mixed[temp_coordinate]!!.name == "KING") {
                        yields_check = true
                    }
                }
            }
        }

        return yields_check

    }

    override fun copy(): Piece {
        return Knight(this.team, this.coordinate, this.move_count, this.legal_moves)
    }

}



