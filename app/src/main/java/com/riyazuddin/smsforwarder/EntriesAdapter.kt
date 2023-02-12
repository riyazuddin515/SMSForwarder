package com.riyazuddin.smsforwarder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.riyazuddin.smsforwarder.databinding.EntryBinding

class EntriesAdapter : RecyclerView.Adapter<EntriesAdapter.EntryViewHolder>() {

    inner class EntryViewHolder(val binding: EntryBinding): RecyclerView.ViewHolder(binding.root)

    private val differCallback = object : DiffUtil.ItemCallback<Entry>(){
        override fun areItemsTheSame(oldItem: Entry, newItem: Entry): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Entry, newItem: Entry): Boolean {
            return oldItem.forwardTo == newItem.forwardTo && oldItem.contains == newItem.contains
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntryViewHolder {
        return EntryViewHolder(
            EntryBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: EntryViewHolder, position: Int) {
        val entry = differ.currentList[position]
        holder.binding.apply {
            forwardToTV.text = entry.forwardTo
            containsTV.text = entry.contains
            deleteIB.setOnClickListener {
                onEntryDelete?.let {
                    it(entry)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var onEntryDelete: ((Entry) -> Unit)? = null
    fun setOnEntryDelete(listener: (Entry) -> Unit){
        onEntryDelete = listener
    }
}


