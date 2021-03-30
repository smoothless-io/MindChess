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

    private var special_commands: Array<SpecialCommand> = arrayOf(
        SpecialCommand(arrayListOf("RESIGN")),
        SpecialCommand(arrayListOf("CLOCK")),
        SpecialCommand(arrayListOf("UNDO")),
        SpecialCommand(arrayListOf("WHAT", "CAN", "JAKUB", "EAT"))
    )

    private data class CommandFormingInfo(
        var move_command_raw: ArrayList<String>,
        var move_command_form_long: Boolean,
        var special_command_raw: ArrayList<String>,
        var special_command_possible: Boolean

    )

    private var cfi = CommandFormingInfo(arrayListOf(), false, arrayListOf(), false)


    private var keyword_pools: MutableMap<String, KeywordPool> = mutableMapOf()
    init {
        keyword_pools["PIECE_NAMES"] = KeywordPool(listOf("PAWN", "KNIGHT", "BISHOP", "ROOK", "QUEEN", "KING"), true, "PIECE_NAME_CLASSIFIER")
        keyword_pools["FILES"] = KeywordPool(listOf("A", "B", "C", "D", "E", "F", "G", "H"), false, "FILE_CLASSIFIER")
        keyword_pools["RANKS"] = KeywordPool(listOf("1", "2", "3", "4", "5", "6", "7", "8"), false, "RANK_CLASSIFIER")
        keyword_pools["SPECIAL_WORDS"] = KeywordPool(listOf("RESIGN", "CLOCK", "FROM", "UNDO"), true, "SPECIAL_WORD_")
    }


    private fun extractSifRanges(buffer: FloatArray) : ArrayList<Pair<Int, Int>> {
        val sifRanges = ArrayList<Pair<Int, Int>>()

        //TODO Implement the logic prototyped in python here

        sifRanges.add(Pair(0, buffer.size - 1))
        return sifRanges
    }


    private fun generateAudioEvent(audioEvent: AudioEvent, startIndex: Int, endIndex: Int) : AudioEvent {

        //TODO Given the original audioEvent and sif range, generate a new audioEvent, with the sif centered, rest zeros.

        val correctedAudioEvent = AudioEvent(TarsosDSPAudioFormat(0.0f, 0, 0, false, false))

        correctedAudioEvent.floatBuffer = FloatArray(audioEvent.floatBuffer.size)

        Log.i(LOG_TAG, "New float buffer size: %s".format(correctedAudioEvent.floatBuffer.size.toString()))



        return correctedAudioEvent


    }

    override fun process(audioEvent: AudioEvent?): Boolean {


        val sifRanges = extractSifRanges(audioEvent!!.floatBuffer)


        for (sifRange in sifRanges) {

            val generatedAudioEvent = generateAudioEvent(audioEvent, sifRange.first, sifRange.second)

            val mfcc_extractor = MFCC(audioEvent.floatBuffer.size, audioInfo.sampleRate.toFloat(), 44, 13, 300f, 3000f)
            mfcc_extractor.process(generatedAudioEvent)

            Log.v(LOG_TAG, "Eeh so this is mfcc: %s".format(mfcc_extractor.mfcc.get(0)))
//
//
//            val mfcc_buffer = ByteBuffer.allocate(44 * 13)
//            val predictions = kss.predict(mfcc_buffer, keyword_pools.values)
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


        if (keyword in keyword_pools["SPECIAL_WORDS"]!!.keywords) {

            if (keyword == "FROM" && cfi.move_command_raw.size == 1) {
                cfi.move_command_form_long = true

            } else {
                cfi.special_command_raw.add(keyword)

                cfi.special_command_possible = false
                for (special_command in special_commands) {

                    if (cfi.special_command_raw == special_command.command) {
                        command = special_command

                        cfi.special_command_raw = arrayListOf()

                    } else if (cfi.special_command_raw == special_command.command.subList(
                            0,
                            if (special_command.command.size > cfi.special_command_raw.size) cfi.special_command_raw.size else special_command.command.size
                        )
                    ) {
                        cfi.special_command_possible = true
                    }

                }

                if (!cfi.special_command_possible) {
                    cfi.special_command_raw.clear()
                }
            }

        } else if (keyword in keyword_pools["PIECE_NAMES"]!!.keywords) {
            cfi.move_command_raw.add(keyword)

            keyword_pools["PIECE_NAMES"]!!.active = false
            keyword_pools["FILES"]!!.active = true

        } else if (keyword in keyword_pools["FILES"]!!.keywords) {
            cfi.move_command_raw.add(keyword)

            keyword_pools["FILES"]!!.active = false
            keyword_pools["RANKS"]!!.active = true

        } else if (keyword in keyword_pools["RANKS"]!!.keywords) {
            cfi.move_command_raw.add(keyword)

            if (cfi.move_command_form_long && cfi.move_command_raw.size < 5) {

                keyword_pools["RANKS"]!!.active = false
                keyword_pools["FILES"]!!.active = true

            } else {

                command = MoveCommand(
                    piece_name = cfi.move_command_raw[0],
                    origin_coordinate = if (cfi.move_command_form_long)
                        Coordinate(cfi.move_command_raw[1], cfi.move_command_raw[2]) else
                        null,
                    destination_coordinate = if (cfi.move_command_form_long)
                        Coordinate(cfi.move_command_raw[3], cfi.move_command_raw[4]) else
                        Coordinate(cfi.move_command_raw[1], cfi.move_command_raw[2])
                )

                cfi.move_command_raw.clear()
                cfi.move_command_form_long = false

                keyword_pools["RANKS"]!!.active = false
                keyword_pools["PIECE_NAMES"]!!.active = true

            }

        }

        return command

    }


    override fun processingFinished() {
        TODO("Not yet implemented")
    }
    


}