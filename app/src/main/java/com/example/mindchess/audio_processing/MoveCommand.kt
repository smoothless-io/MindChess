package com.example.mindchess

import androidx.navigation.ActivityNavigator
import com.example.mindchess.common.BOARD_TILES

data class MoveCommand(
    var piece_id: Char,
    var origin_coordinate: Coordinate,
    var destination_coordinate: Coordinate
) {

}
