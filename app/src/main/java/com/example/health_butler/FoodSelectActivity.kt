package com.example.health_butler

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView

class FoodSelectActivity : Fragment(){
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        saveInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_weightrecord, container, false)

        class FoodAdapter : RecyclerView.Adapter<FoodAdapter.ViewHolder>() {

            inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
                
            }

            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): FoodAdapter.ViewHolder {
                TODO("Not yet implemented")
            }

            override fun onBindViewHolder(holder: FoodAdapter.ViewHolder, position: Int) {
                TODO("Not yet implemented")
            }

            override fun getItemCount(): Int {
                TODO("Not yet implemented")
            }
        }

        return view
    }
}