package com.example.mindchess.ui

interface GameEventListener {

    fun onViewModelChange(viewModel: GameViewModel)
    fun onStalemate()
    fun onCheckmate()
}
