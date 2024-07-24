package com.bannking.app.utils

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import com.bannking.app.R

class GenericTextWatcher(private val view: View, private val editText: Array<EditText>) :
    TextWatcher {
    @SuppressLint("NonConstantResourceId")
    override fun afterTextChanged(editable: Editable) {
        val text = editable.toString()
        when (view.id) {
//            R.id.otp_edit_box1 -> if (text.length == 1) editText[1].requestFocus()
//            R.id.otp_edit_box2 -> if (text.length == 1) editText[2].requestFocus() else if (text.length == 0) editText[0].requestFocus()
//            R.id.otp_edit_box3 -> if (text.length == 1) editText[3].requestFocus() else if (text.length == 0) editText[1].requestFocus()
//            R.id.otp_edit_box4 -> if (text.length == 0) editText[2].requestFocus()

            R.id.otp_edit_box1 -> if (text.length == 1) editText[1].requestFocus()
            R.id.otp_edit_box2 -> if (text.length == 1) editText[2].requestFocus() else if (text.isEmpty()) editText[0].requestFocus()
            R.id.otp_edit_box3 -> if (text.length == 1) editText[3].requestFocus() else if (text.isEmpty()) editText[1].requestFocus()
            R.id.otp_edit_box4 -> if (text.length == 1) editText[4].requestFocus() else if (text.isEmpty()) editText[2].requestFocus()
            R.id.otp_edit_box5 -> if (text.length == 1) editText[5].requestFocus() else if (text.isEmpty()) editText[3].requestFocus()
//            R.id.otp_edit_box6 -> if (text.length == 1) editText[6].requestFocus() else if (text.length == 0) editText[4].requestFocus()
            R.id.otp_edit_box6 -> if (text.isEmpty()) editText[4].requestFocus()
        }
    }

    override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {}
    override fun onTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {}
}