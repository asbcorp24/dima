package ru.alitas.crawler.security

import android.content.Context
import android.util.Base64
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.security.MessageDigest
import java.security.SecureRandom

class PinSecureStore(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs = EncryptedSharedPreferences.create(
        context,
        "crawler_pin_store",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun hasPin(crawlerId: String): Boolean =
        prefs.contains(keySalt(crawlerId)) && prefs.contains(keyHash(crawlerId))

    fun savePin(crawlerId: String, pin: String) {
        require(pin.matches(Regex("\\d{5}"))) { "PIN должен состоять из 5 цифр" }

        val salt = ByteArray(16).also { SecureRandom().nextBytes(it) }
        val hash = digest(salt, pin)

        prefs.edit()
            .putString(keySalt(crawlerId), Base64.encodeToString(salt, Base64.NO_WRAP))
            .putString(keyHash(crawlerId), Base64.encodeToString(hash, Base64.NO_WRAP))
            .apply()
    }

    fun verifyPin(crawlerId: String, pin: String): Boolean {
        val saltEncoded = prefs.getString(keySalt(crawlerId), null) ?: return false
        val hashEncoded = prefs.getString(keyHash(crawlerId), null) ?: return false

        val salt = Base64.decode(saltEncoded, Base64.NO_WRAP)
        val expected = Base64.decode(hashEncoded, Base64.NO_WRAP)
        val actual = digest(salt, pin)

        return MessageDigest.isEqual(expected, actual)
    }

    private fun digest(salt: ByteArray, pin: String): ByteArray =
        MessageDigest.getInstance("SHA-256").digest(salt + pin.toByteArray(Charsets.UTF_8))

    private fun keySalt(id: String) = "salt_$id"
    private fun keyHash(id: String) = "hash_$id"
}
