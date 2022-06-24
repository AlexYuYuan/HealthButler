package com.example.health_butler

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.buttom_setdrinking_dialog.*
import kotlinx.android.synthetic.main.fragment_drink_buttom_left.*


class DrinkButtomLeftFragment : Fragment() {

    var targetDrinking = 0   //目标饮水量
    var currentDrinking = 0   //目前饮水量

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

        drinking_target.setOnClickListener {
            showDialog(0)
        }

        add.setOnClickListener {
            showDialog(1)
        }
    }

    fun showDialog(num : Int) {
        val btnsheet = layoutInflater.inflate(R.layout.buttom_setdrinking_dialog, null)
        val dialog = BottomSheetDialog(this.requireContext())
        dialog.setContentView(btnsheet)

        val idBtnDismiss : Button? = dialog.findViewById<Button>(R.id.idBtnDismiss)
        val setDrinking : EditText? = dialog.findViewById<EditText>(R.id.setDrinking)

        idBtnDismiss?.setOnClickListener {
            if (num == 0) {
                targetDrinking = setDrinking?.text.toString().toInt()
                total_drinking.text = targetDrinking.toString() + " ml"
            }
            else if (num == 1) {
                currentDrinking =  setDrinking?.text.toString().toInt()
                current_drinking.text = currentDrinking.toString()
            }
//            targetDrinking = setDrinking?.text.toString().toInt()
//            total_drinking.text = targetDrinking.toString() + " ml"
            dialog.dismiss()
        }
        dialog.show()
    }
}