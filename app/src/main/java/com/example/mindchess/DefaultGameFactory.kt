package com.example.mindchess

import com.example.mindchess.chess_mechanics.Piece

class DefaultGameFactory(
    white_piece_image_provider: PieceImageProvider,
    black_piece_image_provider: PieceImageProvider
) : GameFactory {

    override fun createNewGame(): Game {
        val piece_setup: Array<MutableMap<Coordinate, Piece>> = arrayOf(mutableMapOf(), mutableMapOf())

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

    private fun populatePieces(piece_setup: Array<MutableMap<Coordinate, Piece>>) {
        var coordinate: Coordinate?

        // Pawns
        for (i in 0..7) {
            coordinate = Coordinate(i, 1)
            piece_setup[0][coordinate] = white_piece_factory.createPawn(coordinate)

            coordinate = Coordinate(i, 6)
            piece_setup[1][coordinate] = black_piece_factory.createPawn(coordinate)
        }

        // Knights
        coordinate = Coordinate(1, 0)
        piece_setup[0][coordinate] = white_piece_factory.createKnight(coordinate)

        coordinate = Coordinate(6, 0)
        piece_setup[0][coordinate] = white_piece_factory.createKnight(coordinate)

        coordinate = Coordinate(1, 7)
        piece_setup[1][coordinate] = black_piece_factory.createKnight(coordinate)

        coordinate = Coordinate(6, 7)
        piece_setup[1][coordinate] = black_piece_factory.createKnight(coordinate)

        // Bishops
        coordinate = Coordinate(2, 0)
        piece_setup[0][coordinate] = white_piece_factory.createBishop(coordinate)

        coordinate = Coordinate(5, 0)
        piece_setup[0][coordinate] = white_piece_factory.createBishop(coordinate)

        coordinate = Coordinate(2, 7)
        piece_setup[1][coordinate] = black_piece_factory.createBishop(coordinate)

        coordinate = Coordinate(5, 7)
        piece_setup[1][coordinate] = black_piece_factory.createBishop(coordinate)

        // Rooks
        coordinate = Coordinate(0, 0)
        piece_setup[0][coordinate] = white_piece_factory.createRook(coordinate)

        coordinate = Coordinate(7, 0)
        piece_setup[0][coordinate] = white_piece_factory.createRook(coordinate)

        coordinate = Coordinate(0, 7)
        piece_setup[1][coordinate] = black_piece_factory.createRook(coordinate)

        coordinate = Coordinate(7, 7)
        piece_setup[1][coordinate] = black_piece_factory.createRook(coordinate)

        // Queens
        coordinate = Coordinate(3, 0)
        piece_setup[0][coordinate] = white_piece_factory.createQueen(coordinate)

        coordinate = Coordinate(3, 7)
        piece_setup[1][coordinate] = black_piece_factory.createQueen(coordinate)

        // Kings
        coordinate = Coordinate(4, 0)
        piece_setup[0][coordinate] = white_piece_factory.createKing(coordinate)

        coordinate = Coordinate(4, 7)
        piece_setup[1][coordinate] = black_piece_factory.createKing(coordinate)
    }

}