package com.example.cameraapp.audiorecored

import java.io.File

interface AudioPlayer {
    fun play(file : File)
    fun stop()
}