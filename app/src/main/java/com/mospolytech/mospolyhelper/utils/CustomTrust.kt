package com.mospolytech.mospolyhelper.utils

import okio.Buffer
import java.io.IOException
import java.io.InputStream
import java.security.GeneralSecurityException
import java.security.KeyStore
import java.security.cert.CertificateFactory
import java.util.*
import javax.net.ssl.*


class CustomTrust {

    /**
     * Returns an input stream containing one or more certificate PEM files. This implementation just
     * embeds the PEM files in Java strings; most applications will instead read this from a resource
     * file that gets bundled with the application.
     */
    private fun trustedCertificatesInputStream(): InputStream {
        // PEM files for root certificates of Comodo and Entrust. These two CAs are sufficient to view
        // https://publicobject.com (Comodo) and https://squareup.com (Entrust). But they aren't
        // sufficient to connect to most HTTPS sites including https://godaddy.com and https://visa.com.
        // Typically developers will need to get a PEM file from their organization's TLS administrator.
        val comodoRsaCertificationAuthority = """
            -----BEGIN CERTIFICATE-----
            MIIGODCCBSCgAwIBAgIQBxlESbsqMVJJXL6K0qYEOTANBgkqhkiG9w0BAQsFADCB
            jzELMAkGA1UEBhMCR0IxGzAZBgNVBAgTEkdyZWF0ZXIgTWFuY2hlc3RlcjEQMA4G
            A1UEBxMHU2FsZm9yZDEYMBYGA1UEChMPU2VjdGlnbyBMaW1pdGVkMTcwNQYDVQQD
            Ey5TZWN0aWdvIFJTQSBEb21haW4gVmFsaWRhdGlvbiBTZWN1cmUgU2VydmVyIENB
            MB4XDTIwMDUwMjAwMDAwMFoXDTIyMDUwMjIzNTk1OVowGzEZMBcGA1UEAwwQKi5t
            b3Nwb2x5dGVjaC5ydTCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAIhN
            Q1wRNTfb01FcuOAOOQeYIxFUdyqH6GUFSK0isUKDMX084HA3D+sV+4RrvC3VR8q2
            5TWxqb2VlS/6Knj2PydiymnGqjsHf9RLOUqDUih7R89XLagnv238Vm9N4uCr3dDZ
            o32qBIGrsJ0fNbJ8nCVa/HFYPY8fgg5hweTB2XsrmCoEErwIYXTnNX/YnKtg9Bu0
            EF7WKwWk4JsWV7sTqpxy65/jWfSkG+niOqzHQNDc7W5ovZg1e32qXM8MFH9+45vL
            Kk9EjjQhlW4RsWph9nXcEIlJ87B9zr8TLVCsMEvD5WZ6KskUbdUhq6b7JPW2uIAO
            fMcyndWmHiodBFU0zKcCAwEAAaOCAwEwggL9MB8GA1UdIwQYMBaAFI2MXsRUrYrh
            d+mb+ZsF4bgBjWHhMB0GA1UdDgQWBBT8Kpa+NdNo8RvOLm5tPigdEubF9TAOBgNV
            HQ8BAf8EBAMCBaAwDAYDVR0TAQH/BAIwADAdBgNVHSUEFjAUBggrBgEFBQcDAQYI
            KwYBBQUHAwIwSQYDVR0gBEIwQDA0BgsrBgEEAbIxAQICBzAlMCMGCCsGAQUFBwIB
            FhdodHRwczovL3NlY3RpZ28uY29tL0NQUzAIBgZngQwBAgEwgYQGCCsGAQUFBwEB
            BHgwdjBPBggrBgEFBQcwAoZDaHR0cDovL2NydC5zZWN0aWdvLmNvbS9TZWN0aWdv
            UlNBRG9tYWluVmFsaWRhdGlvblNlY3VyZVNlcnZlckNBLmNydDAjBggrBgEFBQcw
            AYYXaHR0cDovL29jc3Auc2VjdGlnby5jb20wKwYDVR0RBCQwIoIQKi5tb3Nwb2x5
            dGVjaC5ydYIObW9zcG9seXRlY2gucnUwggF9BgorBgEEAdZ5AgQCBIIBbQSCAWkB
            ZwB2AEalVet1+pEgMLWiiWn0830RLEF0vv1JuIWr8vxw/m1HAAABcda6e24AAAQD
            AEcwRQIhALT8i7h6q6TOboXcJvZftcwX6OscX3lr3kdTdyKxEQoCAiBt/mqovC7I
            TjKzq8ljS1b8AJ3RKndkuNebM/ZQmfo8wAB1AN+lXqtogk8fbK3uuF9OPlrqzaIS
            pGpejjsSwCBEXCpzAAABcda6e5UAAAQDAEYwRAIgRMknxTMGzVsOkD36UluvKP9r
            njBAlidrKTXeNbL2w9sCICs1+t4lBgwrQxh0s+tVqKV+xm1yhxLA9ufkgwDb0Tvx
            AHYAb1N2rDHwMRnYmQCkURX/dxUcEdkCwQApBo2yCJo32RMAAAFx1rp7ZwAABAMA
            RzBFAiBZy79noNi/VG+GeSJziYtF/zpccMQ486Jnn9R5oLNHCwIhALbgcNeix0VN
            key7m0D5x1a09eL+t6WXxes1U83cQW9DMA0GCSqGSIb3DQEBCwUAA4IBAQB/tCz0
            sZuktAxnSsCAd9WnOYYz6Qj5EMLRCydeefR/RqdmICAfd1Q9Bsw9RtqS+2sI85HM
            vICFePd+VzeoKrfMKgEpyAR27+DshAXegDc0+KBsPTHFuVxyOrZWT6kYHO5U0XEs
            CTiRj6McR3Rin4/O3gPQhNt3Qww+3q9c4JeoUwdj+NGcNbwDyY1c6bql+i4+OUFO
            cH++KChd2KpdKz1srbBQjw/WGZbLJUU0mF9MpxMlQYzJwKgJ9B3LEiKkOoKJ+VSt
            Apxfbs5pTzznZpIAUAw8alQiG8eS1fLMzZu34mqS6D/SCNVkdJQy8Cb++4FW2f3n
            IbtUZuqIHa9zQTNF
            -----END CERTIFICATE-----
            
            """.trimIndent()
        val entrustRootCertificateAuthority = """
            -----BEGIN CERTIFICATE-----
            MIIGEzCCA/ugAwIBAgIQfVtRJrR2uhHbdBYLvFMNpzANBgkqhkiG9w0BAQwFADCB
            iDELMAkGA1UEBhMCVVMxEzARBgNVBAgTCk5ldyBKZXJzZXkxFDASBgNVBAcTC0pl
            cnNleSBDaXR5MR4wHAYDVQQKExVUaGUgVVNFUlRSVVNUIE5ldHdvcmsxLjAsBgNV
            BAMTJVVTRVJUcnVzdCBSU0EgQ2VydGlmaWNhdGlvbiBBdXRob3JpdHkwHhcNMTgx
            MTAyMDAwMDAwWhcNMzAxMjMxMjM1OTU5WjCBjzELMAkGA1UEBhMCR0IxGzAZBgNV
            BAgTEkdyZWF0ZXIgTWFuY2hlc3RlcjEQMA4GA1UEBxMHU2FsZm9yZDEYMBYGA1UE
            ChMPU2VjdGlnbyBMaW1pdGVkMTcwNQYDVQQDEy5TZWN0aWdvIFJTQSBEb21haW4g
            VmFsaWRhdGlvbiBTZWN1cmUgU2VydmVyIENBMIIBIjANBgkqhkiG9w0BAQEFAAOC
            AQ8AMIIBCgKCAQEA1nMz1tc8INAA0hdFuNY+B6I/x0HuMjDJsGz99J/LEpgPLT+N
            TQEMgg8Xf2Iu6bhIefsWg06t1zIlk7cHv7lQP6lMw0Aq6Tn/2YHKHxYyQdqAJrkj
            eocgHuP/IJo8lURvh3UGkEC0MpMWCRAIIz7S3YcPb11RFGoKacVPAXJpz9OTTG0E
            oKMbgn6xmrntxZ7FN3ifmgg0+1YuWMQJDgZkW7w33PGfKGioVrCSo1yfu4iYCBsk
            Haswha6vsC6eep3BwEIc4gLw6uBK0u+QDrTBQBbwb4VCSmT3pDCg/r8uoydajotY
            uK3DGReEY+1vVv2Dy2A0xHS+5p3b4eTlygxfFQIDAQABo4IBbjCCAWowHwYDVR0j
            BBgwFoAUU3m/WqorSs9UgOHYm8Cd8rIDZsswHQYDVR0OBBYEFI2MXsRUrYrhd+mb
            +ZsF4bgBjWHhMA4GA1UdDwEB/wQEAwIBhjASBgNVHRMBAf8ECDAGAQH/AgEAMB0G
            A1UdJQQWMBQGCCsGAQUFBwMBBggrBgEFBQcDAjAbBgNVHSAEFDASMAYGBFUdIAAw
            CAYGZ4EMAQIBMFAGA1UdHwRJMEcwRaBDoEGGP2h0dHA6Ly9jcmwudXNlcnRydXN0
            LmNvbS9VU0VSVHJ1c3RSU0FDZXJ0aWZpY2F0aW9uQXV0aG9yaXR5LmNybDB2Bggr
            BgEFBQcBAQRqMGgwPwYIKwYBBQUHMAKGM2h0dHA6Ly9jcnQudXNlcnRydXN0LmNv
            bS9VU0VSVHJ1c3RSU0FBZGRUcnVzdENBLmNydDAlBggrBgEFBQcwAYYZaHR0cDov
            L29jc3AudXNlcnRydXN0LmNvbTANBgkqhkiG9w0BAQwFAAOCAgEAMr9hvQ5Iw0/H
            ukdN+Jx4GQHcEx2Ab/zDcLRSmjEzmldS+zGea6TvVKqJjUAXaPgREHzSyrHxVYbH
            7rM2kYb2OVG/Rr8PoLq0935JxCo2F57kaDl6r5ROVm+yezu/Coa9zcV3HAO4OLGi
            H19+24rcRki2aArPsrW04jTkZ6k4Zgle0rj8nSg6F0AnwnJOKf0hPHzPE/uWLMUx
            RP0T7dWbqWlod3zu4f+k+TY4CFM5ooQ0nBnzvg6s1SQ36yOoeNDT5++SR2RiOSLv
            xvcRviKFxmZEJCaOEDKNyJOuB56DPi/Z+fVGjmO+wea03KbNIaiGCpXZLoUmGv38
            sbZXQm2V0TP2ORQGgkE49Y9Y3IBbpNV9lXj9p5v//cWoaasm56ekBYdbqbe4oyAL
            l6lFhd2zi+WJN44pDfwGF/Y4QA5C5BIG+3vzxhFoYt/jmPQT2BVPi7Fp2RBgvGQq
            6jG35LWjOhSbJuMLe/0CjraZwTiXWTb2qHSihrZe68Zk6s+go/lunrotEbaGmAhY
            LcmsJWTyXnW0OMGuf1pGg+pRyrbxmRE1a6Vqe8YAsOf4vmSyrcjC8azjUeqkk+B5
            yOGBQMkKW+ESPMFgKuOXwIlCypTPRpgSabuY0MLTDXJLR27lk8QyKGOHQ+SwMj4K
            00u/I5sUKUErmgQfky3xxzlIPK1aEn8=
            -----END CERTIFICATE-----
            
            """.trimIndent()
        return Buffer()
            .writeUtf8(comodoRsaCertificationAuthority)
            .writeUtf8(entrustRootCertificateAuthority)
            .inputStream()
    }

    /**
     * Returns a trust manager that trusts `certificates` and none other. HTTPS services whose
     * certificates have not been signed by these certificates will fail with a `SSLHandshakeException`.
     *
     *
     * This can be used to replace the host platform's built-in trusted certificates with a custom
     * set. This is useful in development where certificate authority-trusted certificates aren't
     * available. Or in production, to avoid reliance on third-party certificate authorities.
     *
     *
     * See also [CertificatePinner], which can limit trusted certificates while still using
     * the host platform's built-in trust store.
     *
     * <h3>Warning: Customizing Trusted Certificates is Dangerous!</h3>
     *
     *
     * Relying on your own trusted certificates limits your server team's ability to update their
     * TLS certificates. By installing a specific set of trusted certificates, you take on additional
     * operational complexity and limit your ability to migrate between certificate authorities. Do
     * not use custom trusted certificates in production without the blessing of your server's TLS
     * administrator.
     */
    @Throws(GeneralSecurityException::class)
    private fun trustManagerForCertificates(`in`: InputStream): X509TrustManager {
        val certificateFactory = CertificateFactory.getInstance("X.509")
        val certificates = certificateFactory.generateCertificates(`in`)
        require(!certificates.isEmpty()) { "expected non-empty set of trusted certificates" }

        // Put the certificates a key store.
        val password = "password".toCharArray() // Any password will work.
        val keyStore = newEmptyKeyStore(password)
        var index = 0
        for (certificate in certificates) {
            val certificateAlias = Integer.toString(index++)
            keyStore.setCertificateEntry(certificateAlias, certificate)
        }

        // Use it to build an X509 trust manager.
        val keyManagerFactory = KeyManagerFactory.getInstance(
            KeyManagerFactory.getDefaultAlgorithm()
        )
        keyManagerFactory.init(keyStore, password)
        val trustManagerFactory = TrustManagerFactory.getInstance(
            TrustManagerFactory.getDefaultAlgorithm()
        )
        trustManagerFactory.init(keyStore)
        val trustManagers = trustManagerFactory.trustManagers
        check(!(trustManagers.size != 1 || trustManagers[0] !is X509TrustManager)) {
            ("Unexpected default trust managers:"
                    + Arrays.toString(trustManagers))
        }
        return trustManagers[0] as X509TrustManager
    }

    @Throws(GeneralSecurityException::class)
    private fun newEmptyKeyStore(password: CharArray): KeyStore {
        return try {
            val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
            val `in`: InputStream? = null // By convention, 'null' creates an empty key store.
            keyStore.load(`in`, password)
            keyStore
        } catch (e: IOException) {
            throw AssertionError(e)
        }
    }
    val trustManager: X509TrustManager
    val sslSocketFactory: SSLSocketFactory

    init {

        try {
            trustManager = trustManagerForCertificates(trustedCertificatesInputStream())
            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(null, arrayOf<TrustManager>(trustManager), null)
            sslSocketFactory = sslContext.socketFactory
        } catch (e: GeneralSecurityException) {
            throw RuntimeException(e)
        }
    }
}