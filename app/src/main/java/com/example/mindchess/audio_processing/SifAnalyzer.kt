package com.example.mindchess

import android.util.Log
import be.tarsos.dsp.AudioEvent
import be.tarsos.dsp.AudioProcessor
import com.example.mindchess.audio_processing.HotSifSpottingService

private const val LOG_TAG = "SifExtractorTest"

class SifAnalyzer(var hsss: HotSifSpottingService) : AudioProcessor {

    fun extractSifs(buffer: FloatArray) : List<FloatArray> {
        val sifs = mutableListOf<FloatArray>()


        for (i in 1..3) {
            sifs.add(buffer.sliceArray(i*10..i*10 + 5))
        }

        return sifs
    }

    override fun process(audioEvent: AudioEvent?): Boolean {

        val sifs = extractSifs(audioEvent!!.floatBuffer)
        // An array of 1 second (22050 samples) long FloatArrays, which correspond with a sif, that is at the beginning of the array and padded with silence if needed

        for (sif in sifs) {
            hsss.predict(sif)
        }

        return true
    }


    override fun processingFinished() {
        TODO("Not yet implemented")
    }


}