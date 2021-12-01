package com.example.stockmaintenanceapp.adapter

import android.content.Context
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.util.Log
import com.example.stockmaintenanceapp.activity.ViewPDFActivity
import java.io.*
import java.lang.Exception

class PdfDocumentAdapter(context: Context, filePath: String?) : PrintDocumentAdapter() {

    var context:Context
    var path:String

    init{
        this.context=context
        this.path=filePath!!
    }
    override fun onLayout(
        oldAttributes: PrintAttributes?,
        newAttributes: PrintAttributes?,
        cancellationSignal: CancellationSignal?,
        callback: LayoutResultCallback?,
        extras: Bundle?
    ) {

        if (cancellationSignal != null) {
            if(cancellationSignal.isCanceled){
                if (callback != null) {
                    callback.onLayoutCancelled()
                }
            }
            else{
                val builder = PrintDocumentInfo.Builder("file name")
                builder.setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                    .setPageCount(PrintDocumentInfo.PAGE_COUNT_UNKNOWN)
                    .build()
                if (callback != null) {
                    if (newAttributes != null) {
                        callback.onLayoutFinished(builder.build(),!newAttributes.equals(oldAttributes))
                    }

                }
            }

        }
    }

    override fun onWrite(
        pages: Array<out PageRange>?,
        destination: ParcelFileDescriptor?,
        cancellationSignal: CancellationSignal?,
        callback: WriteResultCallback?
    ) {

        var input:InputStream?=null
        var output:OutputStream?=null

        try {
            val file = File(path)
            input = FileInputStream(file)
            output = FileOutputStream(destination?.fileDescriptor)

            val buff = byteArrayOf()
            val size: Int = input.read(buff)
            if (cancellationSignal != null) {
                while (size >= 0 && !cancellationSignal.isCanceled) {
                    output.write(buff, 0, size)
                }
                if (cancellationSignal.isCanceled) {
                    if (callback != null) {
                        callback.onWriteCancelled()
                    }
                } else {
                    callback?.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
                }
            }
        }
        catch (e:Exception){
            if (callback != null) {
                callback.onWriteFailed(e.message)
            }
            e.message?.let { Log.d("PDF", it) }
            e.printStackTrace()
        }
        finally {
            try{
                if (input != null) {
                    input.close()
                }
                if (output != null) {
                    output.close()
                }
            }catch (e:IOException){
                e.message?.let { Log.e("PDF", it) }
            }
        }
    }

}
