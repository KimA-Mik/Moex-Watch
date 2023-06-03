package ru.kima.moex.views.priceConfig

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.launch
import ru.kima.moex.databinding.FragmentPriceConfigBinding
import ru.kima.moex.views.factory

class PriceConfigFragment : Fragment() {
    private var _binding: FragmentPriceConfigBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    private val viewModel: PriceConfigViewModel by viewModels { factory() }

    private val args: PriceConfigFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.updateSecId(args.secid)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPriceConfigBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.security.collect { security ->
                        binding.secId.text = security.sec_id
                        binding.seekBar.progress = security.change_percent.toInt()
                        binding.percentView.text = security.change_percent.toInt().toString() + "%"
                        binding.trackCheckBox.isChecked = security.is_tracked
                    }
                }

                launch {
                    viewModel.shouldReturn.collect {
                        it.getValue()?.let { shouldReturn ->
                            if (shouldReturn) {
                                findNavController().navigateUp()
                            }
                        }
                    }
                }
            }
        }

        binding.saveButton.setOnClickListener { viewModel.onSavePressed() }

        binding.priceEdit.setText(String.format("%.2f", args.price))
        binding.seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                binding.percentView.text = p1.toString() + "%"
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                viewModel.updatePercent(p0!!.progress)
            }
        })

        binding.trackCheckBox.setOnClickListener {
            val checkBox = it as CheckBox
            viewModel.setTracked(checkBox.isChecked)
        }
    }
}
