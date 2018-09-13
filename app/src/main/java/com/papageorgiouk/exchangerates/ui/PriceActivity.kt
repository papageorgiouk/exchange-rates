package com.papageorgiouk.exchangerates.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.papageorgiouk.exchangerates.R
import com.papageorgiouk.exchangerates.domain.RatesViewModel
import kotlinx.android.synthetic.main.activity_price.*

class PriceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_price)

        //  set up the RecyclerView and its adapter
        val adapter = RatesAdapter(this)
        recycler.apply {
            layoutManager = LinearLayoutManager(
                    this@PriceActivity,
                    LinearLayoutManager.VERTICAL,
                    false
                    )

            setAdapter(adapter)
        }

        //  init the ViewModel
        val rates = ViewModelProviders.of(this)
                .get(RatesViewModel::class.java)

        //  ...and load the data
        rates.loadRates().observe(this, Observer {
            it?.ratesData?.let { adapter.loadData(it) }
            it?.error?.let { showError(it) }
        })
    }

    private fun showError(error: Throwable) {
        Snackbar.make(this.recycler, "An error occured", Snackbar.LENGTH_LONG).show()
        Log.e("Data error", error.message)
    }
}
