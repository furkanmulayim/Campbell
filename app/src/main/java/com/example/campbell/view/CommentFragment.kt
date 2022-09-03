package com.example.campbell.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.campbell.R
import com.example.campbell.adapter.CommentRcyclerAdapter
import com.example.campbell.databinding.FragmentCommentBinding
import com.example.campbell.model.Camp
import com.example.campbell.model.Comment
import com.example.campbell.util.hideKeyboard
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage

class CommentFragment : Fragment() {

    private var campUuid: Camp? = null
    private var binding: FragmentCommentBinding? = null
    private var control: Boolean = false
    private lateinit var storage: FirebaseStorage
    private lateinit var database: FirebaseFirestore
    private lateinit var recyclerViewAdapter: CommentRcyclerAdapter
    private var basildi = ""

    private var mCommentList = ArrayList<Comment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            campUuid = CommentFragmentArgs.fromBundle(it).campListData
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCommentBinding.inflate(layoutInflater)
        val view = binding?.root
        storage = FirebaseStorage.getInstance()
        database = FirebaseFirestore.getInstance()
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

        binding?.yorumIptal?.setOnClickListener {
            youmIptalTiklaninca()
        }
        binding?.yorumGonder?.setOnClickListener {
            yorumPaylas()
        }

    }


    fun yorumEkleTiklaninca() {
        if (control == false) {
            binding?.yorumIptal?.isClickable = true
            control = true
            binding?.yorumPanel?.isVisible = true
            binding?.yorumEkle?.isVisible = false
            val anim = AnimationUtils.loadAnimation(requireContext(), R.anim.yorum_anim)
            binding?.yorumPanel?.startAnimation(anim)
            puanlama()
        }

    }

    fun youmIptalTiklaninca() {
        binding?.yorumIptal?.isClickable = false
        control = false
        binding?.yorumPanel?.isVisible = false
        binding?.yorumEkle?.isVisible = true
        val anim = AnimationUtils.loadAnimation(requireContext(), R.anim.yorum_anim_gidis)
        binding?.yorumPanel?.startAnimation(anim)
    }

    fun yorumPaylas() {


        var countryName = campUuid?.campCountryName.toString()
        var nameSurname = binding?.textAdSoyad?.text.toString()
        var mailAdress = binding?.textMailAdres?.text.toString()
        var commentable = binding?.textComment?.text.toString()
        var tarih = Timestamp.now()
        var favori = ""



        if (nameSurname != "" && mailAdress != "" && commentable != "") {

            if (mailAdress.contains("@gmail.com", false) && mailAdress.length > 17) {

                if (nameSurname.length > 5 && commentable.length > 5) {

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
                        postHashMap.put("mailAdress", mailAdress)
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
                            "Yorumunuz çok uzun",
                            Toast.LENGTH_SHORT
                        ).show()
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
                    "Lütfen geçerli Gmail adresinizi giriniz.",
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
                                val kullMail = document.get("mailAdress") as String
                                val kullComment = document.get("commentable") as String
                                val favori = document.get("favori") as String
                                val tarih = document.get("tarih") as Timestamp


                                val downloadedComment = Comment(
                                    kullAdSoyad,
                                    kullMail,
                                    kullComment,
                                    favori,
                                    tarih.toString()
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