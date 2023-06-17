package ru.kima.moex.views.secdetails

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.components.YAxis
import kotlinx.coroutines.launch
import ru.kima.moex.R
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
        context?.let {
            viewModel.colorGreen = it.getColor(R.color.increasing_green)
            viewModel.colorRed = it.getColor(R.color.ev_red)
        }
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
                    viewModel.priceData.collect { price->
                        price?.let {
                            binding.priceTextView.text =
                                resources.getString(R.string.price, String.format("%.2f", it.price))
                            binding.securityNameTextView.text = it.sec_name
                        }
                    }
                }

                launch {
                    viewModel.candleData.collect { candleData ->
                        binding.candleChart.resetTracking()
                        binding.candleChart.data = candleData
                        binding.candleChart.invalidate()
                    }
                }

                launch {
                    viewModel.favorite.collect { isFavorite ->
                        if (isFavorite) {
                            binding.configButton.isEnabled = true
                            binding.favoriteButton.setBackgroundResource(R.drawable.ic_favorite)
                        } else {
                            binding.configButton.isEnabled = false
                            binding.favoriteButton.setBackgroundResource(R.drawable.ic_favorite_border)
                        }
                    }
                }

                launch {
                    viewModel.navigationEvent.collect { event ->
                        event.getValue()?.let {
                            findNavController().navigate(
                                SecurityDetailsFragmentDirections.showPriceTrackingConfig(
                                    viewModel.SecurityId,
                                    it.price.toFloat()
                                )
                            )
                        }
                    }
                }
            }
        }
        binding.secId.text = viewModel.SecurityId

        binding.candleChart.setBackgroundColor(Color.WHITE)
        binding.candleChart.description.isEnabled = false
        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        binding.candleChart.setMaxVisibleValueCount(399)
        // scaling can now only be done on x- and y-axis separately
        binding.candleChart.setPinchZoom(false)
        binding.candleChart.setDrawGridBackground(false)
        val xAxis: XAxis = binding.candleChart.xAxis
        xAxis.position = XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.valueFormatter = LineChartXAxisValueFormatter()

        xAxis.granularity = 1f
        xAxis.isGranularityEnabled = true
        xAxis.setAvoidFirstLastClipping(true)


        val leftAxis: YAxis = binding.candleChart.axisLeft
        //        leftAxis.setEnabled(false);
        leftAxis.setLabelCount(7, false)
        leftAxis.setDrawGridLines(false)
        leftAxis.setDrawAxisLine(false)

        binding.candleChart.axisRight.isEnabled = false
        binding.candleChart.legend.isEnabled = true


        binding.oneYearRadioButton.setOnClickListener { onRadioButtonClicked(it) }
        binding.sixMonthsRadioButton.setOnClickListener { onRadioButtonClicked(it) }
        binding.oneMonthRadioButton.setOnClickListener { onRadioButtonClicked(it) }
        binding.favoriteButton.setOnClickListener { viewModel.changeFavoriteStatue() }
        binding.configButton.setOnClickListener { viewModel.showConfig() }
    }

    private fun onRadioButtonClicked(view: View) {
        if (view !is RadioButton)
            return
        // Is the button now checked?
        val checked = view.isChecked

        // Check which radio button was clicked
        when (view.getId()) {
            R.id.one_year_radio_button ->
                if (checked)
                    viewModel.timeSpan = SecurityDetailsViewModel.TimeSpan.YEAR

            R.id.six_months_radio_button ->
                if (checked)
                    viewModel.timeSpan = SecurityDetailsViewModel.TimeSpan.SIX_MONTHS

            R.id.one_month_radio_button ->
                if (checked)
                    viewModel.timeSpan = SecurityDetailsViewModel.TimeSpan.ONE_MONTHS
        }
    }
}