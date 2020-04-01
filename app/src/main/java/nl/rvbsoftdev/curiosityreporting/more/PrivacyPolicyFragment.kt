package nl.rvbsoftdev.curiosityreporting.more

import nl.rvbsoftdev.curiosityreporting.R
import nl.rvbsoftdev.curiosityreporting.databinding.FragmentPrivacyPolicyBinding
import nl.rvbsoftdev.curiosityreporting.global.BaseFragment

/** Fragment that displays the privacy policy when clicking the privacy icon in the 'about' fragment **/

class PrivacyPolicyFragment : BaseFragment<FragmentPrivacyPolicyBinding>() {

    override val layout = R.layout.fragment_privacy_policy
    override val firebaseTag = "Privacy Policy Fragment"

}
