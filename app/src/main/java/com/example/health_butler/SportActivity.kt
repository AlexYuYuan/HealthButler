package com.example.health_butler

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.activity_sport.*

class SportActivity : Fragment() {
    private var progr = 0
    var compeleted_sportT = 30
    var total_sportT = 70
    val params1 = arrayOf("one", "two", "three")
    var i : Int = params1.size - 1

    private val sports = ArrayList<Sports>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        saveInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_sport, container, false)
        return view
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        tv_date.text = params1[i]

        iv_calendar_next.setOnClickListener {
            if (i < params1.size - 1) {
                i++
            }
            tv_date.text = params1[i]
        }
        iv_calendar_previous.setOnClickListener {
            if (i > 0) {
                i--
            }
            tv_date.text = params1[i]
        }

        completed.text = "30"
        total_sportTime.text = "70"
        progr = 60
        updateProgressBar()

        initSports()

        val adapter = MyListAdapter(this.requireContext(), R.layout.sport_item, sports)  // listview适配器
        showSportData.adapter = adapter

        addSport.setOnClickListener {
            showDialog()  // 显示对话框
        }

    }

    private fun updateProgressBar() {
        sport_progress_bar.progress = progr
    }

    // 测试例子
    private fun initSports() {
        sports.add(Sports("跑步", 30, false))
        sports.add(Sports("跳绳", 20, true))
    }

    private fun showDialog() {
        val sheet = layoutInflater.inflate(R.layout.setsport_dialog, null)
        val dialog = Dialog(this.requireContext())
        val window : Window? = dialog.getWindow()
        dialog.setContentView(sheet)

        window?.setGravity(Gravity.CENTER)
        window?.setLayout(900, ViewGroup.LayoutParams.WRAP_CONTENT)

        val SportName : EditText = dialog.findViewById<EditText>(R.id.SportName)
        val SportTime : EditText = dialog.findViewById<EditText>(R.id.SportTime)
        val add : Button = dialog.findViewById<Button>(R.id.add)

        add.setOnClickListener {
            var name : String = ""
            var time : Int = 0
            var isComplete = false

            if (SportName.text.toString().equals("") || SportTime.text.toString().equals("")) {
                Toast.makeText(context, "Please enter information", Toast.LENGTH_LONG).show()
            }
            else {
                name = SportName.text.toString()
                time = SportTime.text.toString().toInt()
                isComplete = false

                //更新数据库
                sports.add(Sports(name, time, isComplete))
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    class MyListAdapter(val activity: Context, val resourceID: Int, data: List<Sports>) : ArrayAdapter<Sports>(activity, resourceID, data) {

        inner class ViewHolder(val sportName: TextView, val sportTime : TextView, val isComplete: CheckBox)

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

            val view: View
            val viewHolder: ViewHolder

            if (convertView == null) {
                view = LayoutInflater.from(context).inflate(resourceID, parent, false)
                val sportName: TextView = view.findViewById(R.id.sportName)
                val sportTime : TextView = view.findViewById(R.id.sportTime)
                val isComplete: CheckBox = view.findViewById(R.id.isComplete)
                viewHolder = ViewHolder(sportName, sportTime, isComplete)
                view.tag = viewHolder
            } else {
                view = convertView
                viewHolder = view.tag as ViewHolder
            }

            val sportsList = getItem(position)   // 获取当前项

            if (sportsList != null) {
                var sportName = sportsList.sportName
                var time = sportsList.time
                var isComplete = sportsList.isComplete
                viewHolder.sportName.text = sportName   // 设置控件
                viewHolder.sportTime.text = time.toString() + "分钟"
                viewHolder.isComplete.isChecked = isComplete
                viewHolder.isComplete.setOnCheckedChangeListener { buttonView, isChecked ->
                    // 修改运动状态
                    if(isChecked) {
                        Toast.makeText(context,"turns on at $sportName", Toast.LENGTH_LONG).show()
                        // 更新数据库
                    }
                    else {
                        Toast.makeText(context,"turns off at $sportName", Toast.LENGTH_LONG).show()
                        // 更新数据库
                    }
                }
            }

            return view
        }
    }
}