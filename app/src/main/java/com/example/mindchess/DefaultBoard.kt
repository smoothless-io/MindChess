package com.example.mindchess

import com.example.mindchess.chess_mechanics.Piece

class DefaultBoard(
    val piece_setup: MutableMap<Coordinate, Piece>
) : Board {

    override var turn = 1

    init {
        for (piece in piece_setup.values) {
            piece.legal_moves.clear()
            piece.findPossibleMoves(piece_setup)
        }
    }

    override fun playMove(piece_name: String, origin_coordinate: Coordinate?, destination_coordinate: Coordinate) {


        if (origin_coordinate != null) {

            piece_setup[origin_coordinate]?.move(piece_setup, destination_coordinate)

        } else {
            for (piece in piece_setup.values) {
                if (piece_name == piece.name && turn == piece.team && piece.legal_moves.contains(destination_coordinate)) {
                    piece.move(piece_setup, destination_coordinate)
                    break
                }
            }
        }

        for (piece in piece_setup.values) {
            piece.findPossibleMoves(piece_setup)
        }

        turn *= -1


    }

    override fun getPieces(): Collection<Piece>{
        return piece_setup.values
    }

}