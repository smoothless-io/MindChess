package com.example.mindchess

interface GameEventListener {

    fun onViewModelChange(viewModel: GameViewModel)
    fun onStalemate()
    fun onCheckmate()
}
