package com.example.cameraapp.audiorecored

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import java.io.File


// write it in Activity
//private val recorder by lazy {
//    AndroidAudioRecorder(applicationContext)
//}
//
//private val player by lazy {
//    AndroidAudioPlayer(applicationContext)
//}
//private var audioFile: File? = null
@Composable
fun AudioRecordScreen(navController: NavController, player: AudioPlayerImp, recorder: RecorderImpl) {
     var audioFile: File? = null
   val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            File(context.cacheDir, "audio.mp3").also {
                recorder.start(it)
                audioFile = it
            }
        }) {
            Text(text = "Start recording")
        }
        Button(onClick = {
            recorder.stop()
        }) {
            Text(text = "Stop recording")
        }
        Button(onClick = {
            player.play(audioFile ?: return@Button)
        }) {
            Text(text = "Play")
        }
        Button(onClick = {
            player.stop()
        }) {
            Text(text = "Stop playing")
        }
    }
}