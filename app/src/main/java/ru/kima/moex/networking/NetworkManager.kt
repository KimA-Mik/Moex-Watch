package ru.kima.moex.networking

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

private const val TAG = "NetworkManager"
class NetworkManager private constructor(context: Context) {
    //for Volley API
    private var requestQueue: RequestQueue

    init {
        requestQueue = Volley.newRequestQueue(context.applicationContext)
    }

    fun getRequest(url: String, listener: MoexListener<String>) {
        val request = StringRequest(
            Request.Method.GET, url,
            { response ->
                Log.d("$TAG: ", "somePostRequest Response : $response")
                listener.getResult(response.toString())
            }
        ) { error ->
            if (null != error.networkResponse) {
                Log.d(
                    "$TAG: ",
                    "Error Response code: " + error.networkResponse.statusCode
                )
                listener.getResult(String())
            }
        }
        requestQueue.add(request)
    }

    companion object {
        private var instance: NetworkManager? = null
        @Synchronized
        fun getInstance(context: Context): NetworkManager? {
            if (null == instance) instance = NetworkManager(context)
            return instance
        }

        //this is so you don't need to pass context each time
        @Synchronized
        fun getInstance(): NetworkManager? {
            checkNotNull(instance) {
                NetworkManager::class.java.simpleName +
                        " is not initialized, call getInstance(...) first"
            }
            return instance
        }
    }
}