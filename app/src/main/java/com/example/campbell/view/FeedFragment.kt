package com.example.campbell.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.campbell.R
import com.example.campbell.adapter.CampAdapter
import com.example.campbell.databinding.FragmentFeedBinding
import com.example.campbell.model.Camp
import com.example.campbell.wiemodel.FeedViewModel

class FeedFragment : Fragment() {

    private var binding: FragmentFeedBinding? = null

    private lateinit var viewModel: FeedViewModel
    private val campAdapter = CampAdapter(arrayListOf())
    private var campUuid: Camp? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFeedBinding.inflate(layoutInflater)


        binding?.pbFeed?.visibility = View.VISIBLE
        arguments?.let {
            campUuid = CampFragmentArgs.fromBundle(it).campListData
        }

        viewModel = ViewModelProviders.of(this).get(FeedViewModel::class.java)
        viewModel.refreshData()

        observeLiveData()

        binding?.campList?.layoutManager = LinearLayoutManager(context)
        binding?.campList?.adapter = campAdapter

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.swipeRefreshLayout?.setOnRefreshListener {
           // binding?.campList?.visibility = View.GONE
            viewModel.refreshFromAPI()
            binding?.swipeRefreshLayout?.isRefreshing = false
        }

        val anim = AnimationUtils.loadAnimation(context, R.anim.items_anim)
        binding?.campList?.startAnimation(anim)
    }

    fun observeLiveData() {
        viewModel.canliVeri.observe(viewLifecycleOwner, Observer { camp ->
            camp?.let {
                binding?.campList?.visibility = View.VISIBLE
                campAdapter.updateCampList(camp)
                binding?.pbFeed?.visibility = View.GONE
            }
        })
    }

}