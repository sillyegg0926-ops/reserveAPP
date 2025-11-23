package com.example.myreserveapp.calendar

import android.view.View
import android.widget.TextView
import com.kizitonwose.calendar.view.ViewContainer
import com.example.myreserveapp.R
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.view.CalendarView
import com.kizitonwose.calendar.view.MonthDayBinder
import java.time.LocalDate


//private val DayViewContainer.calendarView: Any
private var selectedDate: LocalDate? = null

class DayViewContainer(view: View) : ViewContainer(view) {
    val textView = view.findViewById<TextView>(R.id.calendarDayText)

    val calendarView = view.findViewById<CalendarView>(R.id.calendarView)
    lateinit var day: CalendarDay

    // With ViewBinding
    // val textView = CalendarDayLayoutBinding.bind(view).calendarDayText
    init {
        view.setOnClickListener {
//            // Use the CalendarDay associated with this container.
//            // Check the day position as we do not want to select in or out dates.
//            if (day.position == DayPosition.MonthDate) {
//                // Keep a reference to any previous selection
//                // in case we overwrite it and need to reload it.
//                val currentSelection = selectedDate
//                if (currentSelection == day.date) {
//                    // If the user clicks the same date, clear selection.
//                    selectedDate = null
//                    // Reload this date so the dayBinder is called
//                    // and we can REMOVE the selection background.
//                    calendarView.notifyDateChanged(currentSelection)
//                } else {
//                    selectedDate = day.date
//                    // Reload the newly selected date so the dayBinder is
//                    // called and we can ADD the selection background.
//                    calendarView.notifyDateChanged(day.date)
//                    if (currentSelection != null) {
//                        // We need to also reload the previously selected
//                        // date so we can REMOVE the selection background.
//                        calendarView.notifyDateChanged(currentSelection)
//                    }
//                }
//            }
        }
    }
}
