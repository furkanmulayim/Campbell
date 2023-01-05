package com.campbell.campbell.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.campbell.campbell.R
import com.campbell.campbell.adapter.CommentRcyclerAdapter
import com.campbell.campbell.databinding.FragmentCommentBinding
import com.campbell.campbell.model.Camp
import com.campbell.campbell.model.Comment
import com.campbell.campbell.util.hideKeyboard
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.time.*
import java.util.*
import kotlin.collections.ArrayList

class CommentFragment : Fragment() {

    private lateinit var mAuth: FirebaseAuth
    lateinit var mGoogleSignInClient: GoogleSignInClient

    private val auth by lazy {
        FirebaseAuth.getInstance()
    }

    private var campUuid: Camp? = null
    private var binding: FragmentCommentBinding? = null
    private var control: Boolean = false
    private lateinit var storage: FirebaseStorage
    private lateinit var database: FirebaseFirestore
    private lateinit var recyclerViewAdapter: CommentRcyclerAdapter
    private var basildi = ""
    private var cikisButtonKontrol = false

    private var mCommentList = ArrayList<Comment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            campUuid = CommentFragmentArgs.fromBundle(it).campListData
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCommentBinding.inflate(layoutInflater)
        val view = binding?.root
        storage = FirebaseStorage.getInstance()
        database = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()
        firebaseCommentGet()
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.campNameGeldi?.text = campUuid?.campCountryName
        binding?.campNameGeldi?.text = binding?.campNameGeldi?.text.toString()

        val anim = AnimationUtils.loadAnimation(requireContext(), R.anim.detay0)
        binding?.scrollView2?.startAnimation(anim)

        binding?.goingToCamp?.setOnClickListener {
            activity?.onBackPressed()
        }

        binding?.yorumEkle?.setOnClickListener {
            yorumEkleTiklaninca()
        }

        girisCikisIslemler()

        binding?.yorumIptal?.setOnClickListener {
            binding?.footerCll?.isVisible = true
            youmIptalTiklaninca()
        }
        binding?.yorumGonder?.setOnClickListener {
            yorumPaylas()
        }
    }

    /** Giriş yapıldığı zaman on resume isle devam edeceğinden bu fonksiyonda çalıştırmak daha mantıklı*/
    override fun onResume() {
        super.onResume()
        userInf()
    }

    /** Giriş Yapılmış mı kontrol ediyor*/
    fun isSignedIn(): Boolean {
        return GoogleSignIn.getLastSignedInAccount(requireContext()) != null
    }

    /**  Feed Fragment İçin İsim Göstermek*/
    fun userInf() {
        if (isSignedIn()) {
            val currentUser = mAuth.currentUser
            binding?.googleButton?.isVisible = false
            binding?.googleCikis?.isVisible = true
        } else {
            binding?.googleButton?.isVisible = true
            binding?.googleCikis?.isVisible = false
        }

    }

    /** Çıkış ve giriş Yapmak için*/
    private fun girisCikisIslemler() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

        binding?.googleButton?.setOnClickListener {
            val intent = Intent(requireContext(), SignInActivity::class.java)
            startActivity(intent)
        }

        binding?.googleCikis?.setOnClickListener {
            mGoogleSignInClient.signOut().addOnCompleteListener {
                binding?.googleButton?.isVisible = true
                binding?.googleCikis?.isVisible = false
                Toast.makeText(requireContext(), "Çıkış Yapıldı", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun yorumEkleTiklaninca() {

        if (binding?.googleCikis?.isVisible == true) {


            val currentUser = mAuth.currentUser
            binding?.textAdSoyad?.setText(currentUser?.displayName)

            binding?.footerCll?.isVisible = false

            binding?.yorumPanel?.setOnClickListener {
                youmIptalTiklaninca()
            }

            if (control == false) {
                binding?.yorumIptal?.isClickable = true
                control = true
                binding?.sayfaGecisAlani?.isVisible = false
                binding?.yorumPanel?.isVisible = true
                binding?.yorumEkle?.isVisible = false

                binding?.googleButton?.isVisible = false
                val anim = AnimationUtils.loadAnimation(requireContext(), R.anim.yorum_anim)
                binding?.yorumPanel?.startAnimation(anim)
                puanlama()
            }
        } else {
            Toast.makeText(
                requireContext(),
                "Lütfen Yorum Yapabilmek İçin Mail Hesabınız İle Giriş Yapınız",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun youmIptalTiklaninca() {
        binding?.yorumIptal?.isClickable = false
        control = false
        binding?.footerCll?.isVisible = true
        binding?.sayfaGecisAlani?.isVisible = true
        binding?.yorumPanel?.isVisible = false
        binding?.yorumEkle?.isVisible = true
        binding?.googleButton?.isVisible = binding?.googleCikis?.isVisible != true

        val anim = AnimationUtils.loadAnimation(requireContext(), R.anim.yorum_anim_gidis)
        binding?.yorumPanel?.startAnimation(anim)
    }

    fun yorumPaylas() {

        var countryName = campUuid?.campCountryName.toString()
        var nameSurname = binding?.textAdSoyad?.text.toString()
        var commentable = binding?.textComment?.text.toString()
        var tarih = Timestamp.now()
        var favori = ""

        val sdf = SimpleDateFormat(" dd MMM HH:mm", Locale.getDefault())
        var sonTarih = sdf.format(Date((tarih?.seconds ?: 0) * 1000))


        if (nameSurname != "" && commentable != "") {

            if (commentable.length < 100) {

                binding?.yorumGonder?.isClickable = false

                if (basildi == "bir") {
                    favori = "1"
                } else if (basildi == "iki") {
                    favori = "2"
                } else if (basildi == "uc") {
                    favori = "3"
                } else if (basildi == "dort") {
                    favori = "4"
                } else if (basildi == "bes") {
                    favori = "5"
                }

                val postHashMap = hashMapOf<String, Any>()
                postHashMap.put("nameSurname", nameSurname)
                postHashMap.put("commentable", commentable)
                postHashMap.put("favori", favori)
                postHashMap.put("tarih", tarih)

                database.collection("$countryName").add(postHashMap)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(
                                requireContext(),
                                "Yorumunuz başarı ile gönderilmiştir.",
                                Toast.LENGTH_SHORT
                            ).show()
                            binding?.yorumGonder?.isClickable = true
                            activity?.hideKeyboard()
                            youmIptalTiklaninca()
                        }
                    }.addOnFailureListener { ex ->
                        Toast.makeText(
                            requireContext(), ex.localizedMessage, Toast.LENGTH_SHORT
                        ).show()
                        binding?.yorumGonder?.isClickable = true
                    }
            } else {
                Toast.makeText(
                    requireContext(),
                    "Yorumunuz veya Adınız Soyadınız çok kısa",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            Toast.makeText(
                requireContext(),
                "Lütfen tüm boş alanları doldurunuz.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("NotifyDataSetChanged")
    fun firebaseCommentGet() {

        var countryName = campUuid?.campCountryName.toString()
        database.collection("$countryName").orderBy("tarih", Query.Direction.DESCENDING)
            .addSnapshotListener { snspt, ex ->
                if (ex != null) {
                    Toast.makeText(requireContext(), ex.localizedMessage, Toast.LENGTH_SHORT).show()
                } else {

                    if (snspt != null) {
                        if (!snspt.isEmpty) {

                            var commentList = snspt.documents
                            mCommentList.clear()

                            for (document in commentList) {

                                val kullAdSoyad = document.get("nameSurname") as String
                                val kullComment = document.get("commentable") as String
                                val favori = document.get("favori") as String
                                val tarih = document.get("tarih") as Timestamp

                                val sdf = SimpleDateFormat(" dd MMM HH:mm", Locale.getDefault())
                                var sonTarih = sdf.format(Date((tarih?.seconds ?: 0) * 1000))

                                val downloadedComment = Comment(
                                    kullAdSoyad,
                                    kullComment,
                                    favori,
                                    sonTarih.toString()
                                )

                                mCommentList.add(downloadedComment)
                            }

                            val layoutManager = LinearLayoutManager(context)
                            binding?.commentList?.layoutManager = layoutManager
                            recyclerViewAdapter = CommentRcyclerAdapter(mCommentList)
                            binding?.commentList?.adapter = recyclerViewAdapter
                            recyclerViewAdapter.notifyDataSetChanged()

                        }
                    }

                }

            }
    }

    fun puanlama() {

        binding?.heartOne?.setOnClickListener {
            basildi = "bir"
            binding?.heartOne?.setBackgroundResource(R.drawable.ic_heart)
            binding?.heartTwo?.setBackgroundResource(R.drawable.ic_heart_gray)
            binding?.heartThree?.setBackgroundResource(R.drawable.ic_heart_gray)
            binding?.heartFour?.setBackgroundResource(R.drawable.ic_heart_gray)
            binding?.heartFive?.setBackgroundResource(R.drawable.ic_heart_gray)
        }

        binding?.heartTwo?.setOnClickListener {
            basildi = "iki"
            binding?.heartOne?.setBackgroundResource(R.drawable.ic_heart)
            binding?.heartTwo?.setBackgroundResource(R.drawable.ic_heart)
            binding?.heartThree?.setBackgroundResource(R.drawable.ic_heart_gray)
            binding?.heartFour?.setBackgroundResource(R.drawable.ic_heart_gray)
            binding?.heartFive?.setBackgroundResource(R.drawable.ic_heart_gray)
        }

        binding?.heartThree?.setOnClickListener {
            basildi = "uc"
            binding?.heartOne?.setBackgroundResource(R.drawable.ic_heart)
            binding?.heartTwo?.setBackgroundResource(R.drawable.ic_heart)
            binding?.heartThree?.setBackgroundResource(R.drawable.ic_heart)
            binding?.heartFour?.setBackgroundResource(R.drawable.ic_heart_gray)
            binding?.heartFive?.setBackgroundResource(R.drawable.ic_heart_gray)
        }

        binding?.heartFour?.setOnClickListener {
            basildi = "dort"
            binding?.heartOne?.setBackgroundResource(R.drawable.ic_heart)
            binding?.heartTwo?.setBackgroundResource(R.drawable.ic_heart)
            binding?.heartThree?.setBackgroundResource(R.drawable.ic_heart)
            binding?.heartFour?.setBackgroundResource(R.drawable.ic_heart)
            binding?.heartFive?.setBackgroundResource(R.drawable.ic_heart_gray)
        }

        binding?.heartFive?.setOnClickListener {
            basildi = "bes"
            binding?.heartOne?.setBackgroundResource(R.drawable.ic_heart)
            binding?.heartTwo?.setBackgroundResource(R.drawable.ic_heart)
            binding?.heartThree?.setBackgroundResource(R.drawable.ic_heart)
            binding?.heartFour?.setBackgroundResource(R.drawable.ic_heart)
            binding?.heartFive?.setBackgroundResource(R.drawable.ic_heart)
        }

    }

}