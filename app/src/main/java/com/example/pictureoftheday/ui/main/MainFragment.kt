//logic for screen
package com.example.pictureoftheday.ui.main

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import coil.api.load
import com.example.pictureoftheday.R
import kotlinx.android.synthetic.main.main_fragment.*


class MainFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val viewModel:MainViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        val observer = Observer<PODServerResponseData?> {serverResponse ->
            serverResponse?.let{
                serverResponse.url?.let{url->
                    image_view.load(url)
                    message.text = serverResponse.explanation
                }
            }
        }
        viewModel.getData().observe(viewLifecycleOwner, observer)
    }
    companion object {
        fun newInstance():MainFragment = MainFragment()
    }

}
