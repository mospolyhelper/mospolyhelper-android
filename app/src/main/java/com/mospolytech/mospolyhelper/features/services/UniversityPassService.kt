package com.mospolytech.mospolyhelper.features.services
import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.nfc.cardemulation.HostApduService
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.preference.PreferenceManager
import com.mospolytech.mospolyhelper.data.core.local.SharedPreferencesDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.time.LocalDateTime
import java.util.*
import javax.crypto.*
import javax.crypto.spec.SecretKeySpec
import kotlin.experimental.and

class UniversityPassService : HostApduService() {
    private lateinit var dataSource: SharedPreferencesDataSource
    override fun onCreate() {
        Log.d(packageName, "Service onCreate()")
        dataSource = SharedPreferencesDataSource(PreferenceManager.getDefaultSharedPreferences(this.applicationContext))
        androidId = Settings.Secure.getString(contentResolver, "android_id")
        serviceActivated =
            getSharedPreferences(packageName.toString() + "_preferences", 0).getBoolean(
                SERVICE_STATE, true
            )
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(packageName, "Service onStartCommand()")
        if (intent != null) {
            serviceActivated = intent.getBooleanExtra(SERVICE_STATE, true)
            storeState(serviceActivated)
        }
        var log = dataSource.getString("UniversityPassLog", "")
        GlobalScope.async(Dispatchers.IO) {
            log = "${LocalDateTime.now()}: Изменение состояния службы: $serviceActivated\n$log"
            dataSource.setString("UniversityPassLog", log)
        }
        return Service.START_STICKY
    }

    override fun processCommandApdu(commandApdu: ByteArray, extras: Bundle?): ByteArray? {
        val hex = Hex(commandApdu).hexString
        var log = dataSource.getString("UniversityPassLog", "")
        log = "${LocalDateTime.now()}: Начало без async\n" +
                "$hex\n$log"
        GlobalScope.async(Dispatchers.IO) {
            log = "${LocalDateTime.now()}: Начало\n" +
                    "$hex\n$log"
            dataSource.setString("UniversityPassLog", log)
        }
        dataSource.setString("UniversityPassLog", log)
        if (!serviceActivated) {
            return null
        }
        return if (commandApdu[0] == 0.toByte() && commandApdu[1] == (-92).toByte()) {
            GlobalScope.async(Dispatchers.IO) {
                log = "${LocalDateTime.now()}: 1 этап - отправка секртеного ключа\n" +
                        "$hex\n$log"
                dataSource.setString("UniversityPassLog", log)
            }
            aidSelected = true
            val respApdu = ByteArray(10)
            random = ByteArray(8)
            SecureRandom().nextBytes(random)
            key = SecretKeySpec(random, "DES")
            System.arraycopy(random, 0, respApdu, 0, 8)
            respApdu[8] = -112
            respApdu[9] = 0
            respApdu
        } else if (aidSelected && commandApdu[0] == 0.toByte() && commandApdu[1] == (-48).toByte()) {
            GlobalScope.async(Dispatchers.IO) {
                log = "${LocalDateTime.now()}: 2 этап - расшифровка и проверка Aid\n" +
                        "$hex\n$log"
                dataSource.setString("UniversityPassLog", log)
            }
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
            if (Arrays.equals(aid, decryptedAid)) {
                hostCheckPassed = true
                val bArr = ByteArray(2)
                bArr[0] = -112
                return bArr
            }
            GlobalScope.async(Dispatchers.IO) {
                log = "${LocalDateTime.now()}: Ошибка во время 2 этапа\n" +
                        "$hex\n$log"
                dataSource.setString("UniversityPassLog", log)
            }
            aidSelected = false
            val bArr2 = ByteArray(2)
            bArr2[0] = 103
            bArr2
        } else if (!aidSelected || !hostCheckPassed || commandApdu[0] != 0.toByte() || commandApdu[1] != (-80).toByte()) {
            GlobalScope.async(Dispatchers.IO) {
                log = "${LocalDateTime.now()}: Ошибка\n" +
                        "$hex\n$log"
                dataSource.setString("UniversityPassLog", log)
            }
            val bArr3 = ByteArray(2)
            bArr3[0] = 103
            bArr3
        } else {
            GlobalScope.async(Dispatchers.IO) {
                log = "${LocalDateTime.now()}: 3 этап - отправка id\n" +
                        "$hex\n$log"
                dataSource.setString("UniversityPassLog", log)
            }
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
                GlobalScope.async(Dispatchers.IO) {
                    log = "${LocalDateTime.now()}: Ошибка во время 3 этапа\n" +
                            "$hex\n$log"
                    dataSource.setString("UniversityPassLog", log)
                }
                val bArr4 = ByteArray(2)
                bArr4[0] = 103
                return bArr4
            }
            val respApdu2 = ByteArray(10)
            System.arraycopy(encryptedAndroidId, 0, respApdu2, 0, 8)
            respApdu2[8] = -112
            respApdu2[9] = 0
            respApdu2
        }
    }

    override fun onDeactivated(reason: Int) {
        Log.d(packageName, "Service onDeactivated()")
        clearSession()
    }

    override fun onDestroy() {
        Log.d(packageName, "Service onDestroy()")
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

    companion object {
        private const val KEY_LENGTH = 8
        private const val SERVICE_STATE = "state"
        private val aid = byteArrayOf(-16, 80, 65, 82, 83, 69, 67, 46)
        private var aidSelected = false
        private var androidId: String? = null
        private var hostCheckPassed = false
        private var key: SecretKey? = null
        private lateinit var random: ByteArray
        var serviceActivated = true
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
        private fun byteArrayToHexString(array: ByteArray): String {
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

        private fun hexStringToByteArray(s: String): ByteArray {
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
    }
}
