package com.mypdfviewer.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.selection.triStateToggleable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.mypdfviewer.viewModel.PdfViewModel

@Composable
fun PdfScreen(modifier: Modifier = Modifier, viewModel: PdfViewModel) {
    val bitmap by viewModel.bitmap.collectAsState()
    var scale by remember { mutableStateOf(1f) }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri: Uri? ->
            uri?.let {
                viewModel.openPdfFromUri(it)
                scale = 1f
            }
        }
    )

    Column(modifier = modifier.fillMaxSize()) {
        bitmap?.let {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .transformable(
                        state = rememberTransformableState { zoomChange, panChange, _ ->
                            scale *= zoomChange

                        }
                    )
                    .graphicsLayer(
                        scaleX = scale.coerceIn(1f, 5f),
                        scaleY = scale.coerceIn(1f, 5f)
                    )
                    .padding(8.dp)
                    .background(Color.White)
            ) {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "PDF Page",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp))
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = { viewModel.prevPage(); scale = 1f }) {
                    Text("Previous")
                }
                Button(onClick = { viewModel.clearBitMap() }) {
                    Text("Home")
                }
                Button(onClick = { viewModel.nextPage(); scale = 1f }) {
                    Text("Next")
                }
            }
        } ?: run {
            Button(
                onClick = { filePickerLauncher.launch(arrayOf("application/pdf")) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text("Pick a PDF")
            }

            Button(
                onClick = { filePickerLauncher.launch(arrayOf("application/msword",
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                    "application/vnd.oasis.opendocument.text",
                    "application/vnd.google-apps.document")) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text("Pick a Docs")
            }
        }


    }
}