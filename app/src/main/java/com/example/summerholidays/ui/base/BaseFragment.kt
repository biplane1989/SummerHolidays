package com.tapi.a0028speedtest.base

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.lifecycleScope

abstract class BaseFragment : Fragment() {
    private var isWaitingForShowScreen: Boolean = false
    internal open fun onActionReceived(actionName: String, data: Any?): Boolean {
        return false
    }


    protected fun sendAction(actionName: String, data: Any? = null) {
        lifecycleScope.launchWhenResumed {
            getBaseActivity()?.sendAction(actionName, data)
        }
    }

    private fun getBaseActivity(): BaseActivity? {
        if (activity != null) {
            return activity as BaseActivity
        }
        return null
    }

    fun showSingleOverlayScreen(baseDialog: BaseDialog, tag: String? = null) {
        if (isWaitingForShowScreen) {
            return
        }
        isWaitingForShowScreen = true
        baseDialog.show(childFragmentManager, tag)
        baseDialog.lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDialogDismissed() {
                isWaitingForShowScreen = false

            }
        })
    }

    override fun onPause() {
        super.onPause()
        isWaitingForShowScreen = false
    }
}