package com.example.mindchess

import android.Manifest
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import be.tarsos.dsp.io.TarsosDSPAudioFormat
import be.tarsos.dsp.io.android.AndroidAudioPlayer
import be.tarsos.dsp.io.android.AudioDispatcherFactory
import com.example.mindchess.audio_processing.*
import org.tensorflow.lite.Interpreter
import java.io.File
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

private const val LOG_TAG = "AudioTest"


class ChessActivity : AppCompatActivity() {

    private lateinit var viewModel: GameViewModel
    private var gameController: DefaultGameController? = null
    private var gameFactory: GameFactory? = null



    private val dangerousPermissions = arrayOf(
        Manifest.permission.RECORD_AUDIO
    )

    var f_input_stream: FileInputStream = FileInputStream(File("model.tflite"))
    var f_channel: FileChannel = f_input_stream.getChannel()
    var tflite_model: MappedByteBuffer = f_channel.map(FileChannel.MapMode.READ_ONLY, 0, f_channel.size())

    private val interpreter = Interpreter(tflite_model)
    //private val interpreter = Interpreter(FileInputStream(File("src/main/ml/model.tflite")))

    // private val model = Model.newInstance(applicationContext)

    private var sampleRate = 22050
    private var bufferSize = 22050
    private var bufferOverlap = 0


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val chessView = ChessGameView(this)
        setContentView(chessView)

        val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            Log.v(LOG_TAG, "> requestPermissionLauncher - ${result.values}")
            if (result.values.all { it }) {
                Log.v(LOG_TAG, "All permissions granted.")
                startRecording()
            } else {
                Log.v(LOG_TAG, "Not all permissions granted.")
            }
        }

        requestPermissionLauncher.launch(dangerousPermissions)

        gameFactory = DefaultGameFactory()
        gameController = DefaultGameController(gameFactory!!.createNewGame())
        gameController!!.addViewModelListener(chessView)


        chessView.addViewListener(object : ChessGameViewListener {
            override fun onCoordinateSelected(coordinate: Coordinate) {
                gameController?.processTouch(coordinate)
            }
        })

    }




    @RequiresApi(Build.VERSION_CODES.M)
    private fun startRecording() {
        val audioDispatcher = AudioDispatcherFactory.fromDefaultMicrophone(
            sampleRate,
            bufferSize,
            bufferOverlap
        )

        val audioPlayer = AndroidAudioPlayer(
            TarsosDSPAudioFormat(
                sampleRate.toFloat(),
                16,
                1,
                false,
                false
            ), bufferSize, AudioManager.STREAM_MUSIC
        )

        val sifAnalyzer = SifAnalyzer(
            KeywordSpottingService(interpreter),
            object : OnCommandFormed {

                override fun handleCommand(command: Command) {
                    gameController?.processVoiceCommand(command)

//                if (command is MoveCommand) {
//                    Log.v(LOG_TAG, "MOVE COMMAND " + command.piece_name + " " + command.origin_coordinate.toString() + " " + command.destination_coordinate.toString())
//                    gameController?.processMoveCommand(command)
//                } else if (command is SpecialCommand) {
//                    Log.v(LOG_TAG, "SPECIAL COMMAND")
//                    gameController?.processSpecialCommand(command)
//                }


                }

            })


        //audioDispatcher.addAudioProcessor(audioPlayer)
        audioDispatcher.addAudioProcessor(sifAnalyzer)


        Thread(audioDispatcher, "Recording Thread").start()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        interpreter.close()
    }

}