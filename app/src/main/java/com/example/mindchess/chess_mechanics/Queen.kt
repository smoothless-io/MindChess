package com.example.mindchess.chess_mechanics

import com.example.mindchess.ui.Coordinate
import com.example.mindchess.ui.isOnBoard
import com.example.mindchess.ui.move

class Queen(
    override val team: Int,
    override var coordinate: Coordinate,

    override var move_count: Int = 0,
    override val legal_moves: ArrayList<Coordinate> = arrayListOf()
) : Piece() {

    override val name = "QUEEN"
    override val value = 9

    override fun findPossibleMoves(
        piece_setup: Array<MutableMap<Coordinate, Piece>>,
        last_moved_piece: Piece?
    ): Boolean {

        var yields_check = super.findPossibleMoves(piece_setup, last_moved_piece)

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
                        yields_check = true
                    }
                    break
                }

                temp_coordinate.move(step)
            }

        }

        return yields_check
    }

    override fun copy(): Piece {
        return Queen(this.team, this.coordinate, this.move_count, this.legal_moves)
    }

}



