package nl.rvbsoftdev.curiosityreporting.global

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.google.firebase.analytics.FirebaseAnalytics

/** BaseFragment from which all fragments inherit, set up databinding and lifecycleowner **/

abstract class BaseFragment<B : ViewDataBinding>(val lockPortraitOrientation: Boolean = true) : Fragment() {

    protected lateinit var binding: B
    @get:LayoutRes
    protected abstract val layout: Int
    protected abstract val firebaseTag: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate<B>(inflater, layout, container, false).also {
            it.lifecycleOwner = viewLifecycleOwner
        }

        Bundle().apply {
            putString(FirebaseAnalytics.Param.ITEM_ID, firebaseTag)
            (activity as NavigationActivity).firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, this)
        }

        return binding.root
    }


    /** Default behavior only allows portrait orientation. Content in About Fragment not suitable for landscape orientation **/
    override fun onResume() {
        super.onResume()
        if (lockPortraitOrientation) {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    override fun onPause() {
        super.onPause()
        if (lockPortraitOrientation) {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
        }
    }

}