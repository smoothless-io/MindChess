package com.example.mindchess.audio_processing

import com.example.mindchess.Coordinate

data class MoveCommand(
    var piece_id: Char,
    var origin_coordinate: Coordinate,
    var destination_coordinate: Coordinate
)
