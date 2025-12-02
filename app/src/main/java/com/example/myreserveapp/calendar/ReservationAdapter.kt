package com.example.myreserveapp.calendar

import android.content.Context
import com.example.myreserveapp.Reservation
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myreserveapp.R

// 假設您有一個 list_item_reservation.xml 佈局文件
// 它包含兩個 TextViews 用於顯示時間和人數
class ReservationAdapter(private var reservations: List<String>) :
    RecyclerView.Adapter<ReservationAdapter.ViewHolder>(){

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // 請確保這裡的 ID 與您的 view_item.xml 中的 TextView ID 相符
        val timeslotText: TextView = view.findViewById(R.id.textView_title1)
    }

    // 創建 ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_view, parent, false) // 替換成您實際的 item layout 名稱
        return ViewHolder(view)
    }

    // 綁定資料
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val displayString: String = reservations[position]

        // 設置 TextView 文本
        holder.timeslotText.text = displayString

        // 注意：舊的邏輯如 holder.textView.text = reservation.timeslot 將不再適用
    }

    // 獲取項目數量
    override fun getItemCount(): Int {
        return reservations.size
    }

    // *** 新增的公用函式：用於更新資料 ***
    fun updateData(newReservations: List<String>) {
        this.reservations = newReservations
        notifyDataSetChanged() // 通知 RecyclerView 重新繪製
    }
}
////    RecyclerView.Adapter<ReservationAdapter.ReservationViewHolder>() {
//
//    class ReservationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        // 假設您的佈局中有這些 ID
//        val timeSlotText: TextView = itemView.findViewById(R.id.textView_title1)
//        val countText: TextView = itemView.findViewById(R.id.textView_title4)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservationViewHolder {
//        // 請將 R.layout.list_item_reservation 替換為您實際的列表項佈局檔案
//        val view = LayoutInflater.from(parent.context)
//            .inflate(R.layout.item_view, parent, false)
//        return ReservationViewHolder(view)
//    }
//    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        val displayString = reservations[position]
//        // 假設您的 ViewHolder.textView 是用來顯示時段的 TextView
//        holder.textView.text = displayString
//        // 舊的邏輯 (例如：reservation.timeslot) 將不再適用
//    }
//
////    override fun onBindViewHolder(holder: ReservationViewHolder, position: Int) {
////        val reservation = reservations[position]
////        holder.timeSlotText.text = reservation.timeSlot
//////        holder.countText.text = "人數: ${reservation.currentCount}"
////    }
//
//    override fun getItemCount(): Int {
//        return reservations.size // 數量的計算現在是正確的
//    }
////            Int = reservations.size
//    fun updateData(newReservations: List<String>) {
//        this.reservations = newReservations
//        notifyDataSetChanged()
//    }
////    fun updateData(newReservations: List<Reservation>) {
////        this.reservations = newReservations
////        notifyDataSetChanged() // 通知 RecyclerView 刷新內容
////    }
