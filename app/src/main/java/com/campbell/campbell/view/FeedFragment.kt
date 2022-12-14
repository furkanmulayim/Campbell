package com.campbell.campbell.view

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
import com.campbell.campbell.R
import com.campbell.campbell.adapter.CampAdapter
import com.campbell.campbell.databinding.FragmentFeedBinding
import com.campbell.campbell.model.Camp
import com.campbell.campbell.util.hideKeyboard
import com.campbell.campbell.wiemodel.FeedViewModel
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
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

        MobileAds.initialize(requireContext()){
        }
        reklamgoster()

        mAuth = FirebaseAuth.getInstance()
        binding?.pbFeed?.visibility = View.VISIBLE
        arguments?.let {
            campUuid = CampFragmentArgs.fromBundle(it).campListData
        }


        viewModel = ViewModelProviders.of(this)[FeedViewModel::class.java]
        viewModel.refreshData()
        observeLiveData()

        binding?.campList?.layoutManager = LinearLayoutManager(context)
        binding?.campList?.adapter = campAdapter

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /** Ge??i?? anmasyonlar??n??n deklare edilmesi */
        val anim = AnimationUtils.loadAnimation(context, R.anim.items_anim)
        binding?.campList?.startAnimation(anim)

        /** olu??turulan fonksiyonllar??n g??r??n??m olusturulduktan sonra kullan??lmas??*/
        refresh()
        search()
        girisCikisIslemler()
    }

    /** Giri?? Yap??lm???? m?? kontrol ediyor*/
    private fun isSignedIn(): Boolean {
        return GoogleSignIn.getLastSignedInAccount(requireContext()) != null
    }

    /**  Feed Fragment ????in ??sim G??stermek*/
    private fun userInf() {
        if (isSignedIn()) {
            val currentUser = mAuth.currentUser
            binding?.currentUserName?.text = currentUser?.displayName
            binding?.currentLoginLogout?.isVisible = false
        }
    }

    /** Giri?? yap??ld?????? zaman on resume isle devam edece??inden bu fonksiyonda ??al????t??rmak daha mant??kl??*/
    override fun onResume() {
        super.onResume()
        userInf()
    }

    fun reklamgoster(){
        val adRequest = AdRequest.Builder().build()
        binding?.adView?.loadAd(adRequest)
    }

    /** ????k???? ve giri?? Yapmak i??in*/
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
                Toast.makeText(requireContext(), "????k???? Yap??ld??", Toast.LENGTH_SHORT).show()
                binding?.currentUserName?.text = ""
                binding?.currentLoginLogout?.isVisible = true
            }
        }
    }

    /** Yenilemek i??in kullan??lan fonksiyon*/
    private fun refresh() {
        binding?.swipeRefreshLayout?.setOnRefreshListener {
            binding?.campList?.visibility = View.GONE
            viewModel.refreshFromAPI()
            binding?.swipeRefreshLayout?.isRefreshing = false
        }
    }

    /** Datalar??m??z?? canl?? ve g??zlemlenebilri olmas?? i??in b??yle bir fonksiyon kulland??k */
    private fun observeLiveData() {

        viewModel.canliVeri.observe(viewLifecycleOwner, Observer { camp ->
            camp?.let {
                binding?.campList?.visibility = View.VISIBLE
                campAdapter.updateCampList(camp)
                binding?.pbFeed?.visibility = View.GONE
            }
        })
    }

    /** Kamp yerleri i??erisinde aarama yapmak i??in kullan??lan fonksiyon*/
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