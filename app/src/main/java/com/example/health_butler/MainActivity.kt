package com.example.health_butler

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import com.example.health_butler.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var progr = 0
    private lateinit var binding: ActivityMainBinding
    val params1 = arrayOf("one", "two", "three")
    var i : Int = params1.size - 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.tvDateMonth.text = params1[i]

        binding.ivCalendarNext.setOnClickListener {
            if (i < params1.size - 1) {
                i++
            }
            binding.tvDateMonth.text = params1[i]
        }
        binding.ivCalendarPrevious.setOnClickListener {
            if (i > 0) {
                i--
            }
            binding.tvDateMonth.text = params1[i]
        }

        progr = 70
        updateProgressBar()

    }

    private fun updateProgressBar() {
        var progress_bar : ProgressBar = findViewById(R.id.progress_bar)
        progress_bar.progress = progr

        var text_view_progress : TextView = findViewById(R.id.text_view_progress)
        text_view_progress.text = "$progr%"
    }
}