package com.example.mindchess

import com.example.mindchess.chess_mechanics.*
import com.example.mindchess.chess_mechanics.Piece

class DefaultPieceFactory(
    private val team: Int
) : PieceFactory {

    override fun createPawn(coordinate: Coordinate) : Pawn {
        return Pawn(team = team, coordinate = coordinate)
    }

    override fun createKnight(coordinate: Coordinate) : Knight {
        return Knight(team = team, coordinate = coordinate)
    }

    override fun createBishop(coordinate: Coordinate) : Bishop {
        return Bishop(team = team, coordinate = coordinate)
    }

    override fun createRook(coordinate: Coordinate) : Rook {
        return Rook(team = team, coordinate = coordinate)
    }

    override fun createQueen(coordinate: Coordinate) : Queen {
        return Queen(team = team, coordinate = coordinate)
    }

    override fun createKing(coordinate: Coordinate) : King {
        return King(team = team, coordinate = coordinate)
    }

}