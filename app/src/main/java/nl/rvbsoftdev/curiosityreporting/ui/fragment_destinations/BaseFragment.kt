package nl.rvbsoftdev.curiosityreporting.ui.fragment_destinations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.google.firebase.analytics.FirebaseAnalytics
import nl.rvbsoftdev.curiosityreporting.ui.single_activity.SingleActivity

abstract class BaseFragment<B : ViewDataBinding> : Fragment() {

    protected lateinit var binding: B
    @get:LayoutRes
    protected abstract val layout: Int
    protected abstract val firebaseTag: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate<B>(inflater, layout, container, false).also {
            it.lifecycleOwner = this
        }

        Bundle().apply {
            putString(FirebaseAnalytics.Param.ITEM_ID, firebaseTag)
            (activity as SingleActivity).firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, this)
        }

        return binding.root
    }
}