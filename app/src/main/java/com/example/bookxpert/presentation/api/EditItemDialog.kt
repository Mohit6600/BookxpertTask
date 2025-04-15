package com.example.bookxpert.presentation.api

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.bookxpert.data.local.model.LocalApiItem
import com.example.bookxpert.databinding.DialogEditItemBinding

class EditItemDialog(
    private val item: LocalApiItem,
    private val onUpdate: (LocalApiItem) -> Unit
) : DialogFragment() {

    private lateinit var binding: DialogEditItemBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogEditItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.editName.setText(item.name)
        binding.editCpu.setText(item.color ?: "")
        binding.editStorage.setText(item.capacity ?: "")

        binding.buttonSave.setOnClickListener {
            val updatedItem = item.copy(
                name = binding.editName.text.toString(),
                color = binding.editCpu.text.toString(),
                capacity = binding.editStorage.text.toString()
            )
            onUpdate(updatedItem)
            dismiss()
        }

        binding.buttonCancel.setOnClickListener {
            dismiss()
        }
    }
}