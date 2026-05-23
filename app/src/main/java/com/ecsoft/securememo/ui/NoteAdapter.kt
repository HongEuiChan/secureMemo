package com.ecsoft.securememo.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ecsoft.securememo.Note
import com.ecsoft.securememo.R

class NoteAdapter(
    private var notes: List<Note>,
    private val onCopy: (Note) -> Unit,
    private val onEdit: (Note) -> Unit,
    private val onDelete: (Note) -> Unit
) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    class NoteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTypeTag: TextView = view.findViewById(R.id.tvTypeTag)
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvSubtitle: TextView = view.findViewById(R.id.tvSubtitle)
        val btnCopy: Button = view.findViewById(R.id.btnCopy)
        val btnEdit: Button = view.findViewById(R.id.btnEdit)
        val btnDelete: Button = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]
        holder.tvTitle.text = note.title
        
        val context = holder.itemView.context
        when (note.type) {
            "ACCOUNT" -> {
                holder.tvTypeTag.text = "계정"
                holder.tvTypeTag.setBackgroundColor(androidx.core.content.ContextCompat.getColor(context, android.R.color.holo_blue_dark))
                holder.tvSubtitle.text = "ID: ${note.field1 ?: ""} / PW: ********"
            }
            "TOKEN" -> {
                holder.tvTypeTag.text = "토큰"
                holder.tvTypeTag.setBackgroundColor(androidx.core.content.ContextCompat.getColor(context, android.R.color.holo_purple))
                holder.tvSubtitle.text = "Token: ********"
            }
            "CARD" -> {
                holder.tvTypeTag.text = "카드"
                holder.tvTypeTag.setBackgroundColor(androidx.core.content.ContextCompat.getColor(context, android.R.color.holo_orange_dark))
                val cardNum = note.field1 ?: ""
                val hiddenCard = if (cardNum.length >= 8) cardNum.take(7) + "****" else cardNum
                holder.tvSubtitle.text = "$hiddenCard / PIN: ****"
            }
            else -> {
                holder.tvTypeTag.text = "메모"
                holder.tvTypeTag.setBackgroundColor(androidx.core.content.ContextCompat.getColor(context, android.R.color.darker_gray))
                holder.tvSubtitle.text = note.field1?.take(20) ?: ""
            }
        }

        holder.btnCopy.setOnClickListener { onCopy(note) }
        holder.btnEdit.setOnClickListener { onEdit(note) }
        holder.btnDelete.setOnClickListener { onDelete(note) }
    }

    override fun getItemCount() = notes.size

    fun updateNotes(newNotes: List<Note>) {
        notes = newNotes
        notifyDataSetChanged()
    }
}
