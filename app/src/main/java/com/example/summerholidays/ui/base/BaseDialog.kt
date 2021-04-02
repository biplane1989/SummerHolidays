package com.tapi.a0028speedtest.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

private const val DIALOG_STYLE_KEY = "DIALOG_STYLE_KEY"

abstract class BaseDialog : DialogFragment() {
    protected fun sendAction(actionName: String, data: Any? = null) {
        lifecycleScope.launchWhenResumed {
            getBaseActivity()?.let {
                it.sendAction(actionName, data)
            }
        }
    }

    protected fun getBaseActivity(): BaseActivity? {
        if (activity != null) {
            return activity as BaseActivity
        }
        return null
    }
}