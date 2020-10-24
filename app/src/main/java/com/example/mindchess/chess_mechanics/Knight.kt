package com.example.mindchess.chess_mechanics

import android.graphics.Bitmap
import com.example.mindchess.Coordinate
import com.example.mindchess.common.toInt
import com.example.mindchess.isOnBoard

class Knight(

    override val team: Int,
    override var coordinate: Coordinate,
    override val image: Bitmap?
) : Piece() {

    override val name = "KNIGHT"
    override val value = 3


    override fun findPossibleMoves(piece_setup: MutableMap<Coordinate, Piece>) {

        for (i in 0..3) {

            for (j in 0..1) {
                val step = Coordinate(
                    Math.round(Math.cos(Math.PI / 4 + i * Math.PI / 2)).toInt() * ((j == 0).toInt() + 1),
                    Math.round(Math.sin(Math.PI / 4 + i * Math.PI / 2)).toInt() * ((j == 1).toInt() + 1)
                )

                val temp_coordinate = Coordinate(coordinate.x + step.x, coordinate.y + step.y)


                if (temp_coordinate.isOnBoard() && (piece_setup[temp_coordinate] == null || piece_setup[temp_coordinate]!!.team * team == -1)) {

                    legal_moves.add(temp_coordinate)

                    if (piece_setup[temp_coordinate] != null && piece_setup[temp_coordinate]!!.name == "KING") {
                        //capture.. and check
                        break
                    }
                }
            }
        }



    }

    override fun removeIllegalMoves(possible_moves: ArrayList<Coordinate>) {
        TODO("Not yet implemented")
    }
}



