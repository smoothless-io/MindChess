package com.example.mindchess.ui

import com.example.mindchess.audio_processing.Command
import com.example.mindchess.audio_processing.MoveCommand
import com.example.mindchess.audio_processing.SpecialCommand
import com.example.mindchess.chess_mechanics.Piece
import com.example.mindchess.common.CHECKMATE_CODE
import com.example.mindchess.common.STALAMATE_CODE
import com.example.mindchess.extensions.toInt

class DefaultGame(private var board: Board) : Game {

    override var turn = 0
    override var selected_piece: Piece? = null

    init {
        board.findLegalMoves(turn)
    }



    override fun processTouch(coordinate: Coordinate) : Int {

        var game_result = 0

        if (selected_piece == null) {
            selected_piece = board.getPieceSetup(turn)[coordinate]
        } else {
            val command = MoveCommand(
                piece = selected_piece,
                destination_coordinate = coordinate
            )


            if (board.playMove(turn, command)) {
                turn = (turn == 0).toInt()

                val any_legal_moves = board.findLegalMoves(turn)

                if (!any_legal_moves) {
                    game_result = if (board.isInCheck(turn)) CHECKMATE_CODE else STALAMATE_CODE
                }

                selected_piece = null
            } else {
                selected_piece = board.getPieceSetup(turn)[coordinate]
            }


        }

        return game_result

    }

    override fun processVoiceCommand(command: Command) : Int {

        var game_result = 0

        if (command is MoveCommand) {
            if (board.playMove(turn, command)) {
                turn = (turn == 0).toInt()
                val any_legal_moves = board.findLegalMoves(turn)

                if (!any_legal_moves) {
                    game_result = if (board.isInCheck(turn)) CHECKMATE_CODE else STALAMATE_CODE
                }
            }
        } else if (command is SpecialCommand) {
            // Do something else
            return game_result
        }

        return game_result
    }

    override fun getCurrentBoard() : Board {
        return board
    }

    override fun getSelectedCoordinate(): Coordinate? {
        return selected_piece?.coordinate
    }


}