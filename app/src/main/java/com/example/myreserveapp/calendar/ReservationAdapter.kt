package com.example.myreserveapp.calendar

import com.example.myreserveapp.Reservation
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myreserveapp.R

// 假設您有一個 list_item_reservation.xml 佈局文件
// 它包含兩個 TextViews 用於顯示時間和人數
class ReservationAdapter(private var reservations: List<Reservation>) :
    RecyclerView.Adapter<ReservationAdapter.ReservationViewHolder>() {

    class ReservationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // 假設您的佈局中有這些 ID
        val timeSlotText: TextView = itemView.findViewById(R.id.textView_title1)
        val countText: TextView = itemView.findViewById(R.id.textView_title4)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservationViewHolder {
        // 請將 R.layout.list_item_reservation 替換為您實際的列表項佈局檔案
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_view, parent, false)
        return ReservationViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReservationViewHolder, position: Int) {
        val reservation = reservations[position]
        holder.timeSlotText.text = reservation.timeSlot
//        holder.countText.text = "人數: ${reservation.currentCount}"
    }

    override fun getItemCount(): Int = reservations.size

    fun updateData(newReservations: List<Reservation>) {
        this.reservations = newReservations
        notifyDataSetChanged() // 通知 RecyclerView 刷新內容
    }
}