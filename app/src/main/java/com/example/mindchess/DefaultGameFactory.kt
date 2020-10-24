package com.example.mindchess

import com.example.mindchess.chess_mechanics.Piece

class DefaultGameFactory(
    white_piece_image_provider: PieceImageProvider,
    black_piece_image_provider: PieceImageProvider
) : GameFactory {

    override fun createNewGame(): Game {
        val piece_setup: MutableMap<Coordinate, Piece> = mutableMapOf()

        populatePieces(piece_setup)

        return DefaultGame(
            DefaultBoard(piece_setup)
        )


    }

    override val white_piece_factory: PieceFactory = DefaultPieceFactory(
        1,
        white_piece_image_provider
    )

    override val black_piece_factory: PieceFactory = DefaultPieceFactory(
        -1,
        black_piece_image_provider
    )

    private fun populatePieces(piece_setup: MutableMap<Coordinate, Piece>) {
        var location: Coordinate?

        // Pawns
        for (i in 0..7) {
            location = Coordinate(i, 1)
            piece_setup[location] = white_piece_factory.createPawn(location)

            location = Coordinate(i, 6)
            piece_setup[location] = black_piece_factory.createPawn(location)
        }

        // Knights
        location = Coordinate(1, 0)
        piece_setup[location] = white_piece_factory.createKnight(location)

        location = Coordinate(6, 0)
        piece_setup[location] = white_piece_factory.createKnight(location)

        location = Coordinate(1, 7)
        piece_setup[location] = black_piece_factory.createKnight(location)

        location = Coordinate(6, 7)
        piece_setup[location] = black_piece_factory.createKnight(location)

        // Bishops
        location = Coordinate(2, 0)
        piece_setup[location] = white_piece_factory.createBishop(location)

        location = Coordinate(5, 0)
        piece_setup[location] = white_piece_factory.createBishop(location)

        location = Coordinate(2, 7)
        piece_setup[location] = black_piece_factory.createBishop(location)

        location = Coordinate(5, 7)
        piece_setup[location] = black_piece_factory.createBishop(location)

        // Rooks
        location = Coordinate(0, 0)
        piece_setup[location] = white_piece_factory.createRook(location)

        location = Coordinate(7, 0)
        piece_setup[location] = white_piece_factory.createRook(location)

        location = Coordinate(0, 7)
        piece_setup[location] = black_piece_factory.createRook(location)

        location = Coordinate(7, 7)
        piece_setup[location] = black_piece_factory.createRook(location)

        // Queens
        location = Coordinate(3, 0)
        piece_setup[location] = white_piece_factory.createQueen(location)

        location = Coordinate(3, 7)
        piece_setup[location] = black_piece_factory.createQueen(location)

        // Kings
        location = Coordinate(4, 0)
        piece_setup[location] = white_piece_factory.createKing(location)

        location = Coordinate(4, 7)
        piece_setup[location] = black_piece_factory.createKing(location)
    }

}