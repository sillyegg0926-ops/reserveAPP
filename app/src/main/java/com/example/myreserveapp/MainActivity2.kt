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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myreserveapp.calendar.ReservationAdapter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MainActivity2 : AppCompatActivity() {

    private var selectedDate: LocalDate? = null
    private val today = LocalDate.now()
    private lateinit var calendarView: CalendarView
    private lateinit var myRecyclerView: RecyclerView

    private val allReservations = mutableListOf<Reservation>()

    private var mDate: CalendarDay? = null
    private lateinit var recyclerView: RecyclerView
    private var adapter: ReservationAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main2)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 載入已儲存的預約
        loadReservations()

        calendarView = findViewById(R.id.exFiveCalendar)
        val monthYearText = findViewById<TextView>(R.id.monthYearText)
        myRecyclerView = findViewById(R.id.my_recycler_view)
        recyclerView = myRecyclerView // 初始化 recyclerView

        val fab = findViewById<FloatingActionButton>(R.id.FAB)
        fab.setOnClickListener {
            showCustomAlertDialog()
        }

        // 1. 準備範例資料
        val initialReservations = listOf<Reservation>()

        // 2. 設定 LayoutManager (決定列表如何排列，這裡使用垂直列表)
        myRecyclerView.layoutManager = LinearLayoutManager(this)

        // 3. 設定 Adapter (連接資料和列表項目視圖)
        val initialAdapter = ReservationAdapter(emptyList())
        myRecyclerView.adapter = initialAdapter
        adapter = initialAdapter

        // 若未選日期，預設顯示當天預約
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val todayDate = LocalDate.now().format(dateFormatter)
        updateRecyclerView(todayDate)

        val currentMonth = YearMonth.now()
        val startMonth = currentMonth
        val endMonth = currentMonth.plusMonths(100)
        val firstDayOfWeek = firstDayOfWeekFromLocale()
        calendarView.setup(startMonth, endMonth, firstDayOfWeek)
        calendarView.scrollToMonth(currentMonth)

        calendarView.monthScrollListener = { calendarMonth ->
            val yearMonth = calendarMonth.yearMonth
            val monthText = yearMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
            val yearText = yearMonth.year.toString()
            monthYearText.text = "$yearText 年 $monthText"
        }

        calendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, data: CalendarDay ) {
                container.textView.text = data.date.dayOfMonth.toString()
                if (data.position == DayPosition.MonthDate) {
                    container.textView.setTextColor(Color.BLACK)
                } else {
                    container.textView.setTextColor(Color.GRAY)
                }

                val textView = container.textView
                textView.text = data.date.dayOfMonth.toString()
                container.day = data
                val today = LocalDate.now()
                val isToday = data.date == today
                val isSelected = data.date == selectedDate
                when {
                    isSelected && isToday -> {
                        container.textView.setBackgroundResource(R.drawable.shape)
                    }
                    isSelected -> {
                        container.textView.setBackgroundResource(R.drawable.shape_ring)
                    }
                    isToday -> {
                        container.textView.setBackgroundResource(R.drawable.shape_rectangle)
                    }
                    else -> {
                        container.textView.background = null
                    }
                }

                container.view.setOnClickListener {
                    mDate = data
                    onDayClick(data)
                }
            }
        }
    }

    private fun onDayClick(data: CalendarDay) {
        val clickedDate = data.date
        val oldSelectedDate = selectedDate

        if (selectedDate == clickedDate) {
            selectedDate = null
        } else {
            selectedDate = clickedDate
        }

        calendarView?.notifyDateChanged(clickedDate)
        if (oldSelectedDate != null && oldSelectedDate != clickedDate) {
            calendarView?.notifyDateChanged(oldSelectedDate)
        }
        
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formattedDate = selectedDate?.format(dateFormatter) ?: ""
        // 如果取消選擇 (formattedDate 為空)，則顯示當天預約
        if (formattedDate.isEmpty()) {
            val todayFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val todayString = LocalDate.now().format(todayFormatter)
            updateRecyclerView(todayString)
        } else {
            updateRecyclerView(formattedDate)
        }
    }

    private fun showCustomAlertDialog() {
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
        val dateToShow = mDate?.date ?: selectedDate ?: LocalDate.now()

        dialogTitle.text =  dateToShow.toString()
        dialogMessage.text = "請選擇時段"
        dialogButton.text = "確定"

        val alertDialog = builder.create()
        alertDialog.show()

        dialogButton.setOnClickListener {
            val selectedTimeSlot = autoCompleteTextView.text.toString()
            val selectedDate = dialogTitle.text.toString()

            if (selectedTimeSlot.isNotEmpty()) {
                val newReservation = Reservation(
                    date = selectedDate,
                    timeslot = selectedTimeSlot,
                    currentCount = 1,
                    maxCount = 1
                )
                allReservations.add(newReservation)

                // 儲存預約
                saveReservations()

                updateRecyclerView(selectedDate)

                showSnackbar("已新增預約: $selectedDate $selectedTimeSlot", Toast.LENGTH_SHORT)

                alertDialog.dismiss()

            } else {
                showSnackbar("請選擇一個時段", Toast.LENGTH_SHORT)
            }
        }
    }

    private fun showSnackbar(message: String, duration: Int) {
        val rootView = findViewById<android.view.View>(R.id.main)
        Snackbar.make(rootView, message, duration).show()
    }

    private fun updateRecyclerView(selectedDate: String) {
        if (selectedDate.isEmpty()) {
            adapter?.updateData(emptyList())
            return
        }

        val todayReservation = allReservations.filter {
            it.date == selectedDate
        }

        val groupedReservations = todayReservation
            .groupBy { it.timeslot }
            .map { (timeslot, list) ->
                Pair(timeslot, list.size)
            }
            .sortedBy { it.first }

        val displayList = groupedReservations.map { (timeslot, count) ->
            if (count > 1) {
                "預約時段 $timeslot 預約人數 $count 人"
            } else {
                "預約時段 $timeslot 預約人數 1 人"
            }
        }

        if (adapter == null) {
            val newAdapter = ReservationAdapter(displayList)
            recyclerView.adapter = newAdapter
            adapter = newAdapter
        } else {
            adapter!!.updateData(displayList)
        }
    }

    private fun saveReservations() {
        val sharedPreferences = getSharedPreferences("reservations_prefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(allReservations)
        editor.putString("reservations_list", json)
        editor.apply()
    }

    private fun loadReservations() {
        val sharedPreferences = getSharedPreferences("reservations_prefs", MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("reservations_list", null)
        val type = object : TypeToken<MutableList<Reservation>>() {}.type
        if (json != null) {
            val savedReservations: MutableList<Reservation> = gson.fromJson(json, type)
            allReservations.clear()
            allReservations.addAll(savedReservations)
        }
    }
}
