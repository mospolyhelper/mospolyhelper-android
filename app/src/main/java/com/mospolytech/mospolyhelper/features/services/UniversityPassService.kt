package com.mospolytech.mospolyhelper.features.services
import android.app.Service
import android.content.Intent
import android.content.SharedPreferences
import android.nfc.cardemulation.HostApduService
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.util.*
import javax.crypto.*
import javax.crypto.spec.SecretKeySpec

class UniversityPassService : HostApduService() {
    companion object {
        private const val TAG = "UniversityPassService"
        private const val SERVICE_STATE = "state"
        private val AID = byteArrayOf(-16, 80, 65, 82, 83, 69, 67, 46)
        private val WRONG_LENGTH_SW = byteArrayOf(103, 0)  // 67 00
        private val SELECT_OK_SW = byteArrayOf(-112, 0) // 90 00
        private val WRITE_DATA_SW = byteArrayOf(0, -48) // 00 D0
        private val READ_DATA_SW = byteArrayOf(0, -80) // 00 B0
        private val SELECT_APDU_HEADER = byteArrayOf(0, -92) // 00 A4
        private var aidSelected = false
        private var androidId: String? = null
        private var hostCheckPassed = false
        private var key: SecretKey? = null
        private lateinit var random: ByteArray
        var serviceActivated = true

        private var currentLogEventPos: ULong = 0UL
        private var logList = MutableList(128) { "" }
    }

    override fun onCreate() {
        Log.d(TAG, "Service onCreate()")
        androidId = Settings.Secure.getString(contentResolver, "android_id")
        val prefs = getSharedPreferences(packageName.toString() + "_preferences", 0)
        serviceActivated =
            prefs.getBoolean(
                SERVICE_STATE, true
            )

        restoreLogList(prefs)
        addToLogList("Service onCreate()")
        saveLogList()
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Service onStartCommand()")
        if (intent != null) {
            serviceActivated = intent.getBooleanExtra(SERVICE_STATE, true)
            storeState(serviceActivated)
        }
        addToLogList("Service onStartCommand(), serviceActivated = $serviceActivated")
        saveLogList()
        return Service.START_STICKY
    }

    override fun processCommandApdu(commandApdu: ByteArray, extras: Bundle?): ByteArray? {
        val hex = Hex(commandApdu).hexString
        Log.i(TAG, "Received APDU: $hex")
        addToLogList("Received APDU: $hex")

        if (!serviceActivated) {
            return null
        }
        when {
            containsSW(commandApdu, SELECT_APDU_HEADER) -> {
                aidSelected = true
                random = ByteArray(8)
                SecureRandom().nextBytes(random)
                key = SecretKeySpec(random, "DES")
                return Hex.concatArrays(random, SELECT_OK_SW, 8)
            }
            aidSelected && containsSW(commandApdu, WRITE_DATA_SW) -> {
                val encryptedAid = ByteArray(8)
                var decryptedAid: ByteArray? = null
                System.arraycopy(commandApdu, 5, encryptedAid, 0, 8)
                try {
                    val cipher = Cipher.getInstance("DES/ECB/NoPadding")
                    cipher.init(Cipher.DECRYPT_MODE, key)
                    decryptedAid = cipher.doFinal(encryptedAid)
                } catch (e: NoSuchAlgorithmException) {
                    e.printStackTrace()
                } catch (e2: NoSuchPaddingException) {
                    e2.printStackTrace()
                } catch (e3: InvalidKeyException) {
                    e3.printStackTrace()
                } catch (e4: IllegalBlockSizeException) {
                    e4.printStackTrace()
                } catch (e5: BadPaddingException) {
                    e5.printStackTrace()
                }
                if (Arrays.equals(AID, decryptedAid)) {
                    hostCheckPassed = true
                    return SELECT_OK_SW
                } else {
                    aidSelected = false
                    return WRONG_LENGTH_SW
                }
            }
            aidSelected && hostCheckPassed && containsSW(commandApdu, READ_DATA_SW) -> {
                var encryptedAndroidId: ByteArray? = null
                try {
                    val cipher2 = Cipher.getInstance("DES/ECB/NoPadding")
                    cipher2.init(Cipher.ENCRYPT_MODE, key)
                    encryptedAndroidId = cipher2.doFinal(Hex(androidId!!).bytes)
                } catch (e6: NoSuchAlgorithmException) {
                    e6.printStackTrace()
                } catch (e7: NoSuchPaddingException) {
                    e7.printStackTrace()
                } catch (e8: InvalidKeyException) {
                    e8.printStackTrace()
                } catch (e9: IllegalBlockSizeException) {
                    e9.printStackTrace()
                } catch (e10: BadPaddingException) {
                    e10.printStackTrace()
                }
                clearSession()
                if (encryptedAndroidId == null) {
                    return WRONG_LENGTH_SW
                }
                return Hex.concatArrays(encryptedAndroidId, SELECT_OK_SW, 8)
            }
            else -> {
                return WRONG_LENGTH_SW
            }
        }
    }

    override fun onDeactivated(reason: Int) {
        Log.d(TAG, "Service onDeactivated()")
        addToLogList("Service onDeactivated()")
        saveLogList()
        clearSession()
    }

    override fun onDestroy() {
        Log.d(TAG, "Service onDestroy()")
        addToLogList("Service onDestroy()")
        saveLogList()
        storeState(serviceActivated)
    }

    private fun storeState(state: Boolean) {
        val editor = getSharedPreferences(packageName.toString() + "_preferences", 0).edit()
        editor.putBoolean(SERVICE_STATE, state)
        editor.commit()
    }

    private fun clearSession() {
        aidSelected = false
        hostCheckPassed = false
        key = null
    }

    private inline fun containsSW(byteArray: ByteArray, sw: ByteArray) : Boolean {
        return byteArray[0] == sw[0] && byteArray[1] == sw[1]
    }

    private fun addToLogList(text: String) {
        logList[(currentLogEventPos % logList.size.toULong()).toInt()] = "[$currentLogEventPos] $text"
        currentLogEventPos++
    }

    private fun saveLogList() {
        val editor = getSharedPreferences(packageName.toString() + "_preferences", 0).edit()
        editor.putString("UniversityPassLog", logList.joinToString("\n"))
        editor.putLong("UniversityPassLogCounter", currentLogEventPos.toLong())
        editor.commit()
    }

    private fun restoreLogList(prefs: SharedPreferences) {
        currentLogEventPos =
            prefs.getLong(
                "UniversityPassLogCounter", 0L
            ).toULong()
        logList = prefs.getString("UniversityPassLog", "")!!.split("\n").toMutableList()
    }
}

class Hex(
    val bytes: ByteArray
) {

    constructor(s: String) : this(hexStringToByteArray(s)) {
    }

    val hexString: String
        get() = byteArrayToHexString(bytes)

    companion object {
        fun byteArrayToHexString(array: ByteArray): String {
            val hexString = StringBuffer()
            for (b in array) {
                val intVal: Int = b.toInt() and 255
                if (intVal < 16) {
                    hexString.append("0")
                }
                hexString.append(Integer.toHexString(intVal))
            }
            return hexString.toString()
        }

        fun hexStringToByteArray(s: String): ByteArray {
            val len = s.length
            val data2 = ByteArray(len / 2)
            var i = 0
            while (i < len) {
                data2[i / 2] = ((Character.digit(s[i], 16) shl 4) + Character.digit(
                    s[i + 1], 16
                )).toByte()
                i += 2
            }
            return data2
        }

        fun concatArrays(first: ByteArray, second: ByteArray, firstMax: Int = -1): ByteArray {
            val firsSize = if (firstMax == -1) first.size else firstMax
            val totalLength = firsSize + second.size
            val result = first.copyOf(totalLength)
            System.arraycopy(second, 0, result, firsSize, second.size)
            return result
        }
    }
}
