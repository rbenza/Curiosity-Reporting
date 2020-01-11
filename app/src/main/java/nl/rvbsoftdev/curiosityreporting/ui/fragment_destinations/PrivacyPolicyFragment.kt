package nl.rvbsoftdev.curiosityreporting.ui.fragment_destinations

import nl.rvbsoftdev.curiosityreporting.R
import nl.rvbsoftdev.curiosityreporting.databinding.FragmentPrivacyPolicyBinding

/** Fragment that displays the privacy policy when clicking the privacy icon in the 'about' fragment **/

class PrivacyPolicyFragment : BaseFragment<FragmentPrivacyPolicyBinding>() {

    override val layout = R.layout.fragment_privacy_policy
    override val firebaseTag = "Privacy Policy Fragment"

}
