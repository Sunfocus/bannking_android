package com.bannking.app.utils

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.bannking.app.R
import com.bannking.app.ui.activity.UpgradeActivity


class DialogClass(private val context: Context) {
    private var progressDialog: ProgressDialog? = null
    private var dialogServer: Dialog = Dialog(context)

    init {
        progressDialog = ProgressDialog(context)
        progressDialog!!.setTitle(context.resources.getString(R.string.str_please_wait))
        progressDialog!!.setMessage(context.resources.getString(R.string.str_loading))
        progressDialog!!.setCanceledOnTouchOutside(false)
        progressDialog!!.setCancelable(false)
    }

    fun showLoadingDialog() {
        if (!progressDialog!!.isShowing)
            progressDialog!!.show()
    }

    fun hideLoadingDialog() {
        if (progressDialog!!.isShowing) progressDialog!!.cancel()
//        Handler().postDelayed({  }, 500)
    }


    fun showInformMotionDialog() {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_welcome)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val btnOk: Button = dialog.findViewById(R.id.btn_ok)
        btnOk.setOnClickListener {
//            Toast.makeText(context, "Okay", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        dialog.show()
    }

//    fun showPaymentSuccessfully() {
//        val dialog = Dialog(context)
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        dialog.setCancelable(false)
//        dialog.setContentView(R.layout.dialog_payment_successfully)
//        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        val btnReTransfer : Button = dialog.findViewById(R.id.btn_re_transfer)
//        val txtDone : TextView = dialog.findViewById(R.id.txt_done)
//        btnReTransfer.setOnClickListener { dialog.dismiss() }
//        txtDone.setOnClickListener { dialog.dismiss() }
//        dialog.show()
//    }

    fun showError(strMessage: String) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_error)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val txtOk: TextView = dialog.findViewById(R.id.txt_ok)
        val txtMessage: TextView = dialog.findViewById(R.id.txt_message)
        txtMessage.text = strMessage
        txtOk.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    fun showMaintenanceDialog() {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_maintenance)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val btnOk: Button = dialog.findViewById(R.id.btn_ok)
        btnOk.setOnClickListener {
            dialog.dismiss()
            (context as Activity).finishAffinity()
        }
        dialog.show()
    }

    fun showAccountCreateSuccessfullyDialog(strMessage: String, callbacks: (() -> Unit)? = null) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_account_create_successfully)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val btnOk: TextView = dialog.findViewById(R.id.txt_done)
        val txtSucessfulyMessage: TextView = dialog.findViewById(R.id.txt_sucessfuly_message)
        txtSucessfulyMessage.text = strMessage
        btnOk.setOnClickListener {
            dialog.dismiss()
            callbacks?.invoke()
        }
        dialog.show()
    }

    fun showSuccessfullyDialog(strMessage: String, callbacks: (() -> Unit)? = null) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_account_create_successfully)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val btnOk: TextView = dialog.findViewById(R.id.txt_done)
        val txtSucessfulyMessage: TextView = dialog.findViewById(R.id.txt_sucessfuly_message)
        txtSucessfulyMessage.text = strMessage
        btnOk.setOnClickListener {
            dialog.dismiss()
            callbacks?.invoke()
        }
        dialog.show()
    }

    fun showAccountNotSubscriptionDialog(strMessage: String) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_no_subsription)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val txtSkip: TextView = dialog.findViewById(R.id.txt_skip)
        val txtUpgrade: TextView = dialog.findViewById(R.id.txt_upgrade)
        val txtMessage: TextView = dialog.findViewById(R.id.txt_message)
        txtMessage.text = strMessage
        txtSkip.setOnClickListener {
            dialog.dismiss()
        }

        txtUpgrade.setOnClickListener {
            dialog.dismiss()
            context.startActivity(Intent(context, UpgradeActivity::class.java))
//            (context as Activity).finish()
        }
        dialog.show()
    }

    fun showServerErrorDialog() {
//        dialogServer.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogServer.setCancelable(false)
        dialogServer.setContentView(R.layout.dialog_server_error)
        dialogServer.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val txtOk: TextView = dialogServer.findViewById(R.id.txt_ok)
        txtOk.setOnClickListener {
            dialogServer.dismiss()
        }
        if (!dialogServer.isShowing) {
            dialogServer.show()
        }
    }


    fun editTextDialog(strTitle: String, strMessage: String, click: OnSubmitBtnClick) {
        val alertDialog = AlertDialog.Builder(context)
        alertDialog.setTitle(strTitle)
        alertDialog.setMessage(strMessage)
        val input = EditText(context)
        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        lp.setMargins(2, 1, 2, 1)
        input.layoutParams = lp
        alertDialog.setView(input)
        alertDialog.setIcon(null)
        alertDialog.setCancelable(false)

        alertDialog.setPositiveButton("Submit") { _, _ ->
            click.onClick(input.text.toString())
        }

        alertDialog.setNegativeButton("Dismiss") { dialog, _ ->
            dialog.dismiss()
        }
        alertDialog.show()
    }
}