package com.example.health_butler

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.health_butler.data.Food
import java.util.LinkedList
import com.example.health_butler.data.DataManagement
import com.example.health_butler.data.DietRecord
import com.example.health_butler.data.TYPE

abstract class FoodSelectActivity : AppCompatActivity(){

    lateinit private var number: String
    private val dataBase = DataManagement(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_select)
//
//        val context = this
//        val foodData: LinkedList<Food> = dataBase?.queryAllFoods()!!
//        val foodAdapter = FoodAdapter(foodData)
//        val  recyclerView = findViewById<RecyclerView>(R.id.foodList)
//
//        foodAdapter.setOnItemClickListener(object : OnItemClickListener {
//            override fun onItemClick(position: Int) {
//                var num: String = ""
//                NumberSelectDialog(context, object : DialogListener {
//                    override fun refreshActivity(number: String) {
//                        num = number
//                    }
//                }).show()
//                number = num
//                dataBase.insertDiet(DietRecord(dataBase.getData(),TYPE.LUNCH, foodData.get(position).name, number.toDouble()))
//            }
//
//        })
//
//        if (recyclerView != null) {
//            recyclerView.adapter = foodAdapter
//        }
//        if (recyclerView != null) {
//            recyclerView.layoutManager = LinearLayoutManager(this)
//        }
    }


    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    interface DialogListener {
        fun refreshActivity(number: String)
    }

//    class FoodAdapter(val foodData: List<Food>) : RecyclerView.Adapter<FoodAdapter.ViewHolder>(), View.OnClickListener {
//
//        lateinit private var onItemClickListener: OnItemClickListener
//        lateinit private var rv: RecyclerView
//
//        fun setOnItemClickListener(onItemClickListener: OnItemClickListener){
//            this.onItemClickListener = onItemClickListener
//        }
//
//        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
//            val foodName = view.findViewById<TextView>(R.id.food_name)
//            val calorie = view.findViewById<TextView>(R.id.calorie)
//        }
//
//        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodAdapter.ViewHolder {
//            val view = LayoutInflater.from(parent.context).inflate(R.layout.food_item, parent, false)
//            view.setOnClickListener(this)
//            rv = parent as RecyclerView
//            return ViewHolder(view)
//        }
//
//        override fun onBindViewHolder(holder: FoodAdapter.ViewHolder, position: Int) {
//            holder.foodName.setText(foodData.get(position).name)
//            holder.calorie.setText(foodData.get(position).calorie)
//        }
//
//        override fun getItemCount(): Int {
//            return foodData.size
//        }
//
//        override fun onClick(view: View?) {
//            val position: Int = rv.getChildAdapterPosition(view!!)
//            if (onItemClickListener!=null){
//                onItemClickListener.onItemClick(position);
//            }
//        }
//    }
}