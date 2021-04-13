package com.example.mindchess.audio_processing

import be.tarsos.dsp.AudioEvent

interface OnCommandFormed {
    fun handleCommand(command: Command)
    fun saveSIF(audioEvent: AudioEvent)
}