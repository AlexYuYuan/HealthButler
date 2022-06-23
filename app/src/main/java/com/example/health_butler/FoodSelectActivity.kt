package com.example.health_butler

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.health_butler.data.Food
import java.util.ArrayList

class FoodSelectActivity : Fragment(){
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        saveInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_weightrecord, container, false)

        class FoodAdapter(val foodData: List<Food>) : RecyclerView.Adapter<FoodAdapter.ViewHolder>() {

            inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
                val foodName = view.findViewById<TextView>(R.id.food_name)
                val calorie = view.findViewById<TextView>(R.id.calorie)
            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodAdapter.ViewHolder {
                return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.food_item, parent, false))
            }

            override fun onBindViewHolder(holder: FoodAdapter.ViewHolder, position: Int) {
                holder.foodName.setText(foodData.get(position).name)
                holder.calorie.setText(foodData.get(position).calorie)
            }

            override fun getItemCount(): Int {
                return foodData.size
            }

        }


        return view
    }
    interface DialogListener {
        fun refreshActivity(unit: String)
    }

    interface ItemClickListener {
        fun onItemClickListener(position: Int)
    }

}