package com.example.mindchess.audio_processing

import androidx.navigation.ActivityNavigator
import com.example.mindchess.common.BOARD_TILES

data class SpecialCommand(
    var command: ArrayList<String>
) : Command
