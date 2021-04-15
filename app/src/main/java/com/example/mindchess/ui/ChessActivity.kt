package com.example.mindchess.ui

import android.Manifest
import android.content.res.AssetManager
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import be.tarsos.dsp.AudioEvent
import be.tarsos.dsp.io.TarsosDSPAudioFormat
import be.tarsos.dsp.io.android.AndroidAudioPlayer
import be.tarsos.dsp.io.android.AudioDispatcherFactory
import be.tarsos.dsp.wavelet.HaarWaveletFileWriter
import be.tarsos.dsp.writer.WriterProcessor
import com.example.mindchess.R
import com.example.mindchess.audio_processing.*
import com.example.mindchess.ml.FileClassifier
import com.example.mindchess.ml.RankClassifier
import java.io.File
import java.io.RandomAccessFile

private const val LOG_TAG = "AudioTest"


class ChessActivity : AppCompatActivity() {

    private lateinit var recordingThread: Thread
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
        bufferOverlap = 0,
        sifOffset = 882
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
        var fileCounter = 0
        val currentKeyword = "pawn"


        val audioDispatcher = AudioDispatcherFactory.fromDefaultMicrophone(
            audioInfo.sampleRate,
            audioInfo.bufferSize,
            audioInfo.bufferOverlap
        )

        val sifAnalyzer = SifAnalyzer(
            audioInfo = audioInfo,
            kss = KeywordSpottingService(rankClassifier, fileClassifier, pieceNameClassifier, specialWordClassifier),
            handler = object : OnCommandFormed {

                override fun handleCommand(command: Command) {
                    gameController?.processVoiceCommand(command)
                }

                override fun saveSIF(audioEvent: AudioEvent) {

                    val outputFile = File(getOutputDirectory(), currentKeyword + "_" + fileCounter.toString() + ".pcm")
                    val randomAccessFile = RandomAccessFile(outputFile, "rw")
                    val fileWriter = WriterProcessor(TarsosDSPAudioFormat(audioInfo.sampleRate.toFloat(), 16, 1, true, false), randomAccessFile)

                    fileWriter.process(audioEvent)
                    fileWriter.processingFinished()

                    fileCounter += 1
                }

            })

        audioDispatcher.addAudioProcessor(sifAnalyzer)

        recordingThread = Thread(audioDispatcher, "Recording Thread")
        recordingThread.start()
    }


    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() } }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

    override fun onDestroy() {
        super.onDestroy()

        recordingThread.interrupt()

        rankClassifier.close()
        fileClassifier.close()
        pieceNameClassifier.close()
        specialWordClassifier.close()
    }

}