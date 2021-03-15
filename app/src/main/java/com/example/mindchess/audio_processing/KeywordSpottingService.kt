package com.example.mindchess.audio_processing

import android.content.Context
import android.graphics.ColorSpace
import android.util.Log
import com.example.mindchess.ml.DigitClassifier
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer

private var LOG_TAG = "KeywordSpottingService"

class KeywordSpottingService(private val digitClassifier: DigitClassifier) {

    // private var moves = arrayOf("PAWN", "D", "4", "PAWN", "D", "5", "BISHOP", "F", "4", "KNIGHT", "F", "6", "PAWN", "E", "3", "PAWN", "E", "6", "KNIGHT", "D", "2", "PAWN", "C", "5", "PAWN", "C", "3")
    private var moves = arrayOf("")
    private var count = -1


    fun predict(buffer: ByteBuffer, keyword_pools: Collection<KeywordPool>) : String {

        // Creates inputs for reference.
        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 44, 13, 1), DataType.FLOAT32)
        // inputFeature0.loadBuffer(buffer)

        // Runs model inference and gets result.
        val outputs = digitClassifier.process(inputFeature0)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer
        Log.v(LOG_TAG, "Stuff.")

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