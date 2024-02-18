package com.example.cameraapp.audiorecored

import java.io.File

interface Recorder {
    fun start(outputFile: File)
    fun stop ()
}