package za.co.varsitycollege.serversamurai.flexforce.ui.fragments.workout

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import za.co.varsitycollege.serversamurai.flexforce.R
import za.co.varsitycollege.serversamurai.flexforce.databinding.FragmentWelcomeScreenBinding

class WelcomeScreen : Fragment() {

    private var _binding: FragmentWelcomeScreenBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWelcomeScreenBinding.inflate(inflater, container, false)
        val view = binding.root

        // Setup "Get Started" button
        binding.getStartedButton.setOnClickListener {
            findNavController().navigate(R.id.action_welcomeFragment_to_registerFragment)
        }

        // Retrieve localized strings for "Already have an account?" and "Login"
        val loginText = getString(R.string.login_text)
        val fullText = getString(R.string.already_have_account, loginText)
        val spannableText = SpannableString(fullText)

        // Locate the start and end of the clickable "Login" portion
        val loginTextStart = fullText.indexOf(loginText)
        val loginTextEnd = loginTextStart + loginText.length

        if (loginTextStart >= 0) { // Ensure the login text is found
            val clickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    findNavController().navigate(R.id.action_welcomeFragment_to_loginFragment)
                }
            }

            spannableText.setSpan(clickableSpan, loginTextStart, loginTextEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannableText.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.white)),
                loginTextStart,
                loginTextEnd,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        // Set the spannable text on the TextView
        binding.accessAccountText.text = spannableText
        binding.accessAccountText.movementMethod = android.text.method.LinkMovementMethod.getInstance()

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
