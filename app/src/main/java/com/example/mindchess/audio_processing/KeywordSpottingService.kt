package com.example.mindchess.audio_processing

import android.content.Context
import android.util.Log
import com.example.mindchess.ml.Model
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer

private var LOG_TAG = "KeywordSpottingService"

class KeywordSpottingService(private val context: Context) {

    // private var moves = arrayOf("PAWN", "D", "4", "PAWN", "D", "5", "BISHOP", "F", "4", "KNIGHT", "F", "6", "PAWN", "E", "3", "PAWN", "E", "6", "KNIGHT", "D", "2", "PAWN", "C", "5", "PAWN", "C", "3")
    private var moves = arrayOf("")
    private var count = -1
    private val model = Model.newInstance(context)


    fun predict(buffer: ByteBuffer, keyword_pools: Collection<KeywordPool>) : String {


//        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 44, 13, 1), DataType.FLOAT32)
//        inputFeature0.loadBuffer(buffer)
//
//        val outputs = model.process(inputFeature0)
//        val outputFeature0 = outputs.outputFeature0AsTensorBuffer
//
//        model.close()

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