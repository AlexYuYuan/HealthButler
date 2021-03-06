package com.example.health_butler

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentResultListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.activity_sport.*
import kotlinx.android.synthetic.main.buttom_setdrinking_dialog.*
import kotlinx.android.synthetic.main.fragment_drink_buttom_left.*

class DrinkButtomLeftFragment() : Fragment() {

    private var targetDrinking = 0   // 目标饮水量
    private var currentDrinking = 0   // 目前饮水量


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_drink_buttom_left, container, false)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        targetDrinking = getWaterGoal()
        total_drinking.text = targetDrinking.toString() + " ml"

        var res = queryDrinkRecords(getDate())

        if (res == null) {
            current_drinking.text = currentDrinking.toString()
        }
        else {
            current_drinking.text = res.volume.toString()
            currentDrinking = res.volume
        }
        updateProgress()

        parentFragmentManager.setFragmentResultListener("changeDate", this, FragmentResultListener { requestKey, result ->
            //事件处理
            val date = result.getString("date")
            val drinkRecord = queryDrinkRecords(date!!.toInt())
            if(drinkRecord != null){
                currentDrinking = drinkRecord.volume
                current_drinking.text = currentDrinking.toString()
                updateProgress()
            }
            else {
                currentDrinking = 0
                current_drinking.text = currentDrinking.toString()
                updateProgress()
            }
            // 根据日期决定添加喝水按钮是否可见
            if (date.toInt() == getDate()) {
                add.isVisible = true
            }else {
                add.isVisible = false
            }
        })

        drinking_target.setOnClickListener {
            showDialog(0)
        }
        add.setOnClickListener {
            showDialog(1)
        }
    }


    private fun updateProgress() {
        var target : Double = targetDrinking.toDouble()
        var current : Double = currentDrinking.toDouble()
        var progr = Math.floor((current / target) * 100).toInt()
        drink_progress_bar.progress = progr
    }

    // 更新饮水的对话框
    fun showDialog(num : Int) {
        val btnsheet = layoutInflater.inflate(R.layout.buttom_setdrinking_dialog, null)
        val dialog = BottomSheetDialog(this.requireContext())
        dialog.setContentView(btnsheet)

        val idBtnDismiss : Button? = dialog.findViewById<Button>(R.id.idBtnDismiss)
        val setDrinking : EditText? = dialog.findViewById<EditText>(R.id.setDrinking)

        idBtnDismiss?.setOnClickListener {
            if (num == 0) {
                // 更新饮水目标
                targetDrinking = setDrinking?.text.toString().toInt()
                setWaterGoal(targetDrinking)   // 更新本地存储
                total_drinking.text = targetDrinking.toString() + " ml"
            }
            else if (num == 1) {
                // 添加饮水
                val tem = setDrinking?.text.toString().toInt()
                currentDrinking = currentDrinking + tem
                upDataDrinkRecord(tem)   // 更新数据库
                current_drinking.text = (currentDrinking).toString()
            }
            updateProgress()
            dialog.dismiss()
        }
        dialog.show()
    }
}