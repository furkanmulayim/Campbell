package com.example.campbell.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth


class FeedFragment : Fragment() {


    private lateinit var mAuth: FirebaseAuth
    private var binding: FragmentFeedBinding? = null
    lateinit var mGoogleSignInClient: GoogleSignInClient

    private val auth by lazy {
        FirebaseAuth.getInstance()
    }

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
        mAuth = FirebaseAuth.getInstance()
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

        /** Geçiş anmasyonlarının deklare edilmesi */
        val anim = AnimationUtils.loadAnimation(context, R.anim.items_anim)
        binding?.campList?.startAnimation(anim)

        /** oluşturulan fonksiyonlların görünüm olusturulduktan sonra kullanılması*/
        refresh()
        search()
        girisCikisIslemler()
    }

    /** Giriş Yapılmış mı kontrol ediyor*/
    private fun isSignedIn(): Boolean {
        return GoogleSignIn.getLastSignedInAccount(requireContext()) != null
    }

    /**  Feed Fragment İçin İsim Göstermek*/
    private fun userInf() {
        if (isSignedIn()) {
            val currentUser = mAuth.currentUser
            binding?.currentUserName?.text = currentUser?.displayName
            binding?.currentLoginLogout?.isVisible = false
        }

    }

    /** Giriş yapıldığı zaman on resume isle devam edeceğinden bu fonksiyonda çalıştırmak daha mantıklı*/
    override fun onResume() {
        super.onResume()
        userInf()
    }

    /** Çıkış ve giriş Yapmak için*/
    private fun girisCikisIslemler() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

        binding?.currentLoginLogout?.setOnClickListener {
            val intent = Intent(requireContext(), SignInActivity::class.java)
            startActivity(intent)
        }

        binding?.currentUserName?.setOnClickListener {
            mGoogleSignInClient.signOut().addOnCompleteListener {
                Toast.makeText(requireContext(), "Çıkış Yapıldı", Toast.LENGTH_SHORT).show()
                binding?.currentUserName?.text = ""
                binding?.currentLoginLogout?.isVisible = true

            }
        }
    }

    /** Yenilemek için kullanılan fonksiyon*/
    private fun refresh() {
        binding?.swipeRefreshLayout?.setOnRefreshListener {
            binding?.campList?.visibility = View.GONE
            viewModel.refreshFromAPI()
            binding?.swipeRefreshLayout?.isRefreshing = false
        }
    }

    /** Datalarımızı canlı ve gözlemlenebilri olması için böyle bir fonksiyon kullandık */
    private fun observeLiveData() {
        viewModel.canliVeri.observe(viewLifecycleOwner, Observer { camp ->
            camp?.let {
                binding?.campList?.visibility = View.VISIBLE
                campAdapter.updateCampList(camp)
                binding?.pbFeed?.visibility = View.GONE
            }
        })
    }

    /** Kamp yerleri içerisinde aarama yapmak için kullanılan fonksiyon*/
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