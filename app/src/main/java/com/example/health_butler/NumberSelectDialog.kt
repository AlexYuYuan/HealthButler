package com.example.health_butler

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast


class NumberSelectDialog(context: Context, title: String, unit: String, dialogListener: FoodSelectActivity.DialogListener):Dialog(context){

    lateinit var number: String
    val dialogListener = dialogListener
    val title = title
    val unit = unit


    init{
        setContentView(R.layout.number_select_dialog)
        window?.setGravity(Gravity.CENTER)
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        val titleText = findViewById<TextView>(R.id.title)
        val notarizeButton = findViewById<Button>(R.id.notarizeButton)
        val editText = findViewById<EditText>(R.id.input)

        titleText.setText(title+ "(" + unit + ")")

        notarizeButton.setOnClickListener {
            number = editText.text.toString().trim()
            if(number.toString().equals("")){
                Toast.makeText(context, "请输入数量", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            else{
                dialogListener.refreshActivity(number)
                dismiss()
            }

        }
    }

}