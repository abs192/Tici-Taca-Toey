package com.abs192.ticitacatoey.views.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import android.widget.TextView
import com.abs192.ticitacatoey.R

class ToastDialog(context: Context, private val toastText: String) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setContentView(R.layout.layout_toast)
        findViewById<TextView>(R.id.toast_text)?.text = toastText
    }
}
