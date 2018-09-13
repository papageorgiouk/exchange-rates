package com.papageorgiouk.exchangerates.domain

import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import java.util.Arrays

/**
 * The model for the incoming data
 *
 */
data class Rate(@Json(name = "symbol") val symbol: String,
                @Json(name = "price") val price: Double,
                var delta: Double? = null) {

    //  the object ID is the symbol, for comparison purposes
    override fun equals(other: Any?): Boolean = other is Rate && other.symbol == this.symbol

    // same with hashCode
    override fun hashCode(): Int = Arrays.hashCode(symbol.toByteArray())
}

data class RatesResponse(@Json(name = "rates") val rates: List<Rate>)

interface RatesApi {
    @GET("rates")
    fun loadRates(): Call<RatesResponse>
}

class RatesRepo {
    private val ratesApi: RatesApi

    init {
        val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

        val retrofit = Retrofit.Builder()
                .baseUrl("https://mt4-api-staging.herokuapp.com")
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()

        ratesApi = retrofit.create(RatesApi::class.java)
    }

    //  fetch currentRates data
    fun fetchRates() : Call<RatesResponse> = ratesApi.loadRates()
}