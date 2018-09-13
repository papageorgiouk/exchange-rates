package com.papageorgiouk.exchangerates.ui

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.papageorgiouk.exchangerates.R
import com.papageorgiouk.exchangerates.domain.Rate
import kotlinx.android.synthetic.main.item_rate.view.*


/**
 * The adapter for the RecyclerView that will display symbols-prices
 *
 */
class RatesAdapter(val context: Context) : RecyclerView.Adapter<RatesAdapter.RateVH>() {

    var currentRates: MutableList<Rate>? = null

    /**
     * We call this when we have new data from the ViewModel. We set the new rates, we calculate
     * the price difference from the previous data, and we update the adapter to reflect this
     */
    fun loadData(newRatesData: List<Rate>?) {
        if (currentRates == null && newRatesData != null) {
            //  populate list on first load (and on unspecified errors)
            currentRates = newRatesData.toMutableList()
            notifyDataSetChanged()
        } else {
            //  we assume that the remote api is well-designed and the currency pairs remain the same
            //  for each symbol we calculate the delta of the last two rates, and then we replace the old rate with the new one
            newRatesData?.forEach {
                val newRate = it  //  for clarity's sake
                val position = currentRates!!.indexOf(newRate)  // existing symbol position
                val oldRate = currentRates!![position]  //  get the old rate
                newRate.delta = newRate.price - oldRate.price
                currentRates!![position] = newRate  //  substitute the old rate with the new one
                notifyItemChanged(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RateVH {
        return LayoutInflater.from(context)
                .inflate(R.layout.item_rate, parent, false)
                .let { RateVH(it) }
    }

    override fun getItemCount(): Int = currentRates?.size ?: 0  //  list size if not null, otherwise 0

    override fun onBindViewHolder(holder: RateVH, position: Int) {
        currentRates?.get(position)?.let {
            holder.bindRate(it)
        }
    }

    inner class RateVH(val rateView: View) : RecyclerView.ViewHolder(rateView) {

        /**
         * Bind the values to the TextViews in the ViewHolder and change the text's colour
         * according to the price delta
         */
        fun bindRate(rate: Rate) {
            rateView.apply {
                txt_symbol.text = rate.symbol
                txt_rate.text = rate.price.toString()

                //  set the rate's colour according to price change
                rate.delta?.let {
                    when {
                        it > 0 -> txt_rate.setTextColor(Color.GREEN)
                        it < 0 -> txt_rate.setTextColor(Color.RED)
                        else -> txt_rate.setTextColor(Color.GRAY)
                    }
                }
            }
        }
    }
}