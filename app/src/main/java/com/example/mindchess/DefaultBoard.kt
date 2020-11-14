package com.example.mindchess

import com.example.mindchess.audio_processing.MoveCommand
import com.example.mindchess.chess_mechanics.Piece
import com.example.mindchess.common.toInt

class DefaultBoard(
    val piece_setup: Array<MutableMap<Coordinate, Piece>>
) : Board {


    override fun playMove(team: Int, command: MoveCommand) : Boolean {

        var move_played_successfully = false
        var piece: Piece? = null



        if (command.piece != null)
            piece = command.piece
        else if (command.origin_coordinate != null)
            piece = piece_setup[team][command.destination_coordinate]
        else if (command.piece_name != null) {
            for (_piece in piece_setup[team].values) {
                if (command.piece_name == _piece.name && _piece.legal_moves.contains(command.destination_coordinate)) {
                    piece = _piece
                    break
                }
            }
        }

        if (piece != null && piece.legal_moves.contains(command.destination_coordinate)) {
            piece.move(piece_setup, command.destination_coordinate)
            move_played_successfully = true
        }


        return move_played_successfully

    }


    override fun findLegalMoves(team: Int) {

        for (piece in piece_setup[team].values) {
            piece.legal_moves.clear()
            piece.findPossibleMoves(piece_setup)
        }
    }


    override fun getPieceSetup(team: Int) : MutableMap<Coordinate, Piece> {
        return piece_setup[team]
    }

    override fun getPieces(): Collection<Piece> {
        val pieces = arrayListOf<Piece>()
        for (team_pieces in piece_setup) {
            pieces.addAll(team_pieces.values)
        }
        return pieces
    }

}
