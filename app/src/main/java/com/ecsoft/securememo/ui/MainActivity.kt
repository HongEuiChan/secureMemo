package com.ecsoft.securememo.ui

import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ecsoft.securememo.DatabaseProvider
import com.ecsoft.securememo.Note
import com.ecsoft.securememo.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch

class MainActivity : BaseActivity() {

    private lateinit var adapter: NoteAdapter
    private val noteDao by lazy { DatabaseProvider.getDatabase(this).noteDao() }
    private val types = arrayOf("ACCOUNT", "TOKEN", "CARD", "NOTE")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupRecyclerView()
        setupFab()
        loadNotes(checkDummy = true)
    }

    private fun setupRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        adapter = NoteAdapter(
            notes = emptyList(),
            onCopy = { note -> copyToClipboard(note) },
            onEdit = { note -> showEditDialog(note) },
            onDelete = { note -> deleteNote(note) }
        )
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun setupFab() {
        findViewById<FloatingActionButton>(R.id.fabAdd).setOnClickListener {
            showEditDialog(null)
        }
    }

    private fun loadNotes(checkDummy: Boolean = false) {
        lifecycleScope.launch {
            var notes = noteDao.getAll()
            if (checkDummy && notes.isEmpty()) {
                insertDummyData()
                notes = noteDao.getAll()
            }
            adapter.updateNotes(notes)
        }
    }

    private suspend fun insertDummyData() {
        noteDao.insert(Note(type = "ACCOUNT", title = "Google", field1 = "james@gmail.com", field2 = "pass123"))
        noteDao.insert(Note(type = "CARD", title = "현대카드", field1 = "1234-5678-1234-5678", field2 = "1234", field3 = "777"))
        noteDao.insert(Note(type = "TOKEN", title = "OpenAI API", field1 = "sk-proj-xxxxxx"))
        noteDao.insert(Note(type = "NOTE", title = "비상연락처", field1 = "집: 02-123-4567\n엄마: 010-1234-5678"))
    }

    private fun showEditDialog(note: Note?) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_note, null)
        val spinner = dialogView.findViewById<Spinner>(R.id.spinnerType)
        val etTitle = dialogView.findViewById<TextInputEditText>(R.id.etTitle)
        val etField1 = dialogView.findViewById<TextInputEditText>(R.id.etField1)
        val etField2 = dialogView.findViewById<TextInputEditText>(R.id.etField2)
        val etField3 = dialogView.findViewById<TextInputEditText>(R.id.etField3)
        val layout1 = dialogView.findViewById<TextInputLayout>(R.id.layoutField1)
        val layout2 = dialogView.findViewById<TextInputLayout>(R.id.layoutField2)
        val layout3 = dialogView.findViewById<TextInputLayout>(R.id.layoutField3)

        val spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.note_types, android.R.layout.simple_spinner_item)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = spinnerAdapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                updateUIForType(types[position], layout1, layout2, layout3)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        note?.let {
            spinner.setSelection(types.indexOf(it.type))
            etTitle.setText(it.title)
            etField1.setText(it.field1)
            etField2.setText(it.field2)
            etField3.setText(it.field3)
        }

        AlertDialog.Builder(this)
            .setTitle(if (note == null) R.string.add_memo_title else R.string.edit_memo_title)
            .setView(dialogView)
            .setPositiveButton(R.string.save) { _, _ ->
                val type = types[spinner.selectedItemPosition]
                val title = etTitle.text.toString()
                if (title.isNotEmpty()) {
                    saveNote(note, type, title, etField1.text.toString(), etField2.text.toString(), etField3.text.toString())
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun updateUIForType(type: String, l1: TextInputLayout, l2: TextInputLayout, l3: TextInputLayout) {
        l1.visibility = View.VISIBLE
        l2.visibility = View.VISIBLE
        l3.visibility = View.VISIBLE
        
        when (type) {
            "ACCOUNT" -> {
                l1.hint = getString(R.string.id_hint)
                l2.hint = getString(R.string.pw_hint)
                l3.visibility = View.GONE
            }
            "TOKEN" -> {
                l1.hint = getString(R.string.token_hint)
                l2.visibility = View.GONE
                l3.visibility = View.GONE
            }
            "CARD" -> {
                l1.hint = getString(R.string.card_num_hint)
                l2.hint = getString(R.string.pw_hint)
                l3.hint = getString(R.string.cvc_hint)
            }
            "NOTE" -> {
                l1.hint = getString(R.string.memo_hint)
                l2.visibility = View.GONE
                l3.visibility = View.GONE
            }
        }
    }

    private fun saveNote(oldNote: Note?, type: String, title: String, f1: String, f2: String, f3: String) {
        lifecycleScope.launch {
            val newNote = oldNote?.copy(type = type, title = title, field1 = f1, field2 = f2, field3 = f3)
                ?: Note(type = type, title = title, field1 = f1, field2 = f2, field3 = f3)
            if (oldNote == null) noteDao.insert(newNote) else noteDao.update(newNote)
            loadNotes()
        }
    }

    private fun deleteNote(note: Note) {
        AlertDialog.Builder(this)
            .setTitle("삭제")
            .setMessage("정말 삭제하시겠습니까?")
            .setPositiveButton("삭제") { _, _ ->
                lifecycleScope.launch {
                    noteDao.delete(note.id)
                    loadNotes()
                }
            }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun copyToClipboard(note: Note) {
        val textToCopy = when(note.type) {
            "ACCOUNT" -> note.field2 // Password
            "TOKEN" -> note.field1   // Token
            "CARD" -> note.field1    // Card Number
            else -> note.field1      // Memo content
        } ?: ""
        
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = android.content.ClipData.newPlainText("secure_data", textToCopy)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, R.string.copied_to_clipboard, Toast.LENGTH_SHORT).show()
    }
}
