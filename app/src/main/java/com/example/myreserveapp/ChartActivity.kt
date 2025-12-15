package com.example.myreserveapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.components.XAxis // 可選：用於進階設定
import com.github.mikephil.charting.charts.BarChart // 引入 BarChart
import com.github.mikephil.charting.data.BarData // 引入 BarData
import com.github.mikephil.charting.data.BarDataSet // 引入 BarDataSet
import com.github.mikephil.charting.data.BarEntry // 引入 BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter


class ChartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chart)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val chart = findViewById<BarChart>(R.id.chart)
        val chart2 = findViewById<HorizontalBarChart>(R.id.chart2)
        setupLineChart(chart)
        setupLineChart2(chart2)
    }
    private fun setupLineChart(chart: BarChart) {
        // 1. 建立 Entry 物件列表 (資料點)
        val entries = ArrayList<BarEntry>()
        entries.add(BarEntry(1f, 10f))
        entries.add(BarEntry(2f, 15f))
        entries.add(BarEntry(3f, 8f))
        entries.add(BarEntry(4f, 12f))
        entries.add(BarEntry(5f, 10f))
        entries.add(BarEntry(6f, 10f))
        entries.add(BarEntry(7f, 10f))
        entries.add(BarEntry(8f, 10f))
        entries.add(BarEntry(9f, 10f))
        entries.add(BarEntry(10f, 10f))
        entries.add(BarEntry(11f, 10f))
        entries.add(BarEntry(12f, 15f))
        entries.add(BarEntry(13f, 8f))
        entries.add(BarEntry(14f, 12f))
        entries.add(BarEntry(15f, 10f))
        entries.add(BarEntry(16f, 10f))
        entries.add(BarEntry(17f, 10f))
        entries.add(BarEntry(18f, 10f))
        entries.add(BarEntry(19f, 10f))
        entries.add(BarEntry(20f, 10f))
        entries.add(BarEntry(21f, 10f))
        entries.add(BarEntry(22f, 15f))
        entries.add(BarEntry(23f, 8f))
        entries.add(BarEntry(24f, 12f))
        entries.add(BarEntry(25f, 10f))
        entries.add(BarEntry(26f, 10f))
        entries.add(BarEntry(27f, 10f))
        entries.add(BarEntry(28f, 10f))
        entries.add(BarEntry(29f, 10f))
        entries.add(BarEntry(30f, 10f))
        entries.add(BarEntry(31f, 10f))


        // 2. 從 Entry 建立 DataSet
        val dataSet = BarDataSet(entries, "當月預約人數統計")
        // 您可以根據需要設定顏色和其他樣式...
         dataSet.color = resources.getColor(R.color.blue_800)
         dataSet.valueTextColor = resources.getColor(R.color.example_5_page_bg_color)


        // 3. 將 DataSet 放入 LineData 物件
        val barData = BarData(dataSet)
        // 設定柱狀圖的寬度 (可選)
        barData.barWidth = 0.5f

        // 4. 將資料設定給圖表並更新
        chart.data = barData
        chart.setFitBars(true) // 使柱狀圖適合X軸
        chart.invalidate() // 刷新圖表
    }
    private fun setupLineChart2(chart: HorizontalBarChart) {
        // 1. 建立 Entry 物件列表 (資料點)
        val entries = ArrayList<BarEntry>()
        entries.add(BarEntry(1f, 10f))
        entries.add(BarEntry(2f, 15f))
        entries.add(BarEntry(3f, 8f))
        entries.add(BarEntry(4f, 12f))
        entries.add(BarEntry(5f, 10f))
        entries.add(BarEntry(6f, 10f))
        entries.add(BarEntry(7f, 10f))
        entries.add(BarEntry(8f, 10f))
        entries.add(BarEntry(9f, 10f))
        entries.add(BarEntry(10f, 10f))
        entries.add(BarEntry(11f, 10f))
        entries.add(BarEntry(12f, 10f))

        // 2. 從 Entry 建立 DataSet
        val dataSet = BarDataSet(entries, "整年預約人數統計")
        // 您可以根據需要設定顏色和其他樣式...
        dataSet.color = resources.getColor(R.color.example_6_month_bg_color)
        dataSet.valueTextColor = resources.getColor(R.color.example_5_page_bg_color)


        // 3. 將 DataSet 放入 LineData 物件
        val barData = BarData(dataSet)
        // 設定柱狀圖的寬度 (可選)
        barData.barWidth = 0.3f
        // 設定數值顯示在柱狀圖外側 (避免數值跟圖疊在一起)
        barData.setValueTextSize(10f)

        // 4. 將資料設定給圖表並更新
        chart.data = barData

        // 5. [選用設定] 美化圖表與標籤

        // 隱藏右側的 Y 軸，通常水平條狀圖只需要左側的 Y 軸
        chart.axisRight.isEnabled = false

        // 設定 X 軸的標籤（例如：產品名稱）
        val xAxis = chart.xAxis
        val labels = arrayOf("", "一月", "二月", "三月", "四月", "五月", "六月",
                            "七月", "八月", "九月", "十月", "十一月", "十二月")
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setLabelCount(12)
        xAxis.setDrawGridLines(false) // 不顯示 X 軸網格線
        xAxis.granularity = 1f // 設置最小間隔，確保顯示所有標籤
        // 【關鍵】設定軸的最小值與最大值，確保第一個月和最後一個月不會被切掉
        xAxis.axisMinimum = 0f
        xAxis.axisMaximum = 13f

        // --- Y 軸設定 (在水平圖中，這是水平的軸，顯示人數) ---

        // 隱藏右側 Y 軸 (水平圖的上方)
        chart.axisRight.isEnabled = false

        val axisLeft = chart.axisLeft
        // 設定最小值從 0 開始
        axisLeft.axisMinimum = 0f

        // 為了讓這條軸的線更明顯 (可選)
        axisLeft.setDrawAxisLine(true)
        axisLeft.setDrawGridLines(true)


        // --- 其他外觀設定 ---

        // 【關鍵】增加圖表左側的額外邊距，確保 "十二月" 這種較長的文字不會被切掉
        // 參數順序: Left, Top, Right, Bottom
        chart.setExtraOffsets(20f, 10f, 20f, 10f)

        chart.setTouchEnabled(true) // 允許互動
        chart.setPinchZoom(true) // 允許縮放
//        chart.description.isEnabled = false // 隱藏描述文字
//        chart.legend.isEnabled = false // 如果不需要圖例可以隱藏
        // 讓柱狀圖自動適應高度
        chart.setFitBars(true)
        chart.invalidate() // 刷新圖表
    }
}