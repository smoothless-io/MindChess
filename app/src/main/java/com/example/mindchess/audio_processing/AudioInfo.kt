package com.example.mindchess.audio_processing

data class AudioInfo(
    var sampleRate: Int,
    var bufferSize: Int,
    var bufferOverlap: Int

)