package com.example.cameraapp
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition

@Composable
fun InfoDialog(
    title: String?="Message",
    desc: String?="Your Message",
    onDismiss: () -> Unit,
    navigate : () ->Unit
) {
    Dialog(
        onDismissRequest = onDismiss
    ) {

        Box(
            modifier = Modifier
                .height(460.dp)
        ) {
            Column(
                modifier = Modifier
            ) {
                Spacer(modifier = Modifier.height(150.dp))
                Box(
                    modifier = Modifier
                        .height(490.dp)
                        .background(
                            color = MaterialTheme.colorScheme.onPrimary,
                            shape = RoundedCornerShape(25.dp, 10.dp, 25.dp, 10.dp)
                        )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = title!!,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .padding(top = 10.dp)
                                .fillMaxWidth(),
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                        Spacer(modifier = Modifier.height(8.dp))


                        Text(
                            text = desc!!,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .padding(top = 10.dp, start = 25.dp, end = 25.dp)
                                .fillMaxWidth(),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                        Spacer(modifier = Modifier.height(24.dp))


                        Button(
                            onClick = onDismiss,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(5.dp))
                        ) {
                            Text(
                                text = "Later",
                                color = Color.White
                            )
                        }
                        ElevatedButton(
                            onClick = navigate,
                            colors= ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(5.dp))
                        ) {
                            Text(
                                text = "Enable Location",
                                color = Color.White
                            )
                        }


                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }
            }
            HeaderImage(
                modifier = Modifier
                    .size(200.dp)
                    .align(Alignment.TopCenter)
                /*.border(
                    border = BorderStroke(width = 5.dp, color = Color.White),
                    shape = CircleShape
                )*/
            )
        }
    }
}
@Composable
fun HeaderImage(modifier: Modifier) {
    val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.audio))
    val progress by animateLottieCompositionAsState(composition = composition)

    LottieAnimation(
        composition = composition,
        progress = progress,
        modifier = modifier
    )
}