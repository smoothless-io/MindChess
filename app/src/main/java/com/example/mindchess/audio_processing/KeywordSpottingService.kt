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


        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 44, 13, 1), DataType.FLOAT32)
        // inputFeature0.loadBuffer(buffer)

        val outputs = digitClassifier.process(inputFeature0).outputFeature0AsTensorBuffer
        val digit = outputs.floatArray.indices.maxBy { outputs.floatArray[it] } ?: -1

        Log.v(LOG_TAG, "New Sif:")
        Log.v(LOG_TAG, digit.toString())
        for (i in outputs.floatArray) {
            Log.v(LOG_TAG, i.toString())
        }


//        for (pool in keyword_pools) {
//            if (pool.active) {
//                //model[pool.model_id].predict()
//                return "KNIGHT"
//            }
//        }

        return ""


    }



}