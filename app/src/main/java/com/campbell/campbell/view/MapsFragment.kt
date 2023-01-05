package com.campbell.campbell.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProviders
import com.campbell.campbell.R
import com.campbell.campbell.adapter.CampAdapter
import com.campbell.campbell.model.Camp
import com.campbell.campbell.wiemodel.CampViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsFragment : Fragment() {

    private lateinit var viewModel: CampViewModel

    private var enlem: Double = 0.0
    private var boylam: Double = 0.0
    private var adi: String = ""
    private var campUuid: Camp? = null

    private var mMap: GoogleMap? = null


    private val callback = OnMapReadyCallback { googleMap ->
        mMap = googleMap

        viewModel = ViewModelProviders.of(this).get(CampViewModel::class.java)

        val marker = LatLng(enlem, boylam)
        googleMap.addMarker(MarkerOptions().position(marker).title("${adi}"))
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker, 13f))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            campUuid = CampFragmentArgs.fromBundle(it).campListData
        }

        enlem = campUuid?.enlem?.toDouble() ?: 0.0
        boylam = campUuid?.boylam?.toDouble() ?: 0.0
        adi = campUuid?.campCountryName.toString()

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

    }


}