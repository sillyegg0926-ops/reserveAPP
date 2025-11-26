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

private val selectedDates = mutableSetOf<LocalDate>()
private val today = LocalDate.now()

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
        val calendarView = findViewById<CalendarView>(R.id.exFiveCalendar)
        val monthYearText = findViewById<TextView>(R.id.monthYearText)

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
                    val clickedDate = container.day.date
                    val oldSelectedDate = selectedDates.firstOrNull() // 取得舊的選中日期

                    // 1. 更新 selectedDates 集合
                    selectedDates.clear()
                    selectedDates.add(clickedDate)

                    // 2. 通知舊日期重繪（移除紅框）
                    if (oldSelectedDate != null) {
                        calendarView.notifyDateChanged(oldSelectedDate)
                    }

                        // 3. 通知新日期重繪（加上紅框）
                        calendarView.notifyDateChanged(clickedDate)

                }


            }
            private fun bindDate(date: LocalDate, textView: TextView, isSelectable: Boolean) {
                textView.text = date.dayOfMonth.toString()
                if (isSelectable) {
                    when {
                        selectedDates.contains(date) -> {
                            textView.setBackgroundResource(R.drawable.shape_rectangle)
                        }
                        today == date -> {
                            textView.setBackgroundResource(R.drawable.shape_ring)
                        }

                    }
                }
            }   //當天日期標示


        }


    }

    private fun onDayClick(data: CalendarDay) {
        val fab = findViewById<FloatingActionButton>(R.id.FAB)
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())
        val formattedDate = data.date.format(dateFormatter)
        showSnackbar("已點選日期 : $formattedDate。", Snackbar.LENGTH_LONG)
//        showCustomAlertDialog(formattedDate)
        fab.setOnClickListener {
            showCustomAlertDialog(formattedDate)
        }

        //        下拉選單時段
    }

     private fun showCustomAlertDialog(formattedDate: String) {
        val builder = MaterialAlertDialogBuilder(this)
        val inflater = LayoutInflater.from(this)
        val dialogView = inflater.inflate(R.layout.textinput, null)




         builder.setView(dialogView)

         val autoCompleteTextView = dialogView.findViewById<MaterialAutoCompleteTextView>(R.id.auto_complete_menu)
         val items = arrayOf(
             "09:00~10.00", "10:00~11.00",
             "11:00~12.00","13:00~14.00",
             "14:00~15.00","15:00~16.00",
             "16:00~17.00","19:00~20:00")
         val adapter = ArrayAdapter(this, R.layout.list_item, items)
         autoCompleteTextView.setAdapter(adapter)
         val dialogTitle = dialogView.findViewById<TextView>(R.id.date)
         val dialogMessage = dialogView.findViewById<TextView>(R.id.time)
         val dialogButton = dialogView.findViewById<Button>(R.id.btnDialogConfirm)

         dialogTitle.text = formattedDate
         dialogMessage.text = "請選擇時段"
         dialogButton.text = "確定"

         dialogButton.setOnClickListener {
             val selectedItem = autoCompleteTextView.text.toString()
             if (selectedItem.isNotEmpty()) {
                 showSnackbar("已選擇日期 $selectedItem", Toast.LENGTH_SHORT)
             }


         }

         val alertDialog = builder.create()
         alertDialog.show()



     }

    private fun showSnackbar(message: String, duration: Int) {
        val rootView = findViewById<android.view.View>(R.id.main)
        Snackbar.make(rootView, message, duration).show()
    }
}




