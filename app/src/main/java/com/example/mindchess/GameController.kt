package com.example.mindchess

import com.example.mindchess.audio_processing.Command
import com.example.mindchess.audio_processing.MoveCommand
import com.example.mindchess.audio_processing.SpecialCommand
import com.example.mindchess.chess_mechanics.Piece

interface GameController {

    fun addViewModelListener(listener: GameEventListener)
    fun updateViewModel(pieces: Collection<Piece>, selected_coordinate: Coordinate?)

    fun processTouch(coordinate: Coordinate)
    fun processVoiceCommand(command: Command)
}
