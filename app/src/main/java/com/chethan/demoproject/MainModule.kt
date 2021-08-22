package com.chethan.demoproject

import android.content.Intent
import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import okio.Buffer
import org.apache.http.conn.ConnectTimeoutException
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.security.cert.CertificateException
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

private const val TIME_READ_CONNECTION_TIME_OUT = 60L

val mainModule = module {

    single { DataRepository(get()) }

    single { createWebService() }

    viewModel { RepoViewModel(get()) }

}

fun createWebService(): NetWorkApi {
    val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .baseUrl("http://mobcategories.s3-website-eu-west-1.amazonaws.com")
        .client(httpClient)
        .build()

    return retrofit.create(NetWorkApi::class.java)
}

private val httpClient: OkHttpClient = OkHttpClient().newBuilder().addInterceptor { chain ->
    var request = chain.request()

    /*val authToken = EssPrefs.getAccessToken()
    if (authToken != null && authToken.isNotEmpty()) {
        Utils.log("Authorization : bearer ${EssPrefs.getAccessToken()}")*/
    request =
        request.newBuilder()
            //.addHeader("Authorization", "bearer ${EssPrefs.getAccessToken()}")
            .method(request.method(), request.body())
            .build()
    //}

    if (!request.url().toString().contains("File/FileUpload")) {
        Log.e("Request-->${request.url()}", "params-->${bodyToString(request)}}")
    } else {
        Log.e("Request-->", "${request.url()}")
    }

    try {
        val response = chain.proceed(request)
        val body = response.body()
        val bodyString = body?.string()
        val contentType = body?.contentType()

        if (response.code() == 401) {
            //("Your session has been expired. Please login again to continue.")
        } else if (response.code() == 500) {
            // no internet
        }

        Log.e("Response_Body -->", "$bodyString")

        response.newBuilder().body(ResponseBody.create(contentType, bodyString!!)).build()
    } catch (exception: Exception) {
        Log.e("Response_Exception -->", "${exception.message}")
        if (exception is UnknownHostException) {
            // UnknownHostException
        } else if (exception is SocketTimeoutException || exception is SocketException || exception is ConnectTimeoutException) {
            // SocketTimeoutException
        } else if (exception.message == "Canceled") {
            Log.e("Response_Exception -->", "${exception.message}")

        } else if (exception.message.equals("Socket closed", true)) {
            //Socket closed
        }
        throw  exception
    }

}/*.sslSocketFactory(getSSLFactory(), trustAllCerts[0] as X509TrustManager)
    .connectTimeout(TIME_READ_CONNECTION_TIME_OUT, TimeUnit.SECONDS)
    //.callTimeout(TIME_READ_CONNECTION_TIME_OUT, TimeUnit.SECONDS)
    .readTimeout(TIME_READ_CONNECTION_TIME_OUT, TimeUnit.SECONDS)
    .hostnameVerifier { _, _ -> true; }*/
    .build()

private fun getSSLFactory(): SSLSocketFactory {
    // Create a trust manager that does not validate certificate chains

    // Install the all-trusting trust manager
    val sslContext = SSLContext.getInstance("SSL")
    sslContext.init(null, trustAllCerts, java.security.SecureRandom())

    // Create an ssl socket factory with our all-trusting manager
    return sslContext.socketFactory
}

private val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
    @Throws(CertificateException::class)
    override fun checkClientTrusted(
        chain: Array<java.security.cert.X509Certificate>,
        authType: String
    ) {
    }

    @Throws(CertificateException::class)
    override fun checkServerTrusted(
        chain: Array<java.security.cert.X509Certificate>,
        authType: String
    ) {
    }

    override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> {
        return arrayOf()
    }
})

private fun bodyToString(request: Request): String {

    return try {
        val copy = request.newBuilder().build()
        val buffer = Buffer()
        copy.body()?.writeTo(buffer)
        buffer.readUtf8()
    } catch (e: IOException) {
        "did not work"
    }

}