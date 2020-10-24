package com.example.mindchess

interface Game {

    fun playMove(piece_id: String, origin_coordinate: Coordinate?, destination_coordinate: Coordinate)

    fun getCurrentBoard() : Board
}
