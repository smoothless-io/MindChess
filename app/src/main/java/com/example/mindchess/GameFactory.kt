package com.example.mindchess

interface GameFactory {

    fun createNewGame(): Game

    val white_piece_factory: PieceFactory
    val black_piece_factory: PieceFactory


}
