package com.example.mindchess.audio_processing

import com.example.mindchess.Coordinate
import com.example.mindchess.chess_mechanics.Piece

data class MoveCommand(
    var piece: Piece? = null,
    var piece_name: String? = null,
    var origin_coordinate: Coordinate? = null,
    var destination_coordinate: Coordinate
) : Command
