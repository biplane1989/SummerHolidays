package com.example.summerholidays.ui.gallery

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.summerholidays.R
import com.example.summerholidays.databinding.FragmentGalleryBinding
import com.example.summerholidays.databinding.FragmentHomeBinding
import com.example.summerholidays.network.model.ImageFile
import com.example.summerholidays.ui.home.DownloadImageStatus
import com.example.summerholidays.ui.home.HomeAdapter
import com.example.summerholidays.ui.home.HomeViewModel
import com.example.summerholidays.utils.Constance
import com.example.summerholidays.utils.ImageFileManager
import com.google.android.material.snackbar.Snackbar
import com.tapi.a0028speedtest.base.BaseFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class GalleryFragment : BaseFragment(), GalleryAdapter.GalleryListener, View.OnClickListener {

    val TAG = "giangtd"
    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!
    private val viewmodel: GalleryViewModel by viewModels()
    private lateinit var adapter: GalleryAdapter

    private val imageFilesObserver = Observer<List<ImageFile>>() {
        Log.d(TAG, "list gallery size: " + it.size)
        it?.let {
            adapter.submitList(it)
        }
    }

    private val loadingsObserver = Observer<Boolean>() {
        if (it) {
            binding.pbLoading.visibility = View.VISIBLE
        } else {
            binding.pbLoading.visibility = View.INVISIBLE
        }
    }

    private val notificationDeleteObserver = Observer<Boolean> {
        if (it) {
            context?.let {
                showNotification("Delete all Successful")
            }
        } else {
            context?.let {
                showNotification("Delete all Fail")
            }
        }
    }

    private val observerDownloadImageStatus = Observer<DownloadImageStatus> {
        when (it) {
            DownloadImageStatus.DOWNLOAD_SUCCESSFUL -> {
                context?.let {
                    showNotification("Download Successful")
                }
            }
            DownloadImageStatus.DOWNLOAD_FAIL -> {
                context?.let {
                    showNotification("Download Error ")
                }
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)

        binding.viewmodel = viewmodel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewmodel.listImage.observe(viewLifecycleOwner, imageFilesObserver)
        viewmodel.loading.observe(viewLifecycleOwner, loadingsObserver)
        viewmodel.notificationDeleteAll.observe(viewLifecycleOwner, notificationDeleteObserver)

        viewmodel.downloadImageStatus.observe(viewLifecycleOwner, observerDownloadImageStatus)
        inits()
    }

    private fun inits() {
        binding.rvImage.layoutManager = GridLayoutManager(context, 2, RecyclerView.VERTICAL, false)
        adapter = GalleryAdapter(this)
        binding.rvImage.adapter = adapter

        binding.deleteAll.setOnClickListener(this)
        binding.back.setOnClickListener(this)
        binding.tvEmpty.setOnClickListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClickedItem(item: ImageFile, position: Int) {
        val action = GalleryFragmentDirections.actionGalleryFragmentToDetailFragment(position)
        findNavController().navigate(action)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onClick(v: View?) {
        when (v) {
            binding.deleteAll -> {
                val popup = android.widget.PopupMenu(context, v)
                popup.inflate(R.menu.gallery_menu)
                popup.setOnMenuItemClickListener { item: MenuItem? ->
                    item?.let {
                        when (item.itemId) {
                            R.id.delete_all -> {
                                ImageFileManager.checkDeleteAlldialog(lifecycleScope, requireContext(), viewmodel.getListUrl(), viewmodel)
                            }
                            else -> {
                                // no thing
                            }
                        }
                    }
                    true
                }
                popup.show()
            }
            binding.back -> {
                context?.let {
                    requireActivity().onBackPressed()
                }
            }
            binding.tvEmpty ->{
                context?.let {
                    requireActivity().onBackPressed()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == Constance.DELETE_PERMISSION_REQUEST) {
            lifecycleScope.launch(Dispatchers.Default) {
                val urls = viewmodel.getListUrl()
                for (item in urls) {
                    ImageFileManager.deleteImageAndroidQ(requireContext(), item)
                }
            }
        }
    }
}