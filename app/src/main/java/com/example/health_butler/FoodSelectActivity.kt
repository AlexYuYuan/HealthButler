package com.example.health_butler

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.LinkedList

class FoodSelectActivity : AppCompatActivity(){

    lateinit private var type: TYPE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_select)

        val context = this
        var foodData: LinkedList<Food> = queryAllFoods()
        val foodAdapter = FoodAdapter(foodData)
        val recyclerView = findViewById<RecyclerView>(R.id.foodList)
        val addButton = findViewById<Button>(R.id.addFood)
        val allFood = findViewById<TextView>(R.id.all)
        val commonFood = findViewById<TextView>(R.id.common)
        val userDefinedFood = findViewById<TextView>(R.id.userDefined)

        foodAdapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(position: Int) {
                NumberSelectDialog(context, foodData.get(position).name, foodData.get(position).unit, object : DialogListener {
                    override fun refreshActivity(number: String) {
                        insertDiet(DietRecord(getDate(),TYPE.LUNCH, foodData.get(position).name, number.toDouble()))
                    }
                }).show()

            }

        })

        addButton.setOnClickListener {
            AddFoodDialog(context).show()
        }

        allFood.setOnClickListener {
            foodData = queryAllFoods()
            foodAdapter.notifyDataSetChanged()
        }

        commonFood.setOnClickListener {
            foodData = queryFoodsByType(FOODTYPE.COMMON)
            foodAdapter.notifyDataSetChanged()
        }

        userDefinedFood.setOnClickListener {
            foodData = queryFoodsByType(FOODTYPE.USERDEFINED)
            foodAdapter.notifyDataSetChanged()
        }

        if (recyclerView != null) {
            recyclerView.adapter = foodAdapter
        }
        if (recyclerView != null) {
            recyclerView.layoutManager = LinearLayoutManager(this)
        }
    }


    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    interface DialogListener {
        fun refreshActivity(number: String)
    }

    class FoodAdapter(val foodData: List<Food>) : RecyclerView.Adapter<FoodAdapter.ViewHolder>(), View.OnClickListener {

        lateinit private var onItemClickListener: OnItemClickListener
        lateinit private var rv: RecyclerView

        fun setOnItemClickListener(onItemClickListener: OnItemClickListener){
            this.onItemClickListener = onItemClickListener
        }

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
            val foodName = view.findViewById<TextView>(R.id.food_name)
            val calorie = view.findViewById<TextView>(R.id.calorie)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodAdapter.ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.food_item, null, false)
            view.setOnClickListener(this)
            rv = parent as RecyclerView
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: FoodAdapter.ViewHolder, position: Int) {
            holder.foodName.setText(foodData.get(position).name)
            holder.calorie.setText(foodData.get(position).calorie.toString())
            val name = holder.foodName.text
            val ca = holder.calorie.text
        }

        override fun getItemCount(): Int {
            return foodData.size
        }

        override fun onClick(view: View?) {
            val position: Int = rv.getChildAdapterPosition(view!!)
            if (onItemClickListener!=null){
                onItemClickListener.onItemClick(position);
            }
        }
    }
}