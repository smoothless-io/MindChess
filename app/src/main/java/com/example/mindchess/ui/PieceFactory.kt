package com.example.mindchess.ui

import com.example.mindchess.chess_mechanics.*

interface PieceFactory {

    fun createPawn(coordinate: Coordinate) : Pawn
    fun createKnight(coordinate: Coordinate) : Knight
    fun createBishop(coordinate: Coordinate) : Bishop
    fun createRook(coordinate: Coordinate) : Rook
    fun createQueen(coordinate: Coordinate) : Queen
    fun createKing(coordinate: Coordinate) : King

}
