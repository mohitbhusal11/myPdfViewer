package com.mypdfviewer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import com.mypdfviewer.screens.PdfScreen
import com.mypdfviewer.ui.theme.MypdfviewerTheme
import com.mypdfviewer.viewModel.PdfViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: PdfViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        intent?.data?.let { uri ->
            viewModel.openPdfFromUri(uri)
        }
        setContent {
            MypdfviewerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PdfScreen(
                        modifier = Modifier.padding(innerPadding),
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

