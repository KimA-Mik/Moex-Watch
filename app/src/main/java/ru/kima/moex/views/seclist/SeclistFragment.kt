package ru.kima.moex.views.seclist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import ru.kima.moex.R
import ru.kima.moex.databinding.FragmentSecuritiesListBinding
import ru.kima.moex.model.Security
import ru.kima.moex.views.factory


class SeclistFragment : Fragment() {

    private var _binding: FragmentSecuritiesListBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    private val viewModel: SeclistViewModel by viewModels { factory() }
    private val adapter: SeclistaAdapter by lazy { SeclistaAdapter(viewModel) }

    private var state = State.Loading

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
        applyInterfaceState()
        binding.securityRecyclerView.adapter = adapter
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
                launch {
                    viewModel.data.collect { securities ->
                        updateSecuritiesList(securities)
                    }
                }
                launch {
                    viewModel.showDetails.collect { navEvent ->
                        navEvent.getValue()?.let { security ->
                            findNavController().navigate(
                                SeclistFragmentDirections.showSecurityDetails(security.SECID)
                            )
                        }
                    }
                }
            }
        }

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.fragment_securities_list_menu, menu)
                val search = menu.findItem(R.id.menu_item_search)
                val searchView = search?.actionView as SearchView
                searchView.apply {
                    isSubmitButtonEnabled = true
                    setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                        override fun onQueryTextChange(newText: String?): Boolean {
                            viewModel.queryString(newText)
                            return true
                        }

                        override fun onQueryTextSubmit(query: String?): Boolean {
                            viewModel.queryString(query)
                            return true
                        }
                    })
                }

            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menu_item_search -> {

                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun updateSecuritiesList(securities: List<Security>) {
        if (securities.isEmpty()) {
            if (state == State.Ready) {
                state = State.NoResults
            }
            applyInterfaceState()
            return
        }
        state = State.Ready
        applyInterfaceState()
        val diffCallback = SecurityDiffCallback(adapter.securities, securities)
        val diffSecurities = DiffUtil.calculateDiff(diffCallback)
        adapter.securities = securities
        diffSecurities.dispatchUpdatesTo(adapter)
    }

    private fun applyInterfaceState() {
        when (state) {
            State.Loading -> {
                binding.securityRecyclerView.visibility = GONE
                binding.noResultsTextView.visibility = GONE
                binding.securityListProgressBar.visibility = VISIBLE
            }

            State.Ready -> {
                binding.securityRecyclerView.visibility = VISIBLE
                binding.noResultsTextView.visibility = GONE
                binding.securityListProgressBar.visibility = GONE
            }

            State.NoResults -> {
                binding.securityRecyclerView.visibility = GONE
                binding.noResultsTextView.visibility = VISIBLE
                binding.securityListProgressBar.visibility = GONE
            }
        }
    }

    private enum class State {
        Loading,
        Ready,
        NoResults
    }
}
