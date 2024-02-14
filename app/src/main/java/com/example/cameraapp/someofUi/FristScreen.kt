import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture.OnImageCapturedCallback
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Recording
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.video.AudioConfig
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.currentCompositionLocalContext
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.cameraapp.CameraViewModel
import com.example.cameraapp.MainActivity.Companion.CAMERAX_PERMISSIONS
import com.example.cameraapp.MainViewModel
import com.example.cameraapp.classification.Classification
import com.example.cameraapp.classification.LandmarkImageAnalyzer
import com.example.cameraapp.classification.TfLiteLandMarkerClassification
import com.example.cameraapp.someofUi.cameraPerview
import kotlinx.coroutines.launch
import java.io.File


private var recording:Recording ?= null

private fun hasRequiredPermissions(context: Context): Boolean {
    return CAMERAX_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            context,
            it
        ) == PackageManager.PERMISSION_GRANTED
    }
}


@SuppressLint("MissingPermission")
private fun record (controller: LifecycleCameraController,context: Context){
    if (recording!=null){
        recording?.stop()
        recording= null
        return
    }
    if(!hasRequiredPermissions(context)){
        return

    }
    val outputfile = File(context.filesDir,"myRecord.mp4")
    recording= controller.startRecording(
        FileOutputOptions.Builder((outputfile)).build(),
        AudioConfig.create(true),
        ContextCompat.getMainExecutor(context)
    ){
        when(it){
            is VideoRecordEvent.Finalize->{
                if (it.hasError()){
                    recording?.close()
                    recording= null
                    Toast.makeText(context,"video capture failed", Toast.LENGTH_LONG).show()

                }
                else{
                    Toast.makeText(context,"video capture succssed", Toast.LENGTH_LONG).show()


                }
            }

        }
    }

}


private fun takePhoto (controller: LifecycleCameraController,onPhotoTake: (Bitmap)->Unit,context:Context){
    controller.takePicture(
        ContextCompat.getMainExecutor(context),
        object : OnImageCapturedCallback() {



            override fun onError(exception: ImageCaptureException) {
                super.onError(exception)
            }

            override fun onCaptureSuccess(image: ImageProxy) {
                super.onCaptureSuccess(image)
                val matrix = Matrix().apply {
                    postRotate(image.imageInfo.rotationDegrees.toFloat())
                }
                val rotatedBitmap = Bitmap.createBitmap(
                    image.toBitmap(),
                    0,
                    0,
                    image.width,
                    image.height,
                    matrix,
                    true
                )

                onPhotoTake(rotatedBitmap)

            }
        }
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController, viewModel: CameraViewModel) {
    val context = LocalContext.current

    val scoffeldState = rememberBottomSheetScaffoldState()
    val scope = rememberCoroutineScope()
    var classifications by remember {
        mutableStateOf(emptyList<Classification>())
    }
    val analyzer = remember {
        LandmarkImageAnalyzer(
            classifier = TfLiteLandMarkerClassification(
                context = context
            ),
            onResults = {
                classifications = it
            }
        )
    }



    val controller = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(
                CameraController.IMAGE_CAPTURE or CameraController.VIDEO_CAPTURE or CameraController.IMAGE_ANALYSIS
            )
            setImageAnalysisAnalyzer(ContextCompat.getMainExecutor(context),
            analyzer)
        }

    }
    val bitmap by viewModel.bitmaps.collectAsState()

    BottomSheetScaffold(scaffoldState = scoffeldState, sheetPeekHeight = 0.dp, sheetContent = {PhotoBottomSheetContent(bitmaps = bitmap,)}) {

        Box(modifier = Modifier.padding(it)) {

            cameraPerview(controller = controller, modifier = Modifier.fillMaxSize())
            IconButton(
                onClick = {
                    controller.cameraSelector =
                        if (controller.cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                            CameraSelector.DEFAULT_FRONT_CAMERA
                        } else {
                            CameraSelector.DEFAULT_BACK_CAMERA
                        }

                },
                modifier = Modifier.offset {
                    IntOffset(
                        16.dp.toPx().toInt(),
                        16.dp.toPx().toInt()
                    )
                }) {
                Icon(imageVector = Icons.Default.Cameraswitch, contentDescription = null)

            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
            ) {
                classifications.forEach {
                    Text(
                        text = it.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(8.dp),
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                } }
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = {
                        scope.launch {
                            scoffeldState.bottomSheetState.expand()
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Photo,
                        contentDescription = "Open gallery"
                    )
                }
                IconButton(
                    onClick = {
                        takePhoto(
                            controller = controller,
                            onPhotoTake = viewModel::onTakePhoto
                            ,context = context
                        )
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = "Take photo"
                    )
                }
                IconButton(
                    onClick = {
                        record(controller,context)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Videocam,
                        contentDescription = "Record video"
                    )
                }

            }
        }



    }


}
@Composable
fun PhotoBottomSheetContent(
    bitmaps: List<Bitmap>,
    modifier: Modifier = Modifier
) {
    if(bitmaps.isEmpty()) {
        Box(
            modifier = modifier
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("There are no photos yet")
        }
    } else {
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalItemSpacing = 16.dp,
            contentPadding = PaddingValues(16.dp),
            modifier = modifier
        ) {
            items(bitmaps){
                Image(
                    bitmap =it.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                )
            }

        }
    }
}