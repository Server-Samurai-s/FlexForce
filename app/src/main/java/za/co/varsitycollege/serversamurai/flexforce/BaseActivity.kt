package za.co.varsitycollege.serversamurai.flexforce

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import java.util.*

open class BaseActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        // Retrieve shared preferences to get the saved language preference
        val sharedPreferences: SharedPreferences = newBase.getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)
        val languageCode = sharedPreferences.getString("language", "en") ?: "en"  // Default to English

        // Apply the language code to the newBase context
        val context = updateLocale(newBase, languageCode)
        super.attachBaseContext(context)
    }

    private fun updateLocale(context: Context, languageCode: String): Context {
        val locale = Locale(languageCode)
        // Set this locale as the default
        Locale.setDefault(locale)
        // Update the configuration to apply the new locale
        val config = context.resources.configuration
        config.setLocale(locale)

        return context.createConfigurationContext(config)
    }
}
