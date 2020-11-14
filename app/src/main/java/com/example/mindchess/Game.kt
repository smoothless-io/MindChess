package com.example.mindchess

import com.example.mindchess.audio_processing.Command
import com.example.mindchess.chess_mechanics.Piece

interface Game {

    var turn: Int
    var selected_piece: Piece?

    fun processTouch(coordinate: Coordinate)
    fun processVoiceCommand(command: Command)

    fun getCurrentBoard() : Board
    fun getSelectedCoordinate() : Coordinate?
}
