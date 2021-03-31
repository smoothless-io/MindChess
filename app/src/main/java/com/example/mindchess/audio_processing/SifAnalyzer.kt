package com.example.mindchess.audio_processing

import android.util.Log
import be.tarsos.dsp.AudioEvent
import be.tarsos.dsp.AudioProcessor
import be.tarsos.dsp.io.TarsosDSPAudioFormat
import be.tarsos.dsp.mfcc.MFCC
import com.example.mindchess.ui.Coordinate


private const val LOG_TAG = "SifExtractorTest"

class SifAnalyzer(var audioInfo: AudioInfo, var kss: KeywordSpottingService, var handler: OnCommandFormed) : AudioProcessor {

    // SIF stands for silence-isolated frame.
    private var sif_buffer: ArrayList<String> = arrayListOf() // Or other way of remembering the last part of the last sif.

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
        keywordPools["SPECIAL_WORDS"] = KeywordPool(listOf("RESIGN", "CLOCK", "FROM", "UNDO"), true, "SPECIAL_WORD_")
    }


    private fun extractSifBorders(buffer: FloatArray) : ArrayList<Pair<Int, Int>> {
        val sifBorders = ArrayList<Pair<Int, Int>>()

        //TODO Implement the logic prototyped in python here

        sifBorders.add(Pair(buffer.size / 4, 3 * buffer.size / 4))
        return sifBorders
    }


    private fun generateAudioEvent(audioEvent: AudioEvent, startIndex: Int, endIndex: Int) : AudioEvent {

        val correctedAudioEvent = AudioEvent(TarsosDSPAudioFormat(0.0f, 0, 0, false, false))
        correctedAudioEvent.floatBuffer = FloatArray(audioEvent.floatBuffer.size)

        audioEvent.floatBuffer.copyInto(
            correctedAudioEvent.floatBuffer,
            correctedAudioEvent.floatBuffer.size / 2 - (endIndex - startIndex) / 2,
            startIndex,
            endIndex
        )


        return correctedAudioEvent

    }

    override fun process(audioEvent: AudioEvent?): Boolean {


        val sifBorders = extractSifBorders(audioEvent!!.floatBuffer)


        for (sifBorder in sifBorders) {

            val generatedAudioEvent = generateAudioEvent(audioEvent, sifBorder.first, sifBorder.second)

            val mfccExtractor = MFCC(audioEvent.floatBuffer.size, audioInfo.sampleRate.toFloat(), 13, 44, 300f, 3000f)
            mfccExtractor.process(generatedAudioEvent)

            Log.v(LOG_TAG, "Eeh so this is mfcc: %s".format(mfccExtractor.mfcc.size))
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