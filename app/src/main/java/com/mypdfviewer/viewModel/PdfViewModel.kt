package com.mypdfviewer.viewModel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PdfViewModel(app: Application): AndroidViewModel(app) {
    private lateinit var pdfRender: PdfRenderer
    private lateinit var fileDescriptor: ParcelFileDescriptor
    private var currentPageIndex = 0;
    var pageCount = 0

    private val _bitmap = MutableStateFlow<Bitmap?>(null)
    val bitmap = _bitmap.asStateFlow()

    fun openPdfFromUri(uri: Uri){
        viewModelScope.launch {
            val context = getApplication<Application>()
            val fd = context.contentResolver.openFileDescriptor(uri, "r") ?: return@launch
            fileDescriptor = fd
            pdfRender = PdfRenderer(fd)
            pageCount = pdfRender.pageCount
            showPage(0)
        }
    }

    fun showPage(index: Int){
        if(index < 0 || index >= pdfRender.pageCount){
            return
        }
        pdfRender.openPage(index).use { page ->
            val width = page.width * 2
            val height = page.height * 2
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            canvas.drawColor(Color.WHITE)
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            _bitmap.value = bitmap
        }
    }

    fun nextPage() {
        if (currentPageIndex < pageCount - 1) showPage(currentPageIndex + 1)
    }

    fun prevPage() {
        if (currentPageIndex > 0) showPage(currentPageIndex - 1)
    }

    override fun onCleared() {
        super.onCleared()
        if (::pdfRender.isInitialized) pdfRender.close()
        if (::fileDescriptor.isInitialized) fileDescriptor.close()
    }

}