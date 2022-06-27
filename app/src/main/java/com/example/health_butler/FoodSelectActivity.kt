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
        val foodData: LinkedList<Food> = queryAllFoods()
        val foodAdapter = FoodAdapter(foodData)
        val recyclerView = findViewById<RecyclerView>(R.id.foodList)
        val addButton = findViewById<Button>(R.id.addFood)

        foodAdapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(position: Int) {
                val date = getDateFormat(getDate())
                NumberSelectDialog(context, object : DialogListener {
                    override fun refreshActivity(number: String) {
                        insertDiet(DietRecord(getDate(),TYPE.LUNCH, foodData.get(position).name, number.toDouble()))
                    }
                }).show()


            }

        })

        addButton.setOnClickListener {
            AddFoodDialog(context).show()
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
            val view = LayoutInflater.from(parent.context).inflate(R.layout.food_item, parent, false)
            view.setOnClickListener(this)
            rv = parent as RecyclerView
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: FoodAdapter.ViewHolder, position: Int) {
            holder.foodName.setText(foodData.get(position).name)
            holder.calorie.setText(foodData.get(position).calorie.toString())
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