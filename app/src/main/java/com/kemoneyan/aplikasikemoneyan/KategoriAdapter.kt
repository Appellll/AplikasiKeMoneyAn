package com.kemoneyan.aplikasikemoneyan

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class KategoriAdapter(
    private val listKategori: MutableList<DcKategori>,
    private val onEditClick: (DcKategori, Int) -> Unit,
    private val onDeleteClick: (DcKategori, Int) -> Unit
) : RecyclerView.Adapter<KategoriAdapter.KategoriViewHolder>() {

    class KategoriViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textKategori: TextView = itemView.findViewById(R.id.textKategori)
        val tipeKategori: TextView = itemView.findViewById(R.id.tipeKategori)
        val btnEditKategori: Button = itemView.findViewById(R.id.btnEditKategori)
        val btnHapusKategori: Button = itemView.findViewById(R.id.btnHapusKategori)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KategoriViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_kategori, parent, false)
        return KategoriViewHolder(view)
    }

    override fun onBindViewHolder(holder: KategoriViewHolder, position: Int) {
        val item = listKategori[position]
        holder.textKategori.text = item.nama
        holder.tipeKategori.text = item.tipe

        if (item.tipe.equals("Pemasukan", ignoreCase = true)) {
            holder.tipeKategori.setBackgroundColor(Color.parseColor("#2E7D32"))
        } else {
            holder.tipeKategori.setBackgroundColor(Color.parseColor("#C62828"))
        }

        holder.btnEditKategori.setOnClickListener {
            val currentPos = holder.bindingAdapterPosition
            if (currentPos != RecyclerView.NO_POSITION) {
                onEditClick(item, currentPos)
            }
        }

        holder.btnHapusKategori.setOnClickListener {
            val currentPos = holder.bindingAdapterPosition
            if (currentPos != RecyclerView.NO_POSITION) {
                onDeleteClick(item, currentPos)
            }
        }
    }

    override fun getItemCount(): Int = listKategori.size
}
