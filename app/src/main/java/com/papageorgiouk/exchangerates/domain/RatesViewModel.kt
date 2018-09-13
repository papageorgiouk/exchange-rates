package com.papageorgiouk.exchangerates.domain

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.os.Handler
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Holds either the list of currentRates, or the callback error
 */
data class RatesData(val ratesData: List<Rate>?, val error: Throwable?)


/**
 * The ViewModel for the data
 */
class RatesViewModel : ViewModel() {

    companion object {
        const val MILLIS: Long = 10000  // interval in milliseconds
    }

    private val ratesCall = RatesRepo().fetchRates()

    val handler: Handler = Handler()
    val liveData = MutableLiveData<RatesData?>()

    init {
        //  cancel handler's callbacks just in case
        handler.removeCallbacksAndMessages(null)
    }


    /**
     * We tell the handler to execute the call
     */
    fun loadRates(): LiveData<RatesData?> {

        handler.post(doFetch())

        return liveData
    }

    /**
     * We wrap our code in a runnable so we can call it repeatedly from the handler
     */
    fun doFetch(): Runnable {

        return Runnable {
            ratesCall.clone().enqueue(object : Callback<RatesResponse?> {
                override fun onFailure(call: Call<RatesResponse?>?, t: Throwable?) {
                    liveData.postValue(RatesData(null, t))
                    handler.postDelayed(doFetch(), MILLIS)
                }

                override fun onResponse(call: Call<RatesResponse?>?, response: Response<RatesResponse?>?) {
                    val ratesResponse: RatesResponse? = response?.body()
                    liveData.postValue(RatesData(ratesResponse?.rates?.sortedBy { it.symbol }, null))
                    handler.postDelayed(doFetch(), MILLIS)
                }
            })
        }
    }

    /**
     * Cancel polling when the ViewModel isn't in use anymore
     */
    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacksAndMessages(null)
    }
}