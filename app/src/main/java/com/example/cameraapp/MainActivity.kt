package com.example.cameraapp

import MainScreen
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Recording
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.video.AudioConfig
import androidx.compose.foundation.layout.*
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.cameraapp.audiorecored.AudioPlayerImp
import com.example.cameraapp.audiorecored.AudioRecordScreen
import com.example.cameraapp.audiorecored.RecorderImpl
import com.example.cameraapp.classification.Classification
import com.example.cameraapp.classification.LandmarkImageAnalyzer
import com.example.cameraapp.classification.TfLiteLandMarkerClassification
import com.example.cameraapp.permissions.RequestMultiplePermissions
import com.example.cameraapp.someofUi.CameraPermissionTextProvider
import com.example.cameraapp.someofUi.PermissionDialog
import com.example.cameraapp.someofUi.RecordAudioPermissionTextProvider
import com.example.cameraapp.someofUi.cameraPerview
import com.example.cameraapp.ui.theme.CameraAppTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import java.io.File


@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    private var recording:Recording? =null
    private val audioRecorder by lazy {
    RecorderImpl(applicationContext)
}

private val player by lazy {
    AudioPlayerImp(applicationContext)
}


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CameraAppTheme {
                val viewModel = viewModel<MainViewModel>()
                val cameraViewModel = viewModel<CameraViewModel>()



                val dialogQueue = viewModel.visiblePermissionDialogQueue


                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    MyApp(viewModel,cameraViewModel)

                }


            }


        }
    }


    companion object {
        val CAMERAX_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
        )
    }


    @Composable
    fun MyApp(viewModel: MainViewModel,cameraViewModel: CameraViewModel) {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = "screen1") {
            composable("screen1") { FristScreen(navController, viewModel) }
            composable("AudioRecordScreen") { AudioRecordScreen(navController,player,audioRecorder) }
            composable("Permissionbyaccompanist") { Permissionbyaccompanist(navController) }

            composable("screen2") { MainScreen(navController, cameraViewModel) }
        }
    }
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Permissionbyaccompanist(navController: NavController){
    RequestMultiplePermissions(
        permissions = listOf(
            Manifest.permission.RECORD_AUDIO
        ), navController = navController )

}
    @Composable
    fun FristScreen(navController: NavController, viewModel: MainViewModel) {
        // Your Composable content for Screen1
        val dialogQueue = viewModel.visiblePermissionDialogQueue

        val multiplePermissionResultLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions(),
            onResult = { perms ->
                MainActivity.CAMERAX_PERMISSIONS.forEach { permission ->
                    viewModel.onPermissionResult(
                        permission = permission,
                        isGranted = perms[permission] == true
                    )
                    if (perms[Manifest.permission.CAMERA] == true && perms[Manifest.permission.RECORD_AUDIO] == true) {
                        navController.navigate("screen2")

                    }


                }
            }
        )


        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = {
                navController.navigate("Permissionbyaccompanist")

            }) {
                Text(text = "Audio Recorder")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                multiplePermissionResultLauncher.launch(CAMERAX_PERMISSIONS)
            }) {
                Text(text = "VideoRecorder")
            }
        }

        dialogQueue
            .reversed()
            .forEach { permission ->
                PermissionDialog(
                    permissionTextProvider = when (permission) {
                        Manifest.permission.CAMERA -> {
                            CameraPermissionTextProvider()
                        }

                        Manifest.permission.RECORD_AUDIO -> {
                            RecordAudioPermissionTextProvider()
                        }


                        else -> return@forEach
                    },
                    isPermanentlyDeclined = !shouldShowRequestPermissionRationale(
                        permission
                    ),
                    onDismiss = viewModel::dismissDialog,
                    onOkClick = {
                        viewModel.dismissDialog()
                        multiplePermissionResultLauncher.launch(
                            arrayOf(permission)
                        )
                    },
                    onGoToAppSettingsClick = ::openAppSettings
                )
            }

    }







}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CameraAppTheme {
    }
}

fun Activity.openAppSettings() {
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    ).also(::startActivity)
}