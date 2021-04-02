package com.example.summerholidays.ui.details

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.example.summerholidays.R
import com.example.summerholidays.databinding.FragmentDetailBinding
import com.example.summerholidays.network.model.ImageFile
import com.example.summerholidays.utils.Constance
import com.example.summerholidays.utils.ImageFileManager
import com.google.android.material.snackbar.Snackbar
import com.tapi.a0028speedtest.base.BaseFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class DetailFragment : BaseFragment(), DetailAdapter.DetailListener, View.OnClickListener {

    val TAG = "giangtd"
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private val viewmodel: DetailViewModel by viewModels()
    private lateinit var adapter: DetailAdapter
    private var selectPositionImage = 0
    private var firtSelectStatus = true

    val args: DetailFragmentArgs by navArgs()

    private val imageFilesObserver = Observer<List<ImageFile>>() {
        it?.let {
            adapter.submitList(it)
            binding.pbLoading.visibility = View.INVISIBLE
        }
    }

    private val notificationDeleteObserver = Observer<DetailNotification> {
        when (it) {
            DetailNotification.DELETE_SUCCESSFUL -> {
                context?.let {
                    showNotification("Delete all Successful")
                }
            }
            DetailNotification.DELETE_FAIL -> {
                context?.let {
                    showNotification("Delete all Fail")
                }
            }
            DetailNotification.SET_PHOTO_SCREEN_SUCCESSFUL -> {
                context?.let {
                    showNotification("Set photo screen successful")
                }
            }
            DetailNotification.SET_PHOTO_SCREEN_FAIL -> {
                context?.let {
                    showNotification("Set photo screen fail")
                }
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
        _binding = FragmentDetailBinding.inflate(inflater, container, false)

        binding.viewmodel = viewmodel
        binding.lifecycleOwner = viewLifecycleOwner
        requireActivity().windowManager
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        inits()

        viewmodel.listImage.observe(viewLifecycleOwner, imageFilesObserver)
        viewmodel.notificationDeleteAll.observe(viewLifecycleOwner, notificationDeleteObserver)

        binding.vpImage.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                selectPositionImage = position
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
            }
        })
    }

    private fun inits() {
        adapter = DetailAdapter(this)
        binding.vpImage.adapter = adapter

        binding.delete.setOnClickListener(this)
        binding.back.setOnClickListener(this)
        binding.tvEmpty.setOnClickListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClickedItem(item: ImageFile) {
        // on click item
    }

    override fun selectPosition() {
        if (firtSelectStatus) {
            binding.vpImage.post {
                binding.vpImage.setCurrentItem(args.selectPosition, false)
            }

            firtSelectStatus = false
            Log.d(TAG, "selectPosition: ssss")
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onClick(v: View?) {
        when (v) {
            binding.delete -> {
                val popup = android.widget.PopupMenu(context, v)
                popup.inflate(R.menu.detail_menu)
                popup.setOnMenuItemClickListener { item: MenuItem? ->
                    item?.let {
                        when (item.itemId) {
                            R.id.delete -> {
                                ImageFileManager.checkDeletedialog(lifecycleScope, requireContext(), viewmodel.getUriByPosition(selectPositionImage), viewmodel, selectPositionImage)
                            }
                            R.id.set_photo_screen -> {
                                viewmodel.setBackgroundScreen(selectPositionImage, requireActivity())
                            }
                            else -> {
                                // no thing
                            }
                        }
                    }
                    true
                }
                popup.show()


//                ImageFileManager.checkDeletedialog(lifecycleScope, requireContext(), viewmodel.getUriByPosition(selectPositionImage), viewmodel, selectPositionImage)
            }
            binding.back -> {
                context?.let {
                    requireActivity().onBackPressed()
                }
            }
            binding.tvEmpty -> {
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
                ImageFileManager.deleteImageAndroidQ(requireContext(), viewmodel.getUriByPosition(selectPositionImage))
            }
        }
    }


}