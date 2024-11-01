package za.co.varsitycollege.serversamurai.flexforce.auth

import android.content.Context
import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import java.util.concurrent.Executor

class BiometricHelper(private val context: Context, private val callback: AuthenticationCallback) {

    private val executor: Executor = ContextCompat.getMainExecutor(context)

    private val biometricPrompt: BiometricPrompt by lazy {
        BiometricPrompt(context as FragmentActivity, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    callback.onError(errString.toString())
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    callback.onSuccess()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    callback.onFailure()
                }
            })
    }

    private val promptInfo: BiometricPrompt.PromptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle("Fingerprint Authentication")
        .setSubtitle("Use your fingerprint to log in securely")
        .setNegativeButtonText("Cancel")
        .build()

    fun authenticate() {
        biometricPrompt.authenticate(promptInfo)
    }

    interface AuthenticationCallback {
        fun onSuccess()
        fun onError(error: String)
        fun onFailure()
    }
}
