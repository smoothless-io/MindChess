package com.example.mindchess

import android.util.Log
import com.example.mindchess.audio_processing.MoveCommand
import com.example.mindchess.audio_processing.SpecialCommand
import com.example.mindchess.chess_mechanics.Piece

private const val LOG_TAG = "DefaultGameController"

class DefaultGameController(
    private val game: Game
) : GameController {


    private var viewModel: GameViewModel = GameViewModel(
        game.getCurrentBoard().getPieces()
    )

    private var listeners = mutableListOf<GameEventListener>()

    override fun addViewModelListener(listener: GameEventListener) {
        listeners.add(listener)
        listener.onViewModelChange(viewModel)
    }

    //Updating and playing with viewModel state in this class

    override fun updateViewModel(pieces: Collection<Piece>) {
        viewModel.pieces = pieces
        listeners.forEach {it.onViewModelChange(viewModel)}
    }

    override fun processSpecialCommand(command: SpecialCommand) {

        for (word in command.command) {
            Log.v(LOG_TAG, word)
        }

    }

    override fun processMoveCommand(command: MoveCommand) {

        game.playMove(command.piece_name, command.origin_coordinate, command.destination_coordinate)
        updateViewModel(game.getCurrentBoard().getPieces())

    }


}