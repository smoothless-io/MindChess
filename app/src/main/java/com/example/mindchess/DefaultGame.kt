package com.example.mindchess

class DefaultGame(private var board: Board) : Game {

    override fun playMove(piece_id: String, origin_coordinate: Coordinate?, destination_coordinate: Coordinate) {

        board.playMove(piece_id, origin_coordinate, destination_coordinate)

    }

    override fun getCurrentBoard() : Board {
        return board
    }


}