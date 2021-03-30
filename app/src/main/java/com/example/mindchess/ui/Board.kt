package com.example.mindchess.ui

import com.example.mindchess.audio_processing.MoveCommand
import com.example.mindchess.chess_mechanics.Piece

interface Board {


    fun findLegalMoves(team: Int) : Boolean // returns whether or not the team has any legal moves
    fun playMove(team: Int, command: MoveCommand) : Boolean // returns whether or not the move was succesfully played
    fun isInCheck(team: Int) : Boolean

    fun getPieces() : Collection<Piece>
    fun getPieceSetup(team: Int) : MutableMap<Coordinate, Piece>
    fun getPieceSetupCopy() : Array<MutableMap<Coordinate, Piece>>
    fun getLastMovedPieceCopy() : Piece?
}