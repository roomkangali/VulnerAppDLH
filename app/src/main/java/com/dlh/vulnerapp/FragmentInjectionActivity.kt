package com.dlh.vulnerapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.preference.PreferenceActivity

/**
 * VULNERABILITY: Fragment Injection.
 *
 * An exported PreferenceActivity whose isValidFragment() returns true for ANY
 * fragment name. An attacker can therefore load an arbitrary Fragment inside this
 * app's context via the ":android:show_fragment" intent extra:
 *
 *   am start -n com.dlh.vulnerapp/.FragmentInjectionActivity \
 *            --es ":android:show_fragment" <any.Fragment.class.name>
 */
@Suppress("DEPRECATION")
@SuppressLint("ExportedPreferenceActivity")
class FragmentInjectionActivity : PreferenceActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Fragment Injection Module"
    }

    // VULNERABILITY: no allowlist — every fragment class name is accepted, so a
    // caller-supplied fragment gets instantiated with this app's permissions.
    override fun isValidFragment(fragmentName: String?): Boolean {
        return true
    }
}
