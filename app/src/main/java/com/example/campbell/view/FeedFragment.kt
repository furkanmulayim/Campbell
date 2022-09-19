package com.example.campbell.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.campbell.R
import com.example.campbell.adapter.CampAdapter
import com.example.campbell.databinding.FragmentFeedBinding
import com.example.campbell.model.Camp
import com.example.campbell.util.hideKeyboard
import com.example.campbell.wiemodel.FeedViewModel
import java.util.*
import kotlin.collections.ArrayList

class FeedFragment : Fragment() {

    private var binding: FragmentFeedBinding? = null

    private lateinit var viewModel: FeedViewModel
    private val campAdapter = CampAdapter(arrayListOf())
    private var campUuid: Camp? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        val anim = AnimationUtils.loadAnimation(context, R.anim.items_anim)
        binding?.campList?.startAnimation(anim)

        refresh()
        search()
    }

    private fun refresh() {
        binding?.swipeRefreshLayout?.setOnRefreshListener {
            binding?.campList?.visibility = View.GONE
            viewModel.refreshFromAPI()
            binding?.swipeRefreshLayout?.isRefreshing = false
        }
    }

    private fun observeLiveData() {
        viewModel.canliVeri.observe(viewLifecycleOwner, Observer { camp ->
            camp?.let {
                binding?.campList?.visibility = View.VISIBLE
                campAdapter.updateCampList(camp)
                binding?.pbFeed?.visibility = View.GONE
            }
        })
    }

    private fun search() {

        binding?.search?.addTextChangedListener {
            if (binding?.search?.text.toString().equals("")) {
                observeLiveData()
            } else {
                binding?.searchButton?.setOnClickListener {
                    hideKeyboard()
                }
                var arama: String = binding?.search?.text.toString()

                viewModel.canliVeri.observe(viewLifecycleOwner, Observer { camp ->
                    camp?.let {
                        binding?.campList?.visibility = View.VISIBLE

                        var myList: List<Camp>
                        myList = camp.map { it }

                        myList.filter { it.campCountryName.toString().contains("$arama", true) }
                            ?.let { it1 -> campAdapter.updateCampList(it1) }
                        binding?.pbFeed?.visibility = View.GONE
                    }
                })
            }
        }
    }

}