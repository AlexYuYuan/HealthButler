package com.example.health_butler

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText


class NumberSelectDialog(context: Context, dialogListener: FoodSelectActivity.DialogListener):Dialog(context){

    lateinit var number: String
    val dialogListener = dialogListener


    init{
        setContentView(R.layout.number_select_dialog)
        window?.setGravity(Gravity.CENTER)
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        val notarizeButton = findViewById<Button>(R.id.notarizeButton)
        val editText = findViewById<EditText>(R.id.input)

        notarizeButton.setOnClickListener {
            number = editText.text.toString()
            dialogListener.refreshActivity(number)
            dismiss()
        }
    }

}