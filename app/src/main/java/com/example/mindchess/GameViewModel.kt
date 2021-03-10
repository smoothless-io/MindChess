package com.example.mindchess

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mindchess.chess_mechanics.Piece

data class GameViewModel(

    var pieces: Collection<Piece> = arrayListOf(),
    var selected_coordinate: Coordinate? = null


)