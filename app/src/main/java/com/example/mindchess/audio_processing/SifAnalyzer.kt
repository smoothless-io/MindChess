package com.example.mindchess.audio_processing

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import be.tarsos.dsp.AudioEvent
import be.tarsos.dsp.AudioProcessor
import be.tarsos.dsp.io.TarsosDSPAudioFormat
import be.tarsos.dsp.mfcc.MFCC
import com.example.mindchess.extensions.toInt
import com.example.mindchess.ui.Coordinate
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.*
import org.jetbrains.kotlinx.multik.ndarray.data.*
import org.jetbrains.kotlinx.multik.ndarray.operations.*
import java.lang.Integer.max
import java.lang.Integer.min
import kotlin.math.sign


private const val LOG_TAG = "SifExtractorTest"

class SifAnalyzer(var audioInfo: AudioInfo, var kss: KeywordSpottingService, var handler: OnCommandFormed) : AudioProcessor {

    // SIF stands for silence-isolated frame.

    private var lastSifBuffer = FloatArray(audioInfo.sampleRate)
    private var lastSifLeakingOffset = audioInfo.bufferSize

    // Positive offset, if no new SIF started (offset is length of appendable silence).
    // Negative offset, if a SIF started (offset is length of that SIF).

    private var specialCommands: Array<SpecialCommand> = arrayOf(
        SpecialCommand(arrayListOf("RESIGN")),
        SpecialCommand(arrayListOf("CLOCK")),
        SpecialCommand(arrayListOf("UNDO")),
        SpecialCommand(arrayListOf("WHAT", "CAN", "JAKUB", "EAT"))
    )

    private data class CommandFormingInfo(
        var moveCommandRaw: ArrayList<String>,
        var moveCommandFormLong: Boolean,
        var specialCommandRaw: ArrayList<String>,
        var specialCommandPossible: Boolean

    )

    private var cfi = CommandFormingInfo(arrayListOf(), false, arrayListOf(), false)


    private var keywordPools: MutableMap<String, KeywordPool> = mutableMapOf()
    init {
        keywordPools["PIECE_NAMES"] = KeywordPool(listOf("PAWN", "KNIGHT", "BISHOP", "ROOK", "QUEEN", "KING"), true, "PIECE_NAME_CLASSIFIER")
        keywordPools["FILES"] = KeywordPool(listOf("A", "B", "C", "D", "E", "F", "G", "H"), false, "FILE_CLASSIFIER")
        keywordPools["RANKS"] = KeywordPool(listOf("1", "2", "3", "4", "5", "6", "7", "8"), false, "RANK_CLASSIFIER")
        keywordPools["SPECIAL_WORDS"] = KeywordPool(listOf("RESIGN", "CLOCK", "FROM", "UNDO"), true, "SPECIAL_WORD_CLASSIFIER")
    }


    private fun extractSifBorders(signalArray: FloatArray) : ArrayList<Pair<Int, Int>> {
        var sifBorders = ArrayList<Pair<Int, Int>>()
        var correctedSifBorders = ArrayList<Pair<Int, Int>>()


        val signal = mk.ndarray(signalArray)
        val signalPower = signal.map {it * it}
        val dynamicRange = signalPower.max()!!.minus(signalPower.min()!!)

        if (dynamicRange > 0.01) {

            val stepSize = audioInfo.sampleRate / 25
            val minSilenceDuration = stepSize * 4
            val silenceThreshold = signalPower.average()

            var currentStart = if (lastSifLeakingOffset > 0) 0 else lastSifLeakingOffset
            var listeningForNewSif = lastSifLeakingOffset > 0

            for (i in 0..signalArray.size - 2 step stepSize) {

                val avgSignalAmplitude = signalPower[Slice(i, i + stepSize, 1)].average()

                if (listeningForNewSif && avgSignalAmplitude > silenceThreshold) {
                    currentStart = i
                    listeningForNewSif = false

                    if (sifBorders.size > 0 && currentStart - sifBorders[sifBorders.size - 1].second <= minSilenceDuration) {
                        currentStart = sifBorders[sifBorders.size - 1].first
                        sifBorders.removeAt(sifBorders.size - 1)
                    }
                }

                if (!listeningForNewSif && (avgSignalAmplitude < silenceThreshold || i == signalArray.size - stepSize)) {
                    sifBorders.add(Pair(currentStart, i))
                    listeningForNewSif = true
                }
            }

            Log.i("extractSifBorders Test", "Num of SIFs: %s".format(sifBorders.size))
            for (sifBorder in sifBorders) {
                Log.i("sifBorders", "%s - %s".format(sifBorder.first, sifBorder.second))
            }


            val currentSifLeakingOffset = if (sifBorders.size > 0) {
                if (currentStart != sifBorders[sifBorders.size - 1].first) -(signalArray.size - currentStart) else
                    signalArray.size - sifBorders[sifBorders.size - 1].second
            } else {
                audioInfo.sampleRate
            }


            correctedSifBorders = ArrayList()

            for (i in sifBorders.indices) {
                val earliestStart = if (i > 0) sifBorders[i - 1].second + audioInfo.sifOffset else -lastSifLeakingOffset
                val latestEnd = if (i < sifBorders.size - 1) sifBorders[i + 1].first - audioInfo.sifOffset else
                    if (currentSifLeakingOffset < 0) audioInfo.sampleRate + currentSifLeakingOffset else audioInfo.sampleRate

                val sifCenter = (sifBorders[i].first + sifBorders[i].second) / 2
                val affordableSifLength = min(min(sifCenter - earliestStart, latestEnd - sifCenter), audioInfo.sampleRate / 2)

                correctedSifBorders.add(Pair(sifCenter - affordableSifLength, sifCenter + affordableSifLength))
            }

            sifBorders = correctedSifBorders
            lastSifLeakingOffset = currentSifLeakingOffset

        } else {
            lastSifLeakingOffset = audioInfo.sampleRate
        }


        return sifBorders
    }



    private fun generateAudioEvent(audioEvent: AudioEvent, startIndex: Int, endIndex: Int) : AudioEvent {

        val correctedAudioEvent = AudioEvent(TarsosDSPAudioFormat(audioInfo.sampleRate.toFloat(), 16, 1, true, false))
        correctedAudioEvent.floatBuffer = FloatArray(audioEvent.floatBuffer.size)

        if (startIndex < 0) {

            lastSifBuffer.copyInto(
                    correctedAudioEvent.floatBuffer,
                    correctedAudioEvent.floatBuffer.size / 2 - (endIndex - startIndex) / 2,
                    lastSifBuffer.size + startIndex,
                    lastSifBuffer.size
            )

            audioEvent.floatBuffer.copyInto(
                    correctedAudioEvent.floatBuffer,
                    correctedAudioEvent.floatBuffer.size / 2 - (endIndex - startIndex) / 2 - startIndex,
                    0,
                    endIndex
            )



        } else {

            audioEvent.floatBuffer.copyInto(
                    correctedAudioEvent.floatBuffer,
                    correctedAudioEvent.floatBuffer.size / 2 - (endIndex - startIndex) / 2,
                    startIndex,
                    endIndex
            )
        }



        return correctedAudioEvent

    }

    override fun process(audioEvent: AudioEvent?): Boolean {


        var sifBorders = extractSifBorders(audioEvent!!.floatBuffer)

        for (sifBorder in sifBorders) {

            val generatedAudioEvent = generateAudioEvent(audioEvent, sifBorder.first, sifBorder.second)

            val mfccExtractor = MFCC(audioEvent.floatBuffer.size, audioInfo.sampleRate.toFloat(), 13, 44, 300f, 3000f)
            mfccExtractor.process(generatedAudioEvent)

            handler.saveSIF(generatedAudioEvent)
//
//
//            val mfcc_buffer = ByteBuffer.allocate(44 * 13)
//            val predictions = kss.predict(mfcc_buffer, keywordPools.values)
//
//            // For now just take the first prediction
//            val keyword = if (predictions.size > 0) predictions[0].first else ""
//
//            if (keyword.length > 0) {
//                val command = formCommand(keyword)
//                if (command != null) {
//                    handler.handleCommand(command)
//                }
//            }
        }

        lastSifBuffer = audioEvent.floatBuffer

        return true
    }



    fun formCommand(keyword: String) : Command? {

        var command: Command? = null


        if (keyword in keywordPools["SPECIAL_WORDS"]!!.keywords) {

            if (keyword == "FROM" && cfi.moveCommandRaw.size == 1) {
                cfi.moveCommandFormLong = true

            } else {
                cfi.specialCommandRaw.add(keyword)

                cfi.specialCommandPossible = false
                for (special_command in specialCommands) {

                    if (cfi.specialCommandRaw == special_command.command) {
                        command = special_command

                        cfi.specialCommandRaw = arrayListOf()

                    } else if (cfi.specialCommandRaw == special_command.command.subList(
                            0,
                            if (special_command.command.size > cfi.specialCommandRaw.size) cfi.specialCommandRaw.size else special_command.command.size
                        )
                    ) {
                        cfi.specialCommandPossible = true
                    }

                }

                if (!cfi.specialCommandPossible) {
                    cfi.specialCommandRaw.clear()
                }
            }

        } else if (keyword in keywordPools["PIECE_NAMES"]!!.keywords) {
            cfi.moveCommandRaw.add(keyword)

            keywordPools["PIECE_NAMES"]!!.active = false
            keywordPools["FILES"]!!.active = true

        } else if (keyword in keywordPools["FILES"]!!.keywords) {
            cfi.moveCommandRaw.add(keyword)

            keywordPools["FILES"]!!.active = false
            keywordPools["RANKS"]!!.active = true

        } else if (keyword in keywordPools["RANKS"]!!.keywords) {
            cfi.moveCommandRaw.add(keyword)

            if (cfi.moveCommandFormLong && cfi.moveCommandRaw.size < 5) {

                keywordPools["RANKS"]!!.active = false
                keywordPools["FILES"]!!.active = true

            } else {

                command = MoveCommand(
                    piece_name = cfi.moveCommandRaw[0],
                    origin_coordinate = if (cfi.moveCommandFormLong)
                        Coordinate(cfi.moveCommandRaw[1], cfi.moveCommandRaw[2]) else
                        null,
                    destination_coordinate = if (cfi.moveCommandFormLong)
                        Coordinate(cfi.moveCommandRaw[3], cfi.moveCommandRaw[4]) else
                        Coordinate(cfi.moveCommandRaw[1], cfi.moveCommandRaw[2])
                )

                cfi.moveCommandRaw.clear()
                cfi.moveCommandFormLong = false

                keywordPools["RANKS"]!!.active = false
                keywordPools["PIECE_NAMES"]!!.active = true

            }

        }

        return command

    }


    override fun processingFinished() {
        TODO("Not yet implemented")
    }
    


}