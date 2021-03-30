package com.example.mindchess.ui

import com.example.mindchess.common.BOARD_TILES

data class Coordinate(
    var x: Int,
    var y: Int
) {

    constructor(file: String, rank: String) : this(file[0].toInt() - 'A'.toInt(), rank[0].toInt() - '1'.toInt())
    constructor(file: Char, rank: Char) : this(file.toInt() - 'A'.toInt(), rank.toInt() - '1'.toInt())
}

internal fun Coordinate.isOnBoard() : Boolean {
    return (x in 0..BOARD_TILES - 1 && y in 0..BOARD_TILES - 1)
}

internal fun Coordinate.move(step_size: Coordinate) {
    this.x += step_size.x
    this.y += step_size.y
}
