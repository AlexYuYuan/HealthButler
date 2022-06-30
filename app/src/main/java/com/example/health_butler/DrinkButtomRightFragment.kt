package com.example.health_butler

import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_drink_buttom_right.*
import java.util.*
import kotlin.collections.ArrayList


class DrinkButtomRightFragment : Fragment() {

    private val times = ArrayList<Time>()
    private val clockList : LinkedList<AlarmClock> = queryClock()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_drink_buttom_right, container, false)
        return view
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        val adapter = MyListAdapter(this.requireContext(), R.layout.time_item, clockList)   // listview适配器
        showData.adapter = adapter


//        showData.setOnItemClickListener { adapterView, view, i, l ->
//            val alertDialog = AlertDialog.Builder(this.requireContext()).create()
//            alertDialog.setMessage("您确定删除此条信息吗？")
//
//            alertDialog.setButton(
//                DialogInterface.BUTTON_NEGATIVE, "否"
//            ) { dialog, which ->
//            }
//
//            //将对应位置的数据删除
//            alertDialog.setButton(
//                DialogInterface.BUTTON_POSITIVE, "是"
//            ) { dialog, which ->
//                                 // 删除数据库条目
//                allData.removeAt(i)   // 删除缓存数据
//                adapter.notifyDataSetChanged()   // 刷新视图
//                true
//            }
//            alertDialog.show()
//        }


        // 添加闹钟，弹出对话框
        add.setOnClickListener {
            val _timePickerDialog : TimePickerDialog
            val hourOfDay = 0
            val minute = 0
            val is24HourView = true
            var hourS = ""
            var minuteS = ""

            _timePickerDialog = TimePickerDialog(activity, android.R.style.Theme_Holo_Light_Dialog,
                OnTimeSetListener { timePicker, i, i1 -> //*Return values
                    if(i < 10) {
                        hourS = "0" + "$i"
                    }
                    else {
                        hourS = "$i"
                    }
                    if(i1 < 10) {
                        minuteS = "0" + "$i1"
                    }
                    else {
                        minuteS = "$i1"
                    }
                    insertClock(AlarmClock("$hourS:$minuteS", false))// 更新数据库
                    clockList.add(AlarmClock("$hourS:$minuteS", false))   // 更新时间列表
                    adapter.notifyDataSetChanged()   // 更新列表
                }, hourOfDay, minute, is24HourView
            )
            _timePickerDialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            _timePickerDialog.setTitle("Select a Time")
            _timePickerDialog.show()
        }

    }

    // 继承adapter类，自定义列表
    private class MyListAdapter(val activity: Context, val resourceID: Int, data: List<AlarmClock>) : ArrayAdapter<AlarmClock>(activity, resourceID, data) {

        inner class ViewHolder(val timeList: TextView, val switch1: Switch)

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

            val view: View
            val viewHolder: ViewHolder

            if (convertView == null) {
                view = LayoutInflater.from(context).inflate(resourceID, parent, false)
                val timeList: TextView = view.findViewById(R.id.timeList)
                val switch1: Switch = view.findViewById(R.id.switch1)
                viewHolder = ViewHolder(timeList, switch1)
                view.tag = viewHolder
            } else {
                view = convertView
                viewHolder = view.tag as ViewHolder
            }

            val timesList = getItem(position)   // 获取当前项

            if (timesList != null) {
                var time = timesList.time
                viewHolder.timeList.text = time   // 设置控件
                viewHolder.switch1.isChecked = timesList.state   // 设置闹钟状态
                viewHolder.switch1.setOnCheckedChangeListener { buttonView, isChecked ->
                    // 修改闹钟状态
                    if(isChecked) {
                        Toast.makeText(context,"The alarm clock turns on at $time",Toast.LENGTH_LONG).show()
                        upDataClock(AlarmClock(time, true))// 更新数据库
                    }
                    else {
                        Toast.makeText(context,"The alarm clock turns off at $time",Toast.LENGTH_LONG).show()
                        upDataClock(AlarmClock(time, false))// 更新数据库
                    }
                }
            }


//            val showData : ListView = view.findViewById(R.id.showData)
//
//            showData.setOnItemClickListener { adapterView, view, i, l ->
////                times.removeAt(i)   // 删除缓存数据
////                adapter.notifyDataSetChanged()   // 刷新视图
////                this.notifyDataSetChanged()
//                Log.v("ccc","ddd")
//            }

            return view
        }
    }

}