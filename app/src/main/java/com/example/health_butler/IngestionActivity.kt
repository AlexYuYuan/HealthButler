package com.example.health_butler


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_ingestion.*
import kotlinx.android.synthetic.main.activity_ingestion.iv_calendar_next
import kotlinx.android.synthetic.main.activity_ingestion.iv_calendar_previous
import kotlinx.android.synthetic.main.activity_ingestion.tv_date



class IngestionActivity : Fragment(){
    private var progr = 0

    var i : Int = 0
    var dateI : Int = getDate() + i * 86400
    var dateS : String = getDateFormat(dateI)
    var flag : Int = 0

    

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        saveInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_ingestion, container, false)
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initFragmentView()

        tv_date.text = "今天"

        iv_calendar_next.setOnClickListener {
            if (i == 0) {
                dateS = "今天"
            }
            else{
                i++
                if(i == 0) {
                    dateS = "今天"
                    dateI = getDate() + i * 86400
                }
                else {
                    dateI = getDate() + i * 86400
                    dateS = getDateFormat(dateI)
                }
            }
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
            }
            tv_date.text = dateS
            childFragmentManager.setFragmentResult("changeIngestion", Bundle().apply {
                putString("date", dateI.toString())
            })
        }



        addRecipes.setOnClickListener {
            val intent = Intent(activity, FoodSelectActivity::class.java)
            startActivity(intent)
        }


//        progr = 70


//        resetFragment()
//        updateProgressBar()
//        updateChartView()

//        go_nutrient.setOnClickListener{
//            val transaction = getChildFragmentManager().beginTransaction()
//            transaction.replace(R.id.statistics_layout, NutrientFragment())
//            transaction.commit()
//        }
//        go_ingestion.setOnClickListener{
//            val transaction = getChildFragmentManager().beginTransaction()
//            transaction.replace(R.id.statistics_layout, TotalIngestionFragment())
//            transaction.commit()
//            Log.v("aaa", "sucess")
//        }


    }
    fun initFragmentView() {
        val transaction = getChildFragmentManager().beginTransaction()
        transaction.replace(R.id.statistics_layout, TotalIngestionFragment())
        transaction.commit()
    }

//    fun resetFlag(num : Int){
//        flag = num
//        resetFragment()
//    }

//    fun resetFragment() {
//        when (flag)
//        {
//            0 -> {
//                val transaction = getChildFragmentManager().beginTransaction()
//                transaction.replace(R.id.statistics_layout, TotalIngestionFragment())
//                transaction.commit()
//            }
//
//            1 -> {
//                val transaction = getChildFragmentManager().beginTransaction()
//                transaction.replace(R.id.statistics_layout, NutrientFragment())
//                transaction.commit()
//            }
//        }
//    }

//    private fun updateProgressBar() {
//        ingestion_progress_bar.progress = progr
//        text_view_progress.text = "$progr%"
//    }

//    private fun updateChartView() {
//        val xvalues = ArrayList<String >()
//        xvalues.add("Coal")
//        xvalues.add("Petrolium")
//        xvalues.add("Natural Gas")
//
//        val piechartentry = ArrayList<Entry>()
//        piechartentry.add( Entry(23.5f, 0 ))
//        piechartentry.add( Entry(45.5f, 1 ))
//        piechartentry.add( Entry(68.5f, 2 ))
//
//        val piedataset = PieDataSet(piechartentry,"")
//        val data = PieData( xvalues,piedataset)
//
//        chart_view.setData(data)
//        piedataset.setColors(ColorTemplate.JOYFUL_COLORS)
//        piedataset.setSliceSpace(2f)
//        piedataset.setValueTextColor(Color.WHITE)
//        piedataset.setValueTextSize(10f)
//        piedataset.setSliceSpace(5f)
//    }
}