package com.example.mindchess.ui

import com.example.mindchess.chess_mechanics.Piece

data class GameViewModel(

    var pieces: Collection<Piece> = arrayListOf(),
    var selected_coordinate: Coordinate? = null


)