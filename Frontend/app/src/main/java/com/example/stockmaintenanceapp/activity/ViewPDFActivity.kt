package com.example.stockmaintenanceapp.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.FileProvider
import com.example.stockmaintenanceapp.R
import com.example.stockmaintenanceapp.adapter.PdfDocumentAdapter
import com.github.barteksc.pdfviewer.PDFView
import java.io.File

class ViewPDFActivity : AppCompatActivity() {

    lateinit var pdfView: PDFView
    lateinit var toolbar: Toolbar
    var filePathCustomerDetail: String? = null
    var filePathOwnPurchase: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_p_d_f)

        initView()
        if (filePathCustomerDetail != null) {
            pdfView.fromFile(File(filePathCustomerDetail!!)).defaultPage(0)
                .spacing(10)
                .load()
        }
        if (filePathOwnPurchase != null) {
            pdfView.fromFile(File(filePathOwnPurchase!!)).defaultPage(0)
                .spacing(10)
                .load()
        }
    }

    private fun initView() {
        filePathCustomerDetail = intent.getStringExtra("filePath")
        filePathOwnPurchase = intent.getStringExtra("filePathOwnPurchase")
        toolbar = findViewById(R.id.toolbar)
        pdfView = findViewById(R.id.idPDFView)
        setSupportActionBar(toolbar)
        supportActionBar?.setTitle("View PDF")

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            finish()
            return true
        }

        if (id == R.id.share) {
            val context = this

            if (filePathCustomerDetail != null) {

                val intent = Intent(Intent.ACTION_SEND).apply {


                    //file type, can be "application/pdf", "text/plain", etc
                    type = "application/pdf"

                    //in my case, I have used FileProvider, thats is a better approach
                    putExtra(
                        Intent.EXTRA_STREAM, if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            FileProvider.getUriForFile(
                                context, context.getPackageName(),
                                File(filePathCustomerDetail)
                            )

                        } else {
                            Uri.fromFile(File(filePathCustomerDetail))
                        }
                    )

                }
                startActivity(Intent.createChooser(intent, "Share Via"))
            }

            if(filePathOwnPurchase!=null){
                val intent = Intent(Intent.ACTION_SEND).apply {


                    //file type, can be "application/pdf", "text/plain", etc
                    type = "application/pdf"

                    //in my case, I have used FileProvider, thats is a better approach
                    putExtra(
                        Intent.EXTRA_STREAM,  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            FileProvider.getUriForFile(
                                context, context.getPackageName(),
                                File(filePathOwnPurchase)
                            )

                        }
                        else{
                            Uri.fromFile(File(filePathOwnPurchase))
                        }
                    )

                }
                startActivity(Intent.createChooser(intent, "Share Via"))
            }
        }
        if (id == R.id.print) {
            val printManager = getSystemService(Context.PRINT_SERVICE) as PrintManager
            try {
                if(filePathCustomerDetail!=null){
                    val printDocumentAdapter = PdfDocumentAdapter(this, filePathCustomerDetail)
                    printManager.print(
                        "Invoice",
                        printDocumentAdapter,
                        PrintAttributes.Builder().build()
                    )
                }

                if(filePathOwnPurchase!=null){
                    val printDocumentAdapter = PdfDocumentAdapter(this, filePathOwnPurchase)
                    printManager.print(
                        "OPurchase",
                        printDocumentAdapter,
                        PrintAttributes.Builder().build()
                    )
                }

            } catch (e: Exception) {
                e.message?.let { Log.d("PDF", it) }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflator = menuInflater
        inflator.inflate(R.menu.share, menu)
        return true

    }
}