package com.kemoneyan.aplikasikemoneyan

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray
import org.json.JSONObject

class kategori : AppCompatActivity() {

    private lateinit var rvKategori: RecyclerView
    private lateinit var btnTambahKategori: Button
    private lateinit var adapter: KategoriAdapter
    private val listKategori = mutableListOf<DcKategori>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_kategori)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        rvKategori = findViewById(R.id.rvKategori)
        btnTambahKategori = findViewById(R.id.btnTambahKategori)

        // Load saved or default categories
        loadKategoriList()

        // Setup RecyclerView & Adapter
        adapter = KategoriAdapter(
            listKategori = listKategori,
            onEditClick = { item, position -> showDialogEditKategori(item, position) },
            onDeleteClick = { item, position -> showDialogHapusKategori(item, position) }
        )
        rvKategori.layoutManager = LinearLayoutManager(this)
        rvKategori.adapter = adapter

        // Setup listener for Tambah Kategori button
        btnTambahKategori.setOnClickListener {
            showDialogTambahKategori()
        }
    }

    private fun showDialogTambahKategori() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_tambah_kategori, null)
        val etNamaKategori = dialogView.findViewById<EditText>(R.id.etNamaKategori)
        val rbPemasukan = dialogView.findViewById<RadioButton>(R.id.rbPemasukan)

        val builder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("Simpan", null)
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }

        val dialog = builder.create()
        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val nama = etNamaKategori.text.toString().trim()
            val tipe = if (rbPemasukan.isChecked) "Pemasukan" else "Pengeluaran"

            if (nama.isEmpty()) {
                etNamaKategori.error = "Nama kategori tidak boleh kosong"
                return@setOnClickListener
            }

            // ID disesuaikan dengan nama kategori agar tidak double
            val id = nama
            val isDuplicate = listKategori.any { it.id.equals(id, ignoreCase = true) }
            if (isDuplicate) {
                etNamaKategori.error = "Kategori sudah ada!"
                Toast.makeText(this, "Kategori dengan nama '$nama' sudah ada!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val newKategori = DcKategori(id = id, nama = nama, tipe = tipe)
            listKategori.add(newKategori)
            saveKategoriList()
            adapter.notifyItemInserted(listKategori.size - 1)
            Toast.makeText(this, "Kategori '$nama' berhasil ditambahkan", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
    }

    private fun showDialogEditKategori(item: DcKategori, position: Int) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_tambah_kategori, null)
        val tvDialogTitle = dialogView.findViewById<TextView>(R.id.tvDialogTitle)
        val etNamaKategori = dialogView.findViewById<EditText>(R.id.etNamaKategori)
        val rbPemasukan = dialogView.findViewById<RadioButton>(R.id.rbPemasukan)
        val rbPengeluaran = dialogView.findViewById<RadioButton>(R.id.rbPengeluaran)

        tvDialogTitle.text = "Edit Kategori"
        etNamaKategori.setText(item.nama)
        if (item.tipe.equals("Pemasukan", ignoreCase = true)) {
            rbPemasukan.isChecked = true
        } else {
            rbPengeluaran.isChecked = true
        }

        val builder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("Simpan", null)
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }

        val dialog = builder.create()
        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val nama = etNamaKategori.text.toString().trim()
            val tipe = if (rbPemasukan.isChecked) "Pemasukan" else "Pengeluaran"

            if (nama.isEmpty()) {
                etNamaKategori.error = "Nama kategori tidak boleh kosong"
                return@setOnClickListener
            }

            val newId = nama
            // Check if another category already has this name/id
            val isDuplicate = listKategori.indices.any { i ->
                i != position && listKategori[i].id.equals(newId, ignoreCase = true)
            }
            if (isDuplicate) {
                etNamaKategori.error = "Kategori sudah ada!"
                Toast.makeText(this, "Kategori dengan nama '$nama' sudah ada!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            item.id = newId
            item.nama = nama
            item.tipe = tipe

            saveKategoriList()
            adapter.notifyItemChanged(position)
            Toast.makeText(this, "Kategori berhasil diperbarui", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
    }

    private fun showDialogHapusKategori(item: DcKategori, position: Int) {
        AlertDialog.Builder(this)
            .setTitle("Hapus Kategori")
            .setMessage("Apakah Anda yakin ingin menghapus kategori '${item.nama}'?")
            .setPositiveButton("Hapus") { dialog, _ ->
                listKategori.removeAt(position)
                saveKategoriList()
                adapter.notifyItemRemoved(position)
                adapter.notifyItemRangeChanged(position, listKategori.size - position)
                Toast.makeText(this, "Kategori '${item.nama}' berhasil dihapus", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun saveKategoriList() {
        val jsonArray = JSONArray()
        for (kategori in listKategori) {
            val obj = JSONObject()
            obj.put("id", kategori.id)
            obj.put("nama", kategori.nama)
            obj.put("tipe", kategori.tipe)
            jsonArray.put(obj)
        }
        val prefs = getSharedPreferences("kategori_prefs", MODE_PRIVATE)
        prefs.edit().putString("kategori_list", jsonArray.toString()).apply()
    }

    private fun loadKategoriList() {
        listKategori.clear()
        val prefs = getSharedPreferences("kategori_prefs", MODE_PRIVATE)
        val jsonString = prefs.getString("kategori_list", null)

        if (!jsonString.isNullOrEmpty()) {
            try {
                val jsonArray = JSONArray(jsonString)
                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    listKategori.add(
                        DcKategori(
                            id = obj.getString("id"),
                            nama = obj.getString("nama"),
                            tipe = obj.getString("tipe")
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            // Default initial categories
            listKategori.add(DcKategori("Gaji", "Gaji", "Pemasukan"))
            listKategori.add(DcKategori("Makanan", "Makanan", "Pengeluaran"))
            listKategori.add(DcKategori("Transportasi", "Transportasi", "Pengeluaran"))
            saveKategoriList()
        }
    }
}
