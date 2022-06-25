package com.example.health_butler

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import kotlinx.android.synthetic.main.number_select_dialog.*

class AddFoodDialog(context: Context): Dialog(context) {

    init{
        setContentView(R.layout.add_food_dialog)
        window?.setGravity(Gravity.CENTER)
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        val notarizeButton = findViewById<Button>(R.id.notarizeButton)
        val nameEditText = findViewById<EditText>(R.id.nameInput)
        val unitEditText = findViewById<EditText>(R.id.unitInput)
        val calorieEditText = findViewById<EditText>(R.id.calorieInput)
        val carbohydrateEditText = findViewById<EditText>(R.id.carbohydrateInput)
        val proteinEditText = findViewById<EditText>(R.id.proteinInput)
        val fatEditText = findViewById<EditText>(R.id.fatInput)

        notarizeButton.setOnClickListener {

            //读取输入框内容
            val inputList = arrayOf(
                nameEditText.text.trim(),
                unitEditText.text.trim(),
                calorieEditText.text.trim(),
                carbohydrateEditText.text.trim(),
                proteinEditText.text.trim(),
                fatEditText.text.trim())

            val sign = arrayOf("名字","单位","热量","碳水","蛋白质","脂肪")
            var i = 0
            //非空判断
            for (input in inputList){
                if(input.toString().equals("")){
                    Toast.makeText(context, sign[i] +"不能为空",Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                i++
            }

            //存入数据库
            if(i > 5){
                insertFood(Food(inputList[0].toString(),
                    inputList[1].toString(),
                    inputList[2].toString().toInt(),
                    inputList[3].toString().toInt(),
                    inputList[4].toString().toInt(),
                    inputList[5].toString().toInt(),
                    FOODTYPE.COMMON))
                dismiss()
            }
        }
    }
}