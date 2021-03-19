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
import com.example.mindchess.ml.FileClassifier
import com.example.mindchess.ml.RankClassifier
private const val LOG_TAG = "AudioTest"


class ChessActivity : AppCompatActivity() {

    private lateinit var viewModel: GameViewModel
    private var gameController: DefaultGameController? = null
    private var gameFactory: GameFactory? = null

    private val dangerousPermissions = arrayOf(
        Manifest.permission.RECORD_AUDIO
    )




    private val rankClassifier by lazy {
        RankClassifier.newInstance(this)
    }

    private val fileClassifier by lazy {
        FileClassifier.newInstance(this)
    }

    private val pieceNameClassifier by lazy {
        RankClassifier.newInstance(this)
    }

    private val specialWordClassifier by lazy {
        FileClassifier.newInstance(this)
    }

    private var audioInfo = AudioInfo(
        sampleRate = 22050,
        bufferSize = 22050,
        bufferOverlap = 0
    )

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
            audioInfo.sampleRate,
            audioInfo.bufferSize,
            audioInfo.bufferOverlap
        )

        val audioPlayer = AndroidAudioPlayer(
            TarsosDSPAudioFormat(
                audioInfo.sampleRate.toFloat(),
                16,
                1,
                false,
                false
            ), audioInfo.bufferSize, AudioManager.STREAM_MUSIC
        )

        val sifAnalyzer = SifAnalyzer(
            audioInfo = audioInfo,
            kss = KeywordSpottingService(rankClassifier, fileClassifier, pieceNameClassifier, specialWordClassifier),
            handler = object : OnCommandFormed {

                override fun handleCommand(command: Command) {
                    gameController?.processVoiceCommand(command)
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

        rankClassifier.close()
        fileClassifier.close()
        pieceNameClassifier.close()
        specialWordClassifier.close()
    }

}