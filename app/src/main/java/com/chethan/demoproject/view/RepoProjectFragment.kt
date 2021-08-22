package com.chethan.demoproject.view

import android.annotation.TargetApi
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.SslErrorHandler
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.chethan.demoproject.R
import kotlinx.android.synthetic.main.repo_project_layout.*


/**
 *Created by Bhagavan Byreddy on 20/08/21.
 */
class RepoProjectFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.repo_project_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            if (requireArguments().containsKey("projectLink")) {

                webview.webViewClient = object : WebViewClient() {
                    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                        //LoadingIndicator.getInstance().show(this@TermsAndConditions)
                        super.onPageStarted(view, url, favicon)
                    }

                    override fun onPageFinished(view: WebView?, url: String?) {
                        //LoadingIndicator.getInstance().hide()
                        super.onPageFinished(view, url)
                    }

                    @SuppressWarnings("deprecation")
                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        urlString: String?
                    ): Boolean {
                        if (urlString != null && urlString.toString().startsWith("mailto:")) {

                            val emailId = urlString.replace("mailto:", "")
                        }
                        val uriUrl = Uri.parse(urlString)
                        val launchBrowser = Intent(Intent.ACTION_VIEW, uriUrl)
                        startActivity(launchBrowser)
                        return true

                    }

                    @TargetApi(Build.VERSION_CODES.N)
                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): Boolean {
                        val url = request?.url
                        if (url != null) {
                            val uriUrl = Uri.parse(url.toString())
                            val launchBrowser = Intent(Intent.ACTION_VIEW, uriUrl)
                            startActivity(launchBrowser)
                        }
                        return true

                    }

                    override fun onReceivedSslError(
                        view: WebView?,
                        handler: SslErrorHandler,
                        error: SslError
                    ) {
                        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
                        var message = "SSL Certificate error."
                        message = when (error.primaryError) {
                            SslError.SSL_UNTRUSTED -> "The certificate authority is not trusted."
                            SslError.SSL_EXPIRED -> "The certificate has expired."
                            SslError.SSL_IDMISMATCH -> "The certificate Hostname mismatch."
                            SslError.SSL_NOTYETVALID -> "The certificate is not yet valid."
                            SslError.SSL_DATE_INVALID -> "The certificate date is invalid."
                            SslError.SSL_INVALID -> "The certificate is invalid."
                            else -> "The certificate is invalid."
                        }
                        message += " Do you want to continue anyway?"
                        builder.setTitle("SSL Certificate Error")
                        builder.setMessage(message)
                        builder.setPositiveButton("continue",
                            DialogInterface.OnClickListener { dialog, which -> handler.proceed() })
                        builder.setNegativeButton("cancel",
                            DialogInterface.OnClickListener { dialog, which -> handler.cancel() })
                        val dialog: AlertDialog = builder.create()
                        dialog.show()
                    }
                }
                webview.loadUrl(requireArguments().getString("projectLink"))
            }
        }

    }

}
