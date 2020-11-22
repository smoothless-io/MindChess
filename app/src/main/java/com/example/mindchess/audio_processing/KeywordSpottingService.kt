package com.example.mindchess.audio_processing

import android.util.Log

private var LOG_TAG = "KeywordSpottingService"

class KeywordSpottingService {

    // private var moves = arrayOf("PAWN", "D", "4", "PAWN", "D", "5", "BISHOP", "F", "4", "KNIGHT", "F", "6", "PAWN", "E", "3", "PAWN", "E", "6", "KNIGHT", "D", "2", "PAWN", "C", "5", "PAWN", "C", "3")
    private var moves = arrayOf("")
    private var count = -1


    fun predict(buffer: FloatArray, keyword_pools: Collection<KeywordPool>) : String {

        count += 1
        return if (count < moves.size) moves[count] else ""

//        for (pool in keyword_pools) {
//            if (pool.active) {
//                //model[pool.model_id].predict()
//                return "KNIGHT"
//            }
//        }


    }

}