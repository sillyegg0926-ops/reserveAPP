package com.example.myreserveapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ChartActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val allReservations = mutableListOf<Reservation>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chart)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        loadReservationsFromFirestore()
    }

    private fun loadReservationsFromFirestore() {
        db.collection("reservations").document("all_reservations")
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val dataList = document.get("list") as? List<Map<String, Any>>
                    if (dataList != null) {
                        allReservations.clear()
                        for (item in dataList) {
                            val date = item["date"] as? String ?: ""
                            val timeslot = item["timeslot"] as? String ?: ""
                            val currentCount = (item["currentCount"] as? Long)?.toInt() ?: 1
                            val maxCount = (item["maxCount"] as? Long)?.toInt() ?: 1
                            if (date.isNotEmpty()) {
                                allReservations.add(Reservation(date, timeslot, currentCount, maxCount))
                            }
                        }
                    }
                }
                // Data loaded (or not), now setup the charts
                val chart = findViewById<BarChart>(R.id.chart)
                val chart2 = findViewById<HorizontalBarChart>(R.id.chart2)
                setupMonthlyChart(chart)
                setupYearlyChart(chart2)
            }
            .addOnFailureListener {
                // On failure, setup with empty data to avoid crash
                val chart = findViewById<BarChart>(R.id.chart)
                val chart2 = findViewById<HorizontalBarChart>(R.id.chart2)
                setupMonthlyChart(chart)
                setupYearlyChart(chart2)
            }
    }

    private fun setupMonthlyChart(chart: BarChart) {
        val currentMonthDate = LocalDate.now()
        val month = currentMonthDate.month
        val year = currentMonthDate.year

        // 1. 從資料庫資料產生 Entry
        val dailyCounts = allReservations
            .filter {
                try {
                    val reservationDate = LocalDate.parse(it.date, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    reservationDate.month == month && reservationDate.year == year
                } catch (e: Exception) { false }
            }
            .groupBy { LocalDate.parse(it.date, DateTimeFormatter.ofPattern("yyyy-MM-dd")).dayOfMonth }
            .mapValues { it.value.size }

        val entries = ArrayList<BarEntry>()
        // Ensure entries for all days in the month to maintain axis length, even if count is 0
        for (day in 1..currentMonthDate.lengthOfMonth()) {
             val count = dailyCounts[day]?.toFloat() ?: 0f
             // Only add if you want to show zero-value bars, otherwise, just use the dailyCounts loop
             if (count > 0) {
                 entries.add(BarEntry(day.toFloat(), count))
             }
        }

        if (entries.isEmpty()) {
            chart.data = null
            chart.invalidate()
            return
        }

        // 2. 從 Entry 建立 DataSet (套用您的樣式)
        val dataSet = BarDataSet(entries, "當月預約人數統計")
        dataSet.color = resources.getColor(R.color.blue_800)
        dataSet.valueTextColor = resources.getColor(R.color.example_5_page_bg_color)
        dataSet.valueTextSize = 14f

        // 3. 將 DataSet 放入 BarData 物件 (套用您的樣式)
        val barData = BarData(dataSet)
        barData.barWidth = 0.5f

        // 4. 將資料設定給圖表並更新 (套用您的樣式)
        chart.data = barData
        chart.setFitBars(true) // 使柱狀圖適合X軸

        // 將X軸標籤移至底部
        val xAxis = chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f

        // --- ★★★ 新增：修改圖例 (Legend) 的字體大小 ★★★ ---
        val legend = chart.legend
        legend.textSize = 12f             // 設定圖例文字大小，您可以調整 16f
        legend.textColor = resources.getColor(R.color.black) // 設定圖例文字顏色 (可選)
        legend.form = Legend.LegendForm.SQUARE   // 設定圖例標示的形狀 (可選)
        // ----------------------------------------------------

        chart.invalidate() // 刷新圖表
    }

    private fun setupYearlyChart(chart: HorizontalBarChart) {
        val currentYear = LocalDate.now().year

        // 1. 從資料庫資料產生 Entry
        val monthlyCounts = allReservations
            .filter {
                try { LocalDate.parse(it.date, DateTimeFormatter.ofPattern("yyyy-MM-dd")).year == currentYear }
                catch (e: Exception) { false }
            }
            .groupBy { LocalDate.parse(it.date, DateTimeFormatter.ofPattern("yyyy-MM-dd")).monthValue }
            .mapValues { it.value.size }

        val entries = ArrayList<BarEntry>()
        for (month in 1..12) {
            val count = monthlyCounts[month]?.toFloat() ?: 0f
            entries.add(BarEntry(month.toFloat(), count))
        }

        // 2. 從 Entry 建立 DataSet (套用您的樣式)
        val dataSet = BarDataSet(entries, "整年預約人數統計")
        dataSet.color = resources.getColor(R.color.example_6_month_bg_color)
        dataSet.valueTextColor = resources.getColor(R.color.example_5_page_bg_color)
        dataSet.valueTextSize = 20f

        // 3. 將 DataSet 放入 BarData 物件 (套用您的樣式)
        val barData = BarData(dataSet)
        barData.barWidth = 0.3f


        // 4. 將資料設定給圖表並更新
        chart.data = barData

        // 5. [選用設定] 美化圖表與標籤 (套用您的樣式)
        chart.axisRight.isEnabled = false

        val xAxis = chart.xAxis
        val labels = arrayOf("", "一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月")
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setLabelCount(12)
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        xAxis.axisMinimum = 0f
        xAxis.axisMaximum = 13f

        chart.axisRight.isEnabled = false

        val axisLeft = chart.axisLeft
        axisLeft.axisMinimum = 0f
        axisLeft.setDrawAxisLine(true)
        axisLeft.setDrawGridLines(true)

        chart.setExtraOffsets(20f, 10f, 20f, 10f)
        chart.setTouchEnabled(true)
        chart.setPinchZoom(true)
        chart.setFitBars(true)


        // --- ★★★ 新增：修改圖例 (Legend) 的字體大小 ★★★ ---
        val legend = chart.legend
        legend.textSize = 14f             // 設定圖例文字大小，您可以調整 16f
        legend.textColor = resources.getColor(R.color.black) // 設定圖例文字顏色 (可選)
        legend.form = Legend.LegendForm.SQUARE   // 設定圖例標示的形狀 (可選)
        // ----------------------------------------------------
        chart.invalidate() // 刷新圖表
    }
}
