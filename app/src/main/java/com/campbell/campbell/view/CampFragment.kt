package com.campbell.campbell.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.campbell.campbell.R
import com.campbell.campbell.databinding.FragmentCampBinding
import com.campbell.campbell.model.Camp
import com.campbell.campbell.util.downFromUrl
import com.campbell.campbell.wiemodel.CampViewModel


class CampFragment : Fragment() {

    private lateinit var viewModel: CampViewModel
    private var campUuid: Camp? = null
    private var binding: FragmentCampBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCampBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val anim  = AnimationUtils.loadAnimation(requireContext(),R.anim.detay0)
        binding?.scrrollview?.startAnimation(anim)

        arguments?.let {
            campUuid = CampFragmentArgs.fromBundle(it).campListData
        }
        viewModel = ViewModelProviders.of(this).get(CampViewModel::class.java)
        viewModel.roomVerisiniAl(campUuid?.uuid ?: 0)

        observeLiveData()

        binding?.goingToMap?.setOnClickListener {
            val go = CampFragmentDirections.actionCampFragmentToMapsFragment(campUuid)
            Navigation.findNavController(it).navigate(go)
        }

        binding?.goingToComment?.setOnClickListener {
            val go = CampFragmentDirections.actionCampFragmentToCommentFragment(campUuid)
            Navigation.findNavController(it).navigate(go)
        }

        binding?.goToGoogle?.setOnClickListener {
            val go = CampFragmentDirections.actionCampFragmentToPictureFragment(campUuid)
            Navigation.findNavController(it).navigate(go)
        }
    }

    fun observeLiveData() {

        viewModel.campLiveData.observe(viewLifecycleOwner) { camp ->
            camp?.let {
                binding?.countryName?.text = it.campCountryName
                binding?.countryComment?.text = it.campAciklamasi
                binding?.countryUlasim?.text = it.ulasim
                binding?.countryOlanak?.text = it.olanak
                context?.let {
                    binding?.countryImage?.downFromUrl(camp.pngUrl, CircularProgressDrawable(it))
                }
            }
        }
    }

}