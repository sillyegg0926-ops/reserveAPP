package com.example.myreserveapp

import android.graphics.Color
import android.os.Bundle
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
//import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myreserveapp.calendar.DayViewContainer
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputLayout
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.view.CalendarView
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.ViewContainer
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

private var selectedDate: LocalDate? = null

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

        

        calendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
//            // Called only when a new container is needed.
//            override fun create(view: View) = DayViewContainer(view)
//
//            // Called every time we need to reuse a container.
//            override fun bind(container: DayViewContainer, data: CalendarDay) {
//                container.textView.text = data.date.dayOfMonth.toString()
//            }
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, data: CalendarDay) {

//                val textView = container.textView
//                textView.text = data.date.dayOfMonth.toString()
//                if (data.position == DayPosition.MonthDate) {
//                    textView.visibility = View.VISIBLE
//                    if (data.date == selectedDate) {
//                        textView.setTextColor(Color.BLUE)
//                    } else {
//                        textView.setTextColor(Color.BLACK)
//                        textView.background = null
//                    }
//                } else {
//                    textView.visibility = View.INVISIBLE
//
//                }

                

                val textView = container.textView
                textView.text = data.date.dayOfMonth.toString()
                container.day = data
                
                container.view.setOnClickListener {
                    onDayClick(data)


                }

////                 Any other binding logic
//                if (day.position == DayPosition.MonthDate) {
//                    // Show the month dates. Remember that views are reused!
//                    textView.visibility = View.VISIBLE
//                    if (day.date == selectedDate) {
//                        // If this is the selected date, show a round background and change the text color.
//                        textView.setTextColor(Color.BLUE)
//
//                    } else {
//                        // If this is NOT the selected date, remove the background and reset the text color.
//                        textView.setTextColor(Color.BLACK)
//                        textView.background = null
//                    }
//                } else {
//                    // Hide in and out dates
//                    textView.visibility = View.INVISIBLE
//                }
            }
        }
//                container.textView.text = data.date.dayOfMonth.toString()
//                if (data.position == DayPosition.MonthDate) {
//                    container.textView.setTextColor(Color.BLACK)
//                } else {
//                    container.textView.setTextColor(Color.GRAY)
//                }

//            }



//        }
        val currentMonth = YearMonth.now()
        val startMonth = currentMonth // Adjust as needed
        val endMonth = currentMonth.plusMonths(100) // Adjust as needed
        val firstDayOfWeek = firstDayOfWeekFromLocale() // Available from the library
        calendarView.setup(startMonth, endMonth, firstDayOfWeek)
        calendarView.scrollToMonth(currentMonth)

    }

    private fun onDayClick(data: CalendarDay) {
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())
        val formattedDate = data.date.format(dateFormatter)
        Toast.makeText(this, "Selected date: $formattedDate", Toast.LENGTH_SHORT).show()
        showCustomAlertDialog(formattedDate)

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

         val alertDialog = builder.create()
         alertDialog.show()



     }











// 假设在 Activity 中调用：
// override fun onCreate(savedInstanceState: Bundle?) {
//     super.onCreate(savedInstanceState)
//     setContentView(R.layout.activity_main)
//
//     findViewById<Button>(R.id.my_button).setOnClickListener {
//         showCustomAlertDialog(this) // 传递当前的 Activity 实例作为 Context
//     }



}




