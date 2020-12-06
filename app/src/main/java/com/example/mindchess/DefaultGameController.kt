package com.example.mindchess

import android.util.Log
import com.example.mindchess.audio_processing.Command
import com.example.mindchess.audio_processing.MoveCommand
import com.example.mindchess.audio_processing.SpecialCommand
import com.example.mindchess.chess_mechanics.Piece
import com.example.mindchess.common.CHECKMATE_CODE
import com.example.mindchess.common.STALAMATE_CODE

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
        val game_result = game.processTouch(coordinate)

        updateViewModel(game.getCurrentBoard().getPieces(), game.getSelectedCoordinate())
        if (game_result == CHECKMATE_CODE) {
            listeners.forEach { it.onCheckmate() }
        } else if (game_result == STALAMATE_CODE) {
            listeners.forEach { it.onStalemate() }
        }

    }

    override fun processVoiceCommand(command: Command) {
        val game_result = game.processVoiceCommand(command)

        updateViewModel(game.getCurrentBoard().getPieces(), game.getSelectedCoordinate())
        if (game_result == CHECKMATE_CODE) {
            listeners.forEach { it.onCheckmate() }
        } else if (game_result == STALAMATE_CODE) {
            listeners.forEach { it.onStalemate() }
        }
    }

}