package com.example.health_butler


import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentResultListener
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.android.synthetic.main.activity_ingestion.*
import kotlinx.android.synthetic.main.activity_ingestion.iv_calendar_next
import kotlinx.android.synthetic.main.activity_ingestion.iv_calendar_previous
import kotlinx.android.synthetic.main.activity_ingestion.tv_date
import kotlinx.android.synthetic.main.activity_sport.*
import kotlinx.android.synthetic.main.fragment_drink_buttom_left.*
import kotlinx.android.synthetic.main.fragment_drink_top.*
import kotlinx.android.synthetic.main.fragment_ingestion.*
import kotlinx.android.synthetic.main.fragment_ingestion.go_nutrient
import kotlinx.android.synthetic.main.fragment_nutrient.*
import java.util.*
import kotlin.math.log


class IngestionActivity : Fragment(){
    private var progr = 0

    var i : Int = 0
    var dateI : Int = getDate() + i * 86400
    var dateS : String = getDateFormat(dateI)
    var flag : Int = 0

    private var foodList : LinkedList<DietShow> = queryDiet(getDate())
    var adapter : MyFoodAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        saveInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_ingestion, container, false)
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()

        initFragmentView()

        foodList = queryDiet(getDate())
        adapter = MyFoodAdapter(this.requireContext(), R.layout.menu_item, foodList)
        showFoodData.adapter = adapter

        tv_date.text = "今天"

        // 日期选择
        iv_calendar_next.setOnClickListener {
            if (i == 0) {
                dateS = "今天"
                addRecipes.isVisible = true
            }
            else{
                i++
                if(i == 0) {
                    dateS = "今天"
                    dateI = getDate() + i * 86400
                    addRecipes.isVisible = true
                }
                else {
                    dateI = getDate() + i * 86400
                    dateS = getDateFormat(dateI)
                    addRecipes.isVisible = false
                }
            }
            foodList = queryDiet(dateI)
            adapter = MyFoodAdapter(this.requireContext(), R.layout.menu_item, foodList)
            showFoodData.adapter = adapter
            tv_date.text = dateS
            childFragmentManager.setFragmentResult("changeIngestion", Bundle().apply {
                putString("date", dateI.toString())
            })
        }
        iv_calendar_previous.setOnClickListener {
            i--
            dateI = getDate() + i * 86400
            dateS = getDateFormat(dateI)
            if (i == 0) {
                dateS = "今天"
                addRecipes.isVisible = true
            }
            if (i != 0) {
                addRecipes.isVisible = false
            }
            foodList = queryDiet(dateI)
            adapter = MyFoodAdapter(this.requireContext(), R.layout.menu_item, foodList)
            showFoodData.adapter = adapter
            tv_date.text = dateS
            childFragmentManager.setFragmentResult("changeIngestion", Bundle().apply {
                putString("date", dateI.toString())
            })
        }

        childFragmentManager.setFragmentResultListener("getDate", this, FragmentResultListener { requestKey, result ->
            //事件处理
            childFragmentManager.setFragmentResult("changeIngestion", Bundle().apply {
                putString("date", dateI.toString())
            })
        })

        addRecipes.setOnClickListener {
            val intent = Intent(activity, FoodSelectActivity::class.java)
            startActivity(intent)
        }
    }

    // 切换fragment板块
    fun initFragmentView() {
        val transaction = getChildFragmentManager().beginTransaction()
        transaction.replace(R.id.statistics_layout, TotalIngestionFragment())
        transaction.commit()
    }

    // 自定义适配器
    inner class MyFoodAdapter(val activity: Context, val resourceID: Int, data: List<DietShow>) : ArrayAdapter<DietShow>(activity, resourceID, data) {

        inner class ViewHolder(val foodName: TextView, val quantity : TextView, val unit: TextView)

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

            val view: View
            val viewHolder: ViewHolder

            if (convertView == null) {
                view = LayoutInflater.from(context).inflate(resourceID, parent, false)
                val foodName: TextView = view.findViewById(R.id.foodName)
                val quantity : TextView = view.findViewById(R.id.quantity)
                val unit: TextView = view.findViewById(R.id.unit)
                viewHolder = ViewHolder(foodName, quantity, unit)
                view.tag = viewHolder
            } else {
                view = convertView
                viewHolder = view.tag as ViewHolder
            }

            val foodsList = getItem(position)   // 获取当前项

            if (foodsList != null) {
                var foodName = foodsList.name
                var quantity = foodsList.num
                var unit = foodsList.unit
                viewHolder.foodName.text = foodName   // 设置控件
                viewHolder.quantity.text = quantity.toString()
                viewHolder.unit.text = unit
            }

            return view
        }
    }
}