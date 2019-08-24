package nl.rvbsoftdev.curiosityreporting.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import nl.rvbsoftdev.curiosityreporting.R

/** Fragment that displays the privacy policy when clicking the privacy icon in the 'about' fragment **/

class PrivacyPolicyFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_privacy_policy, container, false)
    }
}
