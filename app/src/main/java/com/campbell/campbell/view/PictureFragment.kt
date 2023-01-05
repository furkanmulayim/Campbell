package com.campbell.campbell.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.campbell.campbell.R
import com.campbell.campbell.databinding.FragmentPictureBinding
import com.campbell.campbell.model.Camp
import com.campbell.campbell.wiemodel.CampViewModel


class PictureFragment : Fragment() {

    private lateinit var viewModel: CampViewModel
    private var campUuid: Camp? = null
    private var binding: FragmentPictureBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val v: View = inflater.inflate(R.layout.fragment_picture, container, false)

        arguments?.let {
            campUuid = CampFragmentArgs.fromBundle(it).campListData
        }
        viewModel = ViewModelProviders.of(this).get(CampViewModel::class.java)
        var sehrAdi = campUuid?.campCountryName.toString()

        var link =
            "https://www.google.com/search?q=${sehrAdi}&tbm=isch&ved=2ahUKEwib69Sg3uL5AhXN_aQKHXilDGoQ2-cCegQIABAA&oq=gorseller&gs_lcp=CgNpbWcQAzIFCAAQgAQyBQgAEIAEMgUIABCABDIFCAAQgAQyBQgAEIAEMgYIABAeEAUyBggAEB4QBTIGCAAQHhAFMgYIABAeEAUyBggAEB4QBToHCAAQsQMQQzoECAAQQzoICAAQgAQQsQM6CwgAEIAEELEDEIMBUM2_Ali7yAJg88kCaAFwAHgBgAHEAogB-w2SAQcwLjcuMS4xmAEAoAEBqgELZ3dzLXdpei1pbWfAAQE&sclient=img&ei=2s4HY5uWHc37kwX4yrLQBg&bih=714&biw=1490&hl=tr"


        var mWebView = v.findViewById<View>(R.id.gorselimiz) as WebView
        mWebView.loadUrl("$link")
        val webSettings = mWebView.settings
        webSettings.javaScriptEnabled = true

        mWebView.webViewClient = WebViewClient()

        return v


    }


}