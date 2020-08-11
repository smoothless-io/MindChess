package com.example.mindchess

import android.Manifest
import android.content.pm.PackageManager
import android.media.*
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity

import be.tarsos.dsp.AudioDispatcher
import be.tarsos.dsp.AudioEvent
import be.tarsos.dsp.AudioProcessor
import be.tarsos.dsp.io.TarsosDSPAudioFormat
import be.tarsos.dsp.io.android.AndroidAudioInputStream
import be.tarsos.dsp.io.android.AudioDispatcherFactory
import be.tarsos.dsp.pitch.PitchDetectionHandler
import be.tarsos.dsp.pitch.PitchDetectionResult
import be.tarsos.dsp.pitch.PitchProcessor

private const val LOG_TAG = "AudioTest"


class ChessActivity : AppCompatActivity() {

    private var audioDispatcher: AudioDispatcher? = null

    private val dangerousPermissions = arrayOf(
        Manifest.permission.RECORD_AUDIO
    )

    private var

    private var isRecording = false
    private var sampleRate = 22050
    private var bufferSize = 1024

    private var audioRecord: AudioRecord? = null
    private var audioTrack: AudioTrack? = null
    private var recordingThread: Thread? = null


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chess)

        val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                result ->
            Log.v(LOG_TAG, "> requestPermissionLauncher - ${result.values}")
            if (result.values.all { it }) {
                Log.v(LOG_TAG, "All permissions granted.")
                startRecording()
            } else {
                Log.v(LOG_TAG, "Not all permissions granted.")
            }
        }

        requestPermissionLauncher.launch(dangerousPermissions)

        audioDispatcher = AudioDispatcherFactory.fromDefaultMicrophone(sampleRate, bufferSize, 0)


    }




    @RequiresApi(Build.VERSION_CODES.M)
    private fun startRecording() {
        var minBufferSize = AudioRecord.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )

        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            minBufferSize
        )

        audioTrack = AudioTrack.Builder()
            .setAudioAttributes(AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build())
            .setAudioFormat(AudioFormat.Builder()
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setSampleRate(sampleRate)
                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                .build())
            .setBufferSizeInBytes(minBufferSize)
            .build()


        recordingThread = Thread(Runnable {

            var size = 0
            val byteArray = ByteArray(minBufferSize)

            isRecording = true
            audioRecord?.startRecording()

            while (isRecording && audioRecord != null) {

                size = audioRecord!!.read(byteArray, 0, minBufferSize)

                if (AudioRecord.ERROR_INVALID_OPERATION != size) {
                    audioTrack!!.write(byteArray, 0, minBufferSize)
                    audioTrack!!.play()
                    Log.v(LOG_TAG, "Writing and playing something.")
                }
            }

        })

        recordingThread?.start()
    }

    override fun onPause() {
        super.onPause()
        isRecording = false
        audioRecord?.release()
        audioRecord = null
        audioTrack?.release()
        audioTrack = null
    }

}
