package com.example.bookxpert.presentation.pdf

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.bookxpert.databinding.FragmentPdfViewerBinding
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

class PdfViewerFragment : Fragment() {

    private lateinit var binding: FragmentPdfViewerBinding
    private val pdfUrl = "https://fssservices.bookxpert.co/GeneratedPDF/Companies/nadc/2024-2025/BalanceSheet.pdf"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPdfViewerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        downloadAndDisplayPdf(pdfUrl)
    }

    private fun downloadAndDisplayPdf(pdfUrl: String) {
        Thread {
            try {
                val pdfFile = downloadPdf(pdfUrl)
                if (pdfFile != null) {
                    activity?.runOnUiThread {
                        displayPdf(pdfFile)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

    private fun downloadPdf(url: String): File? {
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()

        if (!response.isSuccessful) {
            return null
        }

        val file = File(requireContext().cacheDir, "downloaded_pdf.pdf")
        val inputStream: InputStream = response.body?.byteStream() ?: return null
        val outputStream: OutputStream = FileOutputStream(file)

        val buffer = ByteArray(1024)
        var length: Int
        while (inputStream.read(buffer).also { length = it } != -1) {
            outputStream.write(buffer, 0, length)
        }

        outputStream.flush()
        outputStream.close()
        inputStream.close()

        return file
    }

    private fun displayPdf(file: File) {
        binding.pdfview.fromFile(file)
            .enableSwipe(true)
            .onLoad { pageCount ->
            }
            .onPageChange { page, pageCount ->
            }
            .load()
    }
}
