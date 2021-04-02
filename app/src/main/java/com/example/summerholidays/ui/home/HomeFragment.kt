package com.example.summerholidays.ui.home

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rotrofitwithcoroutineexampletow.data.model.ImageItem
import com.example.summerholidays.databinding.FragmentHomeBinding
import com.example.summerholidays.utils.Util
import com.google.android.material.snackbar.Snackbar
import com.tapi.a0028speedtest.base.BaseFragment

class HomeFragment : BaseFragment(), HomeAdapter.NetworkListener, View.OnClickListener {

    private val TAG = "giangtd"
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()

    private lateinit var adapter: HomeAdapter

    private val observerListData = Observer<List<ImageItem>?> {
        it?.let {
            adapter.submitList(ArrayList(it))
//            adapter.submitList(it)
        }
    }

    private val observerLoading = Observer<Boolean> {
        if (!it) {
            binding.loading.visibility = View.GONE
        } else {
            binding.loading.visibility = View.VISIBLE
        }
    }

    private val observerExceptionNetwork = Observer<NetworkStatus> {
        when (it) {
            NetworkStatus.INTERNET_ERROR -> {
                context?.let {
                    showNotification("No Internet")
                }
            }
            NetworkStatus.NETWORK_ERROR -> {
                context?.let {
                    showNotification("Error Network")
                }
            }
            NetworkStatus.INTERNET_SUCCESSFUL -> {
                binding.tvNoInternet.visibility = View.INVISIBLE
            }
            NetworkStatus.INTERNET_ERROR_FRIST -> {
                binding.tvNoInternet.visibility = View.VISIBLE
                context?.let {
                    showNotification("No Internet")
                }
            }
        }
    }

    private val observerDownloadImageStatus = Observer<DownloadImageStatus> {
        when (it) {
            DownloadImageStatus.DOWNLOAD_SUCCESSFUL -> {
                context?.let {
                    showNotification("Download Successful")
                }
//                Toast.makeText(requireContext(), "Successful", Toast.LENGTH_SHORT).show()
            }
            DownloadImageStatus.DOWNLOAD_FAIL -> {
                context?.let {
                    showNotification("Download Error ")
                }
//                Toast.makeText(requireContext(), "Fail", Toast.LENGTH_SHORT).show()
            }
            else -> {

            }
        }
    }

    private fun showNotification(text: String) {
        view?.let {
//            Snackbar.make(it, text, Snackbar.LENGTH_LONG).show()
            Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerNetworkBroadcastForNougat()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.viewmodel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.networkData.observe(viewLifecycleOwner, observerListData)
        viewModel.exceptionNetWork.observe(viewLifecycleOwner, observerExceptionNetwork)
        viewModel.loading.observe(viewLifecycleOwner, observerLoading)
        viewModel.downloadImageStatus.observe(viewLifecycleOwner, observerDownloadImageStatus)
        inits()
        initListener()
    }

    private fun initListener() {
        binding.fabGallery.setOnClickListener(this)
    }

    private fun inits() {
        binding.rvImage.layoutManager = GridLayoutManager(context, 2, RecyclerView.VERTICAL, false)
        adapter = HomeAdapter(this)
        binding.rvImage.adapter = adapter

        initScrollListener()
    }

    // Scorll list and loadmore data
    private fun initScrollListener() {
        binding.rvImage.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val gridLayoutManager: GridLayoutManager = binding.rvImage.layoutManager as GridLayoutManager

                if (viewModel.getLoadMoreStatus()) {
                    Log.d(TAG, "onScrolled: lat position: " + gridLayoutManager.findLastCompletelyVisibleItemPosition())
                    if (gridLayoutManager.findLastCompletelyVisibleItemPosition() == viewModel.getListSize() - 1) {
                        viewModel.getData()
                    }
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun dowloadImage(item: ImageItem) {
        Log.d(TAG, "dowloadImage: item: " + item)
        viewModel.downloadImage(item)
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.fabGallery -> {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToGalleryFragment())
            }
        }
    }

    private val networkChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(contxt: Context?, intent: Intent?) {
            try {
                if (Util.isNetworkConnected(requireContext()) && viewModel.getAutoLoadData()) {
                    if (viewModel.getListSize() == 0) {
                        viewModel.getData()
                    }
                }
            } catch (e: NullPointerException) {
                e.printStackTrace()
            }
        }
    }

    private fun registerNetworkBroadcastForNougat() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context?.registerReceiver(networkChangeReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        }
    }

    protected fun unregisterNetworkChanges() {
        try {
            context?.unregisterReceiver(networkChangeReceiver)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterNetworkChanges()
    }
}