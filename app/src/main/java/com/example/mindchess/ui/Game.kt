package com.example.mindchess.ui

import com.example.mindchess.audio_processing.Command
import com.example.mindchess.chess_mechanics.Piece

interface Game {

    var turn: Int
    var selected_piece: Piece?

    fun processTouch(coordinate: Coordinate) : Int
    fun processVoiceCommand(command: Command) : Int

    fun getCurrentBoard() : Board
    fun getSelectedCoordinate() : Coordinate?
}
