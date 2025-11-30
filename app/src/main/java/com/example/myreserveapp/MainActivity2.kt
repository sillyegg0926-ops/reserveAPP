package com.example.myreserveapp

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.children
import com.example.myreserveapp.calendar.DayViewContainer
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputLayout
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.atStartOfMonth
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.view.CalendarView
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.ViewContainer
import com.kizitonwose.calendar.view.WeekCalendarView
import com.kizitonwose.calendar.view.YearCalendarView
import java.time.LocalDate
import java.time.Year
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import androidx.recyclerview.widget.LinearLayoutManager // 新增
import androidx.recyclerview.widget.RecyclerView // 新增
import com.example.myreserveapp.calendar.ReservationAdapter

private var selectedDates = mutableSetOf<LocalDate>()
private val today = LocalDate.now()
private lateinit var calendarView: CalendarView
private lateinit var myRecyclerView: RecyclerView // 宣告 RecyclerView 變數

private val allReservations = mutableListOf<Reservation>()



class MainActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main2)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets

        }
        calendarView = findViewById(R.id.exFiveCalendar)
        val monthYearText = findViewById<TextView>(R.id.monthYearText)
        myRecyclerView = findViewById(R.id.my_recycler_view)

        // 1. 準備範例資料
        val initialReservations = listOf<Reservation>()

        // 2. 設定 LayoutManager (決定列表如何排列，這裡使用垂直列表)
        myRecyclerView.layoutManager = LinearLayoutManager(this)

        // 3. 設定 Adapter (連接資料和列表項目視圖)
        myRecyclerView.adapter = ReservationAdapter(allReservations)


        val currentMonth = YearMonth.now()
        val startMonth = currentMonth // Adjust as needed
        val endMonth = currentMonth.plusMonths(100) // Adjust as needed
        val firstDayOfWeek = firstDayOfWeekFromLocale() // Available from the library
        calendarView.setup(startMonth, endMonth, firstDayOfWeek)
        calendarView.scrollToMonth(currentMonth)


        // ... 您的日曆初始化代碼之後
        calendarView.monthScrollListener = { calendarMonth ->
            // 1. 獲取當前可見月份的 YearMonth 物件
            val yearMonth = calendarMonth.yearMonth

            // 2. 格式化年和月 (使用所需的 Locale)
            val monthText = yearMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
            val yearText = yearMonth.year.toString()

            // 3. 更新 TextView
            monthYearText.text = "$yearText 年 $monthText"
        }


        calendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, data: CalendarDay) {
                bindDate(data.date, container.textView, data.position == DayPosition.MonthDate)


                container.textView.text = data.date.dayOfMonth.toString()
                if (data.position == DayPosition.MonthDate) {
                    container.textView.setTextColor(Color.BLACK)
                } else {
                    container.textView.setTextColor(Color.GRAY)
                }

                val textView = container.textView
                textView.text = data.date.dayOfMonth.toString()
                container.day = data



                container.view.setOnClickListener {
                    onDayClick(data)
                }


            }

            private fun bindDate(date: LocalDate, textView: TextView, isSelectable: Boolean) {
                textView.text = date.dayOfMonth.toString()
                if (isSelectable) {
                    when {
                        selectedDates.contains(date) -> {
                            textView.setBackgroundResource(R.drawable.shape_ring)
                        }

                        today == date -> {
                            textView.setBackgroundResource(R.drawable.shape_rectangle)
                        }


                    }

                }
            }   //當天日期標示


        }


    }

    private fun onDayClick(data: CalendarDay) {


//        showCustomAlertDialog(formattedDate)
        val clickedDate = data.date
        val oldSelectedDate = selectedDates.firstOrNull() // 取得舊的選中日期

        // 1. 更新 selectedDates 集合
        if (selectedDates == clickedDate) {
            selectedDates = mutableSetOf()
        } else {
            selectedDates = mutableSetOf(clickedDate)
        }

        // 2. 通知舊日期重繪（移除紅框）

        if (oldSelectedDate != null && oldSelectedDate != clickedDate) {
            calendarView.notifyDateChanged(oldSelectedDate)
        }

        // 3. 通知新日期重繪（加上紅框）
        calendarView.notifyDateChanged(clickedDate)

        clickFab(data)

    }

    private fun clickFab(data: CalendarDay) {
        val fab = findViewById<FloatingActionButton>(R.id.FAB)
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())
        val formattedDate = data.date.format(dateFormatter)
        showSnackbar("已點選日期 : $formattedDate。", Snackbar.LENGTH_LONG)

        if (data == today) {
            fab == showCustomAlertDialog(data.toString())
        }
        if (formattedDate != null) {
            fab.setOnClickListener {
                showCustomAlertDialog(formattedDate)
            }
        }
    }
//


//        fab.setOnClickListener {
//            showCustomAlertDialog(formattedDate)
//        }


    private fun showCustomAlertDialog(clickFab: String) {
        val builder = MaterialAlertDialogBuilder(this)
        val inflater = LayoutInflater.from(this)
        val dialogView = inflater.inflate(R.layout.textinput, null)




        builder.setView(dialogView)

        val items = arrayOf(
            "09:00~10.00", "10:00~11.00",
            "11:00~12.00","13:00~14.00",
            "14:00~15.00","15:00~16.00",
            "16:00~17.00","19:00~20:00")

        val autoCompleteTextView =
            dialogView.findViewById<MaterialAutoCompleteTextView>(R.id.auto_complete_menu)

        val adapter = ArrayAdapter(this, R.layout.list_item, items)
        autoCompleteTextView.setAdapter(adapter)
        val dialogTitle = dialogView.findViewById<TextView>(R.id.date)
        val dialogMessage = dialogView.findViewById<TextView>(R.id.time)
        val dialogButton = dialogView.findViewById<Button>(R.id.btnDialogConfirm)

        dialogTitle.text = clickFab
        dialogMessage.text = "請選擇時段"
        dialogButton.text = "確定"

        val alertDialog = builder.create() // 先創建對話框，以便在點擊時關閉
        alertDialog.show()

        dialogButton.setOnClickListener {
            val selectedTimeSlot = autoCompleteTextView.text.toString()
            val selectedDate = dialogTitle.text.toString() // 獲取當前對話框顯示的日期

            if (selectedTimeSlot.isNotEmpty()) {
                // 1. 呼叫新的函式來更新 RecyclerView
                updateMyRecyclerView(selectedDate, selectedTimeSlot)

                // 2. 顯示 Snackbar
                showSnackbar("已新增預約: $selectedDate $selectedTimeSlot", Toast.LENGTH_SHORT)

                // 3. 關閉對話框
                alertDialog.dismiss()

            } else {
                showSnackbar("請選擇一個時段", Toast.LENGTH_SHORT)
            }
        }

//        dialogButton.setOnClickListener {
//            val selectedItem = autoCompleteTextView.text.toString()
//            if (selectedItem.isNotEmpty()) {
//                showSnackbar("已選擇日期 $selectedItem", Toast.LENGTH_SHORT)
//            }
//
//
//        }

//        val alertDialog = builder.create()




    }

    private fun showSnackbar(message: String, duration: Int) {
        val rootView = findViewById<android.view.View>(R.id.main)
        Snackbar.make(rootView, message, duration).show()
    }

    private fun updateMyRecyclerView(selectedDate: String, selectedTimeSlot: String) {
        val adapter = myRecyclerView.adapter as? ReservationAdapter


        if (adapter != null) {
            // 1. 創建新的預約項目
            val newReservation = Reservation(
                timeSlot = "$selectedDate | $selectedTimeSlot", // 結合日期和時段
                currentCount = 0,
                maxCount = 0
            )

            // 2. 【關鍵】將新項目添加到累計列表
            allReservations.add(newReservation)

            // 3. 通知 Adapter 資料已更新
            // 由於 Adapter 已經持有 allReservations 的引用，這裡只需要告訴它刷新
            adapter.notifyDataSetChanged()

            // 或者，如果您在 Adapter 中使用了 updateData 方法，則呼叫：
            // adapter.updateData(allReservations)
        }
    }

//        if (adapter != null) {
//            // 創建只包含選擇時段的新列表
//            val displayData = listOf(
//                Reservation(
//                    timeSlot = "$selectedDate | $selectedTimeSlot",
//                    currentCount = 0, // 這裡僅作顯示，所以人數用 0
//                    maxCount = 0
//                )
//            )
//            // 將日期作為一個額外的項目顯示 (可選)
//            val dateItem = Reservation(
//                timeSlot = "選擇日期: $selectedDate",
//                currentCount = -1, // 使用特殊值標記為日期行
//                maxCount = 0
//            )
//
////             將日期和時段項目一起傳給 Adapter
//            adapter.updateData(listOf(dateItem) + displayData)
//        }
//    }
}




