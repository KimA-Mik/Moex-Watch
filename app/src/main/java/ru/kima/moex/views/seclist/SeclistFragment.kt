package ru.kima.moex.views.seclist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import ru.kima.moex.databinding.FragmentSecuritiesListBinding
import ru.kima.moex.views.factory

class SeclistFragment : Fragment() {

    private var _binding: FragmentSecuritiesListBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    private val seclistViewModel: SeclistViewModel by viewModels { factory() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSecuritiesListBinding.inflate(layoutInflater, container, false)
        binding.securityRecyclerView.layoutManager = LinearLayoutManager(context)
        //val decoration = DividerItemDecoration(activity, R.drawable.divider)
        val decoration = activity?.let { DividerItemDecoration(it) }
        if (decoration != null) {
            binding.securityRecyclerView.addItemDecoration(decoration)
        }
        binding.updateButton.setOnClickListener() {
            seclistViewModel.loadData()
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                seclistViewModel.data.collect { securities ->
                    val adapter = SeclistaAdapter(securities)
                    binding.securityRecyclerView.adapter = adapter
                }
            }
        }
    }
}