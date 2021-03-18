package com.example.mindchess.audio_processing

import com.example.mindchess.ml.FileClassifier
import com.example.mindchess.ml.RankClassifier
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer

private var LOG_TAG = "KeywordSpottingService"

class KeywordSpottingService(
    private val rankClassifier: RankClassifier,
    private val fileClassifier: FileClassifier,
    private val pieceNameClassifier: RankClassifier,
    private val specialWordClassifier: FileClassifier
) {


    fun predict(buffer: ByteBuffer, keyword_pools: Collection<KeywordPool>) : ArrayList<Pair<String, Float>> {

        val predictions = arrayListOf<Pair<String, Float>>()

        for (pool in keyword_pools) {
            if (pool.active) {

                val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 44, 13, 1), DataType.FLOAT32)
                inputFeature0.loadBuffer(buffer)

                var outputs = TensorBuffer.createDynamic(DataType.FLOAT32)
                when (pool.model_id) {
                    "RANK_CLASSIFIER" -> outputs = rankClassifier.process(inputFeature0).outputFeature0AsTensorBuffer
                    "FILE_CLASSIFIER" -> outputs = fileClassifier.process(inputFeature0).outputFeature0AsTensorBuffer
                    "PIECE_NAME_CLASSIFIER" -> outputs = pieceNameClassifier.process(inputFeature0).outputFeature0AsTensorBuffer
                    "SPECIAL_WORD_CLASSIFIER" -> outputs = specialWordClassifier.process(inputFeature0).outputFeature0AsTensorBuffer
                }

                val index = outputs.floatArray.indices.maxBy { outputs.floatArray[it] } ?: -1
                predictions.add(Pair(pool.keywords[index], outputs.floatArray[index]))

            }
        }

        return predictions

    }



}