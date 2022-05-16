package com.example.health_butler

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.health_butler.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
//    private var progr = 0
//    private lateinit var binding: ActivityMainBinding
//    val params1 = arrayOf("one", "two", "three")
//    var i : Int = params1.size - 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//        binding.tvDateMonth.text = params1[i]
//
//        binding.ivCalendarNext.setOnClickListener {
//            if (i < params1.size - 1) {
//                i++
//            }
//            binding.tvDateMonth.text = params1[i]
//        }
//        binding.ivCalendarPrevious.setOnClickListener {
//            if (i > 0) {
//                i--
//            }
//            binding.tvDateMonth.text = params1[i]
//        }
//
//        progr = 70
//        updateProgressBar()
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

                R.id.weightRecord -> {
                    setFragment(WeightRecordActivity())
                    true
                }

                else -> false
            }
        }

    }

    fun setFragment(fr : Fragment){
        val frag = supportFragmentManager.beginTransaction()
        frag.replace(R.id.fragmentContainer,fr)
        frag.commit()
    }

//    private fun updateProgressBar() {
//        var progress_bar : ProgressBar = findViewById(R.id.progress_bar)
//        progress_bar.progress = progr
//
//        var text_view_progress : TextView = findViewById(R.id.text_view_progress)
//        text_view_progress.text = "$progr%"
//    }
}