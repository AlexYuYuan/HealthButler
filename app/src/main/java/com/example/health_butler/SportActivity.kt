package com.example.health_butler

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_sport.*
import java.util.*

class SportActivity : Fragment() {

    var i : Int = 0
    var dateI : Int = getDate() + i * 86400
    var dateS : String = getDateFormat(dateI)

    private var sportList : LinkedList<SportShow> = querySport()
    var adapter : MyListAdapter? = null


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

        adapter = MyListAdapter(this.requireContext(), R.layout.sport_item, sportList)// listview适配器
        showSportData.adapter = adapter

        tv_date.text = "今天"

        // 日期选择
        iv_calendar_next.setOnClickListener {
            i++
            dateI = getDate() + i * 86400
            dateS = getDateFormat(dateI)
            sportList.clear()
            if (i == 0) {
                dateS = "今天"
                sportList = querySport()
                addSport.isVisible = true
            }
            if (i != 0) {
                sportList = querySportRecordByDate(dateI)
                addSport.isVisible = false
            }
            updateProgress()
            adapter = MyListAdapter(this.requireContext(), R.layout.sport_item, sportList)  // listview适配器
            showSportData.adapter = adapter
            tv_date.text = dateS
        }
        iv_calendar_previous.setOnClickListener {
            i--
            dateI = getDate() + i * 86400
            dateS = getDateFormat(dateI)
            sportList.clear()
            if (i == 0) {
                dateS = "今天"
                sportList = querySport()
                addSport.isVisible = true
            }
            if (i != 0) {
                sportList = querySportRecordByDate(dateI)
                addSport.isVisible = false
            }
            updateProgress()
            adapter = MyListAdapter(this.requireContext(), R.layout.sport_item, sportList)  // listview适配器
            showSportData.adapter = adapter
            tv_date.text = dateS
        }

        updateProgress()


        addSport.setOnClickListener {
            showDialog()  // 显示对话框
        }

    }

    // 更新进度条
    private fun updateProgress() {
        var target : Int = 0
        var current : Int = 0
        for(i in 0 until sportList.size) {
            target += sportList.get(i).time
            if (sportList.get(i).state) {
                current += sportList.get(i).time
            }
        }
        completed.text = current.toString()
        total_sportTime.text = target.toString() + "分钟"
        var progr = Math.floor((current.toDouble() / target.toDouble()) * 100).toInt()
        sport_progress_bar.progress = progr
    }

    // 添加运动项目对话框
    private fun showDialog() {
        val sheet = layoutInflater.inflate(R.layout.setsport_dialog, null)
        val dialog = Dialog(this.requireContext())
        val window : Window? = dialog.getWindow()
        dialog.setContentView(sheet)

        window?.setGravity(Gravity.CENTER)
        window?.setLayout(900, ViewGroup.LayoutParams.WRAP_CONTENT)   // 设置对话框大小

        val SportName : EditText = dialog.findViewById<EditText>(R.id.SportName)
        val SportTime : EditText = dialog.findViewById<EditText>(R.id.SportTime)
        val add : Button = dialog.findViewById<Button>(R.id.add)

        // 添加按钮监听
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

                var flag : Int = insertSport(Sport(name, time))  //更新数据库
                // 是否成功更新
                if (flag == 1) {
                    // 更新成功
                    Toast.makeText(context, "Successfully added", Toast.LENGTH_LONG).show()
                    sportList.add(SportShow(name, time, isComplete))
                    adapter?.notifyDataSetChanged()
                    updateProgress()   // 更新进度条
                }
                else if (flag == 0) {
                    // 更新失败
                    Toast.makeText(context, "This sport already exists", Toast.LENGTH_LONG).show()
                }
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    // 适配器
     inner class MyListAdapter(val activity: Context, val resourceID: Int, data: List<SportShow>) : ArrayAdapter<SportShow>(activity, resourceID, data) {

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
                var sportName = sportsList.name
                var time = sportsList.time
                var isComplete = sportsList.state
                viewHolder.sportName.text = sportName   // 设置控件
                viewHolder.sportTime.text = time.toString() + "分钟"
                viewHolder.isComplete.isChecked = isComplete
                viewHolder.isComplete.setOnCheckedChangeListener { buttonView, isChecked ->
                    // 修改运动状态
                    if(isChecked) {
                        Toast.makeText(context,"turns on at $sportName", Toast.LENGTH_LONG).show()
                        upSportData(sportName, true)// 更新数据库
                        sportList.get(position).state = true
                        updateProgress()
                    }
                    else {
                        Toast.makeText(context,"turns off at $sportName", Toast.LENGTH_LONG).show()
                        upSportData(sportName, false)// 更新数据库
                        sportList.get(position).state = false
                        updateProgress()
                    }
                }
            }

            return view
        }
    }
}