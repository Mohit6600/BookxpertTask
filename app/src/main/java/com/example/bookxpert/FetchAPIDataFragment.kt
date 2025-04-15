package com.example.bookxpert

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bookxpert.data.local.database.ApiDatabase
import com.example.bookxpert.domain.repository.ApiRepository
import com.example.bookxpert.data.remote.api.ApiService
import com.example.bookxpert.presentation.api.EditItemDialog
import com.example.bookxpert.data.local.model.LocalApiItem
import com.example.bookxpert.databinding.FragmentFetchAPIDataBinding
import com.example.bookxpert.presentation.home.adapter.ApiItemAdapter
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.launch

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class FetchAPIDataFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var apiRepository: ApiRepository
    private lateinit var binding: FragmentFetchAPIDataBinding
    private lateinit var adapter: ApiItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        val apiService = ApiService.create()
        val db = ApiDatabase.getDatabase(requireContext())
        apiRepository = ApiRepository(apiService, db.apiItemDao())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFetchAPIDataBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupListeners()
        observeItems()
        fetchData()
        setupNotificationPreference()
    }

    private fun setupRecyclerView() {
        adapter = ApiItemAdapter(
            onItemClick = { item -> showItemDetails(item) },
            onDeleteClick = { item -> deleteItem(item) },
            onUpdateClick = { item -> updateItem(item) }
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    private fun setupListeners() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            fetchData()
        }
    }

    private fun observeItems() {
        lifecycleScope.launch {
            apiRepository.getAllItems().collect { items ->
                adapter.submitList(items)
                binding.swipeRefreshLayout.isRefreshing = false
                if (items.isEmpty()) {
                    binding.emptyView.visibility = View.VISIBLE
                } else {
                    binding.emptyView.visibility = View.GONE
                }
            }
        }
    }

    private fun fetchData() {
        binding.swipeRefreshLayout.isRefreshing = true

        lifecycleScope.launch {
            try {
                apiRepository.fetchAndStoreItems()
            } catch (e: Exception) {
                showError("Failed to fetch data: ${e.message}")
            } finally {
                binding.swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    private fun showItemDetails(item: LocalApiItem) {
        // Implement item details display if needed
    }

    private fun deleteItem(item: LocalApiItem) {
        lifecycleScope.launch {
            try {
                apiRepository.deleteItem(item)
                showMessage("Item deleted successfully")
                sendDeletionNotification(item) // Call notification logic on delete
            } catch (e: Exception) {
                Log.e("DeleteItemError", "Failed to delete item: ${e.message}", e) // Log the exception

                showError("Failed to delete item: ${e.message}")
            }
        }
    }

    private fun updateItem(item: LocalApiItem) {
        val dialog = EditItemDialog(item) { updatedItem ->
            lifecycleScope.launch {
                try {
                    apiRepository.updateItem(updatedItem)
                    showMessage("Item updated successfully")
                    // Do NOT call sendDeletionNotification here
                } catch (e: Exception) {
                    showError("Failed to update item: ${e.message}")
                }
            }
        }
        dialog.show(parentFragmentManager, "EditItemDialog")
    }

    private fun showMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    private fun setupNotificationPreference() {
        binding.enableNotificationsSwitch.setOnCheckedChangeListener { _, isChecked ->
            val sharedPreferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            sharedPreferences.edit().putBoolean(MyFirebaseMessagingService.NOTIFICATION_PREFERENCE_KEY, isChecked).apply()
        }

        val sharedPreferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        binding.enableNotificationsSwitch.isChecked =
            sharedPreferences.getBoolean(MyFirebaseMessagingService.NOTIFICATION_PREFERENCE_KEY, true)
    }

    private fun sendDeletionNotification(item: LocalApiItem) {
        if (shouldSendNotification()) {
            val notificationData = mapOf(
                "itemName" to item.name,
                "itemId" to item.id
            )
            // Get the MyFirebaseMessagingService instance
            val messagingService = (requireActivity().application as AuthApplication).myFirebaseMessagingService
            // Pass the application context to handleNotification
            messagingService.handleNotification(requireContext().applicationContext, notificationData)
        }
    }

    private fun shouldSendNotification(): Boolean {
        val sharedPreferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(MyFirebaseMessagingService.NOTIFICATION_PREFERENCE_KEY, true)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FetchAPIDataFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}