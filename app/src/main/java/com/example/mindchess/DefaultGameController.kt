package com.example.mindchess

import android.util.Log
import com.example.mindchess.audio_processing.Command
import com.example.mindchess.audio_processing.MoveCommand
import com.example.mindchess.audio_processing.SpecialCommand
import com.example.mindchess.chess_mechanics.Piece

private const val LOG_TAG = "DefaultGameController"

class DefaultGameController(
    private val game: Game
) : GameController {



    private var viewModel: GameViewModel = GameViewModel(
        game.getCurrentBoard().getPieces(), game.getSelectedCoordinate()
    )

    private var listeners = mutableListOf<GameEventListener>()

    override fun addViewModelListener(listener: GameEventListener) {
        listeners.add(listener)
        listener.onViewModelChange(viewModel)
    }

    //Updating and playing with viewModel state in this class

    override fun updateViewModel(pieces: Collection<Piece>, selected_coordinate: Coordinate?) {
        viewModel.pieces = pieces
        viewModel.selected_coordinate = selected_coordinate
        listeners.forEach {it.onViewModelChange(viewModel)}
    }


    override fun processTouch(coordinate: Coordinate) {
        game.processTouch(coordinate)
        updateViewModel(game.getCurrentBoard().getPieces(), game.getSelectedCoordinate())
    }

    override fun processVoiceCommand(command: Command) {
        game.processVoiceCommand(command)
        updateViewModel(game.getCurrentBoard().getPieces(), game.getSelectedCoordinate())
    }

}