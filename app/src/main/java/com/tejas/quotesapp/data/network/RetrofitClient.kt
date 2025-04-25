package com.tejas.quotesapp.data.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.*
import android.util.Log

object RetrofitClient {
    private const val BASE_URL = "https://api.quotable.io/"
    
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private fun createSSLSocketFactory(): SSLSocketFactory {
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {
                // Log certificate chain for debugging
                chain?.forEachIndexed { index, cert ->
                    Log.d("SSL_DEBUG", "Certificate $index:")
                    Log.d("SSL_DEBUG", "  Subject: ${cert.subjectDN}")
                    Log.d("SSL_DEBUG", "  Issuer: ${cert.issuerDN}")
                    Log.d("SSL_DEBUG", "  Valid from: ${cert.notBefore}")
                    Log.d("SSL_DEBUG", "  Valid until: ${cert.notAfter}")
                }
            }

            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
                chain?.forEachIndexed { index, cert ->
                    Log.d("SSL_DEBUG", "Server Certificate $index:")
                    Log.d("SSL_DEBUG", "  Subject: ${cert.subjectDN}")
                    Log.d("SSL_DEBUG", "  Issuer: ${cert.issuerDN}")
                    Log.d("SSL_DEBUG", "  Valid from: ${cert.notBefore}")
                    Log.d("SSL_DEBUG", "  Valid until: ${cert.notAfter}")
                }
            }

            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        })

        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, trustAllCerts, java.security.SecureRandom())
        return sslContext.socketFactory
    }
    
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .sslSocketFactory(createSSLSocketFactory(), object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        })
        .hostnameVerifier { _, _ -> true }
        .build()
    
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    val quoteApiService: QuoteApiService by lazy {
        retrofit.create(QuoteApiService::class.java)
    }
} 