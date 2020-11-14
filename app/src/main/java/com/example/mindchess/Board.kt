package com.example.mindchess

import com.example.mindchess.audio_processing.MoveCommand
import com.example.mindchess.chess_mechanics.Piece

interface Board {


    fun findLegalMoves(team: Int)
    fun playMove(team: Int, command: MoveCommand) : Boolean



    fun getPieces() : Collection<Piece>
    fun getPieceSetup(team: Int) : MutableMap<Coordinate, Piece>
}