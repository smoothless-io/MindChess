package com.example.mindchess

import com.example.mindchess.audio_processing.Command
import com.example.mindchess.audio_processing.MoveCommand
import com.example.mindchess.audio_processing.SpecialCommand
import com.example.mindchess.chess_mechanics.Piece
import com.example.mindchess.common.toInt

class DefaultGame(private var board: Board) : Game {

    override var turn = 0
    override var selected_piece: Piece? = null

    init {
        board.findLegalMoves(turn)
    }



    override fun processTouch(coordinate: Coordinate) {

        if (selected_piece == null) {
            selected_piece = board.getPieceSetup(turn)[coordinate]
        } else {
            val command = MoveCommand(
                piece = selected_piece,
                destination_coordinate = coordinate
            )


            if (board.playMove(turn, command)) {
                turn = (turn == 0).toInt()
                board.findLegalMoves(turn)
                selected_piece = null
            } else {
                selected_piece = board.getPieceSetup(turn)[coordinate]
            }


        }

    }

    override fun processVoiceCommand(command: Command) {

        if (command is MoveCommand) {
            if (board.playMove(turn, command)) {
                turn = (turn == 0).toInt()
                board.findLegalMoves(turn)
            }
        } else if (command is SpecialCommand) {
            // Do something else
            return
        }
    }


    override fun getCurrentBoard() : Board {
        return board
    }

    override fun getSelectedCoordinate(): Coordinate? {
        return selected_piece?.coordinate
    }


}