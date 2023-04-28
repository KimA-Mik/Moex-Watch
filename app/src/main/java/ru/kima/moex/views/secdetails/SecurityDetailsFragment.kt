package ru.kima.moex.views.secdetails

import android.content.Context
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.launch
import ru.kima.moex.databinding.FragmentSecurityDetailBinding
import ru.kima.moex.views.factory

class SecurityDetailsFragment : Fragment() {
    private var _binding: FragmentSecurityDetailBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    private val args: SecurityDetailsFragmentArgs by navArgs()

    private val viewModel: SecurityDetailsViewModel by viewModels { factory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.SecurityId = args.securityId
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSecurityDetailBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback = object : OnBackPressedCallback(
            true // default to enabled
        ) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher
            .addCallback(this, callback)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    // TODO: remove placeholder
                    viewModel.priceData.collect { priceList ->
                        if (priceList.isEmpty())
                            return@collect
                        val sb = StringBuilder()
                        for (day in priceList) {
                            sb.append(
                                "${
                                    DateFormat.format(
                                        "yyyy-MM-dd",
                                        day.date
                                    )
                                } - ${day.price}\n"
                            )
                        }
                        binding.textView.text = sb.toString()
                    }
                }
            }
        }
        binding.secId.text = viewModel.SecurityId
    }
}