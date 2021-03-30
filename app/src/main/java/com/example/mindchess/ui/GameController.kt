package com.example.mindchess.ui

import com.example.mindchess.audio_processing.Command
import com.example.mindchess.chess_mechanics.Piece

interface GameController {

    fun addViewModelListener(listener: GameEventListener)
    fun updateViewModel(pieces: Collection<Piece>, selected_coordinate: Coordinate?)

    fun processTouch(coordinate: Coordinate)
    fun processVoiceCommand(command: Command)
}
