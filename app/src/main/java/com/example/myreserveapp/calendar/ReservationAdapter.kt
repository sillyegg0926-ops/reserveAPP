package com.example.myreserveapp.calendar

import android.content.Context
import com.example.myreserveapp.Reservation
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myreserveapp.R

class ReservationAdapter(
    private var reservations: List<String>,
    private val onItemLongClick: (String) -> Unit // 新增長按回調
) : RecyclerView.Adapter<ReservationAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val timeslotText: TextView = view.findViewById(R.id.textView_title1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val displayString: String = reservations[position]
        holder.timeslotText.text = displayString

        // 設定長按監聽器
        holder.itemView.setOnLongClickListener {
            onItemLongClick(displayString)
            true // 返回 true 表示事件已處理
        }
    }

    override fun getItemCount(): Int {
        return reservations.size
    }

    fun updateData(newReservations: List<String>) {
        this.reservations = newReservations
        notifyDataSetChanged()
    }
}
