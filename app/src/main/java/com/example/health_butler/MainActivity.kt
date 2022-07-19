package com.example.health_butler

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.health_butler.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bottomNav.menu.getItem(0).isCheckable = true
        setFragment(IngestionActivity())

        bottomNav.setOnNavigationItemSelectedListener {menu ->

            when(menu.itemId){

                R.id.ingestion -> {
                    setFragment(IngestionActivity())
                    true
                }

                R.id.sport -> {
                    setFragment(SportActivity())
                    true
                }

                R.id.drink -> {
                    setFragment(DrinkActivity())
                    true
                }

//                R.id.weightRecord -> {
//                    setFragment(WeightRecordActivity())
//                    true
//                }

                else -> false
            }
        }

    }

    fun setFragment(fr : Fragment){
        val frag = supportFragmentManager.beginTransaction()
        frag.replace(R.id.fragmentContainer,fr)
        frag.commit()
    }

}