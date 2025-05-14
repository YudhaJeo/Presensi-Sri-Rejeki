package com.example.sri_rejeki_app.Dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sri_rejeki_app.databinding.ItemPresensiBinding
import java.text.SimpleDateFormat
import java.util.Locale

class PresensiAdapter(private val listPresensi: List<Presensi>) :
    RecyclerView.Adapter<PresensiAdapter.PresensiViewHolder>() {

    inner class PresensiViewHolder(val binding: ItemPresensiBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PresensiViewHolder {
        val binding = ItemPresensiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PresensiViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PresensiViewHolder, position: Int) {
        val item = listPresensi[position]
        holder.binding.tvStatus.text = item.jenis_presensi

        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputFormatJam = SimpleDateFormat("HH:mm", Locale.getDefault())
        val outputFormatTanggal = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID")) // format: Selasa, 25 Juli 2025

        val date = inputFormat.parse(item.waktu.toString())
        val timeText = outputFormatJam.format(date)
        val dateText = outputFormatTanggal.format(date)

        holder.binding.tvTanggal.text = dateText
        holder.binding.tvWaktu.text = timeText
    }

    override fun getItemCount(): Int = listPresensi.size
}
