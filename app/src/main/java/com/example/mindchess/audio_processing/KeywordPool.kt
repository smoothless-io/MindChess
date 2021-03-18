package com.example.mindchess.audio_processing

import com.example.mindchess.Coordinate

data class KeywordPool(
    var keywords: List<String>,
    var active: Boolean,
    var model_id: String
)
