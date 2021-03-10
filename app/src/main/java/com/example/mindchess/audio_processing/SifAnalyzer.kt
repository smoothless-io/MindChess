package com.example.mindchess.audio_processing

import android.util.Log
import be.tarsos.dsp.AudioEvent
import be.tarsos.dsp.AudioProcessor
import com.example.mindchess.Coordinate


private const val LOG_TAG = "SifExtractorTest"

class SifAnalyzer(var kss: KeywordSpottingService, var handler: OnCommandFormed) : AudioProcessor {

    // SIF stands for silence-isolated frame.
    private var sif_buffer: ArrayList<String> = arrayListOf() // Or other way of remembering the last part of the last sif.

    private var special_commands: Array<SpecialCommand>

    private var keyword_pools: MutableMap<String, KeywordPool> = mutableMapOf()

    private data class CommandFormingInfo(
        var move_command_raw: ArrayList<String>,
        var move_command_form_long: Boolean,
        var special_command_raw: ArrayList<String>,
        var special_command_possible: Boolean

    )

    private var cfi: CommandFormingInfo


    init {

        special_commands = arrayOf(
            SpecialCommand(arrayListOf("RESIGN")),
            SpecialCommand(arrayListOf("CLOCK")),
            SpecialCommand(arrayListOf("UNDO")),
            SpecialCommand(arrayListOf("WHAT", "CAN", "JAKUB", "EAT"))
        )


        keyword_pools["PIECE_NAMES"] = KeywordPool(listOf<String>("PAWN", "KNIGHT", "BISHOP", "ROOK", "QUEEN", "KING"), true, 0)
        keyword_pools["FILES"] = KeywordPool(listOf<String>("A", "B", "C", "D", "E", "F", "G", "H"), false, 1)
        keyword_pools["RANKS"] = KeywordPool(listOf<String>("1", "2", "3", "4", "5", "6", "7", "8"), false, 2)
        keyword_pools["SPECIAL_WORDS"] = KeywordPool(listOf<String>("RESIGN", "CLOCK", "FROM", "UNDO"), true, 3)

        cfi = CommandFormingInfo(arrayListOf(), false, arrayListOf(), false)
    }


    fun extractSifs(buffer: FloatArray) : List<FloatArray> {
        val sifs = mutableListOf<FloatArray>()

        sifs.add(buffer.sliceArray(100..200))


        return sifs
    }

    override fun process(audioEvent: AudioEvent?): Boolean {

        val sifs = extractSifs(audioEvent!!.floatBuffer)

        for (sif in sifs) {
            val keyword = kss.predict(sif, keyword_pools.values)

            if (keyword.length > 0) {
                val command = formCommand(keyword)


                if (command != null) {
                    handler.handleCommand(command)
                }
            }
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
                            if (special_command.command.size > cfi.special_command_raw.size) cfi.special_command_raw.size else special_command.command.size)
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