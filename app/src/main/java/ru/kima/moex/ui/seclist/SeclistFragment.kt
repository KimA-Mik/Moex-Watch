package ru.kima.moex.ui.seclist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import ru.kima.moex.data.Security
import ru.kima.moex.databinding.FragmentSecuritiesListBinding
import ru.kima.moex.moex.api.MoexResponse
import ru.kima.moex.moex.api.RequestBody
import ru.kima.moex.networking.NetworkManager

class SeclistFragment : Fragment() {

    private var _binding: FragmentSecuritiesListBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    private val seclistViewModel: SeclistViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSecuritiesListBinding.inflate(layoutInflater, container, false)
        binding.securityRecyclerView.layoutManager = LinearLayoutManager(context)
        val securities = seclistViewModel.securities
        val adapter = SeclistaAdapter(securities)
        binding.securityRecyclerView.adapter = adapter

        binding.updateButton.setOnClickListener() {
            loadData()
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun loadData() {
        binding.updateButton.isActivated = false
        val request = RequestBody().Engines().Engine(RequestBody.EngineType.stock).Markets()
            .Market(RequestBody.MarketType.shares).Securities(RequestBody.OutputExtension.Json)
        NetworkManager.getInstance()
            ?.getRequest(request.body) {
                if (it.isNotEmpty()) {
                    val moexResponse = MoexResponse()
                    val tables =  moexResponse.ParseFromJson(it)


                    if (tables[0].data.size != tables[1].data.size){
                        Toast.makeText(activity, "There are some error",
                        Toast.LENGTH_LONG).show()
                        return@getRequest
                    }
                    val count = tables[0].data.size
                    for (i in tables[0].data.indices){
                        val security = Security(
                            SECID = tables[0].data[i]["SECID"].toString(),
                            SECNAME = tables[0].data[i]["SECNAME"].toString(),
                            WAPRICE = tables[1].data[i]["WAPRICE"] as Double
                        )
                        seclistViewModel.securities += security

                    }
                    binding.securityRecyclerView.adapter?.notifyDataSetChanged()
                    Toast.makeText(activity, "$count securities was loaded",
                        Toast.LENGTH_LONG).show()
                    binding.updateButton.isVisible = false

                } else {
                    Toast.makeText(activity, "Request error",
                        Toast.LENGTH_LONG).show()
                }
            }
        binding.updateButton.isActivated = true
    }
}