package ru.kima.moex

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ru.kima.moex.databinding.ActivityMainBinding
import ru.kima.moex.moex.api.MoexResponse
import ru.kima.moex.moex.api.MoexTable
import ru.kima.moex.moex.api.RequestBody
import ru.kima.moex.networking.NetworkManager
import kotlin.system.measureTimeMillis


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        NetworkManager.getInstance(this)

        binding.updateButton.setOnClickListener() {
            loadDogImage()
        }
        binding.textView.text = mainViewModel.text
    }

    private fun loadDogImage() {

        val request = RequestBody().Engines().Engine(RequestBody.EngineType.stock).
            Markets().Market(RequestBody.MarketType.shares).
            Securities(RequestBody.OutputExtension.Json)
        NetworkManager.getInstance()
            ?.getRequest(request.body) {
                if (it.isNotEmpty()){
                    val moexResponse = MoexResponse()
                    var tables : MutableList<MoexTable>
                    val time = measureTimeMillis {
                        tables = moexResponse.ParseFromJson(it)
                    }
                    val sb = StringBuilder()
                    sb.append("It took $time ms\n")
                    for (sec in tables[1].data){
                        sb.append("${sec["SECID"]}:${ if (sec["BID"] == null) "No BID" else sec["BID"]  } - ${sec["ISSUECAPITALIZATION"]}\n")
                    }
                    mainViewModel.text = sb.toString()
                    binding.textView.text = mainViewModel.text
                }
                else {
                    binding.textView.text = "error"
                }
            }
    }
}

