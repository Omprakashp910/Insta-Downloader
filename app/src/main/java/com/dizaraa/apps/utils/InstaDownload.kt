package com.dizaraa.apps.utils

import android.app.Activity
import android.widget.Toast
import androidx.annotation.Keep
import com.franmontiel.persistentcookiejar.ClearableCookieJar
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.dizaraa.apps.model.insta.ModelEdNode
import com.dizaraa.apps.model.insta.ModelGetEdgetoNode
import com.dizaraa.apps.model.insta.ModelInstagramResponse
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import java.net.URI

object InstaDownload {

    //    companion object {

    var Urlwi: String? = ""

    @Keep
    fun startInstaDownload(Url: String, activity: Activity) {

        Utils.displayDLoader(activity)


        try {

            val uri = URI(Url)
            Urlwi = URI(
                uri.scheme,
                uri.authority,
                uri.path,
                null,  // Ignore the query part of the input url
                uri.fragment
            ).toString()


        } catch (ex: java.lang.Exception) {
            try {
//                    progressDralogGenaratinglink.dismiss()
                Utils.dismissDLoader()
            } catch (e: Exception) {
                e.printStackTrace()
            }
//                ShowToast(this@MainActivity, getString(R.string.invalid_url))
            return
        }

        System.err.println("workkkkkkkkk 1122112 $Url")

        var urlwithoutlettersqp: String? = Urlwi
        System.err.println("workkkkkkkkk 1122112 $urlwithoutlettersqp")


        if (urlwithoutlettersqp!!.contains("/reel/")) {
            urlwithoutlettersqp = urlwithoutlettersqp.replace("/reel/", "/p/")
        }

        if (urlwithoutlettersqp.contains("/tv/")) {
            urlwithoutlettersqp = urlwithoutlettersqp.replace("/tv/", "/p/")
        }

        val urlwithoutlettersqp_noa: String = urlwithoutlettersqp

        urlwithoutlettersqp = "$urlwithoutlettersqp?__a=1&__d=dis"
        System.err.println("workkkkkkkkk 87878788 $urlwithoutlettersqp")


        System.err.println("workkkkkkkkk 777777 $urlwithoutlettersqp")



        if (!(activity).isFinishing) {


            try {
                System.err.println("workkkkkkkkk 4 ")

                downloadInstagramImageOrVideoResponseOkhttp(
                    urlwithoutlettersqp_noa, activity
                )

            } catch (e: java.lang.Exception) {
                try {
//                    progressDralogGenaratinglink.dismiss()
                    Utils.dismissDLoader()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                System.err.println("workkkkkkkkk 5")
                e.printStackTrace()
//                ShowToast(, getString(R.string.error_occ))
            }

        }

    }

    var myPhotoUrlIs: String? = null
    var myVideoUrlIs: String? = null

    @Keep
    fun downloadInstagramImageOrVideoResponseOkhttp(URL: String?, activity: Activity) {

//TODO check
//        Unirest.config()
//            .socketTimeout(500)
//            .connectTimeout(1000)
//            .concurrency(10, 5)
//            .proxy(Proxy("https://proxy"))
//            .setDefaultHeader("Accept", "application/json")
//            .followRedirects(false)
//            .enableCookieManagement(false)
//            .addInterceptor(MyCustomInterceptor())

        object : Thread() {
            override fun run() {


                try {

                    val cookieJar: ClearableCookieJar = PersistentCookieJar(
                        SetCookieCache(),
                        SharedPrefsCookiePersistor(activity)
                    )

                    val logging = HttpLoggingInterceptor()
                    logging.setLevel(HttpLoggingInterceptor.Level.BODY)
                    // init OkHttpClient
                    val client: OkHttpClient = OkHttpClient.Builder()
                        .cookieJar(cookieJar)
                        .addInterceptor(logging)
                        .build()

                    val request: Request = Request.Builder()
                        .url("$URL?__a=1&__d=dis")
                        .method("GET", null)
                        .build()
                    val response = client.newCall(request).execute()

                    val ressd = response.body!!.string()
                    var code = response.code
                    if (!ressd.contains("shortcode_media")) {
                        code = 400
                    }
                    if (code == 200) {


                        try {


                            val listType =
                                object : TypeToken<ModelInstagramResponse?>() {}.type
                            val modelInstagramResponse: ModelInstagramResponse? =
                                GsonBuilder().create()
                                    .fromJson<ModelInstagramResponse>(
                                        ressd,
                                        listType
                                    )


                            if (modelInstagramResponse != null) {


                                if (modelInstagramResponse.modelGraphshortcode.shortcode_media.edge_sidecar_to_children != null) {
                                    val modelGetEdgetoNode: ModelGetEdgetoNode =
                                        modelInstagramResponse.modelGraphshortcode.shortcode_media.edge_sidecar_to_children

                                    val modelEdNodeArrayList: List<ModelEdNode> =
                                        modelGetEdgetoNode.modelEdNodes
                                    for (i in 0 until modelEdNodeArrayList.size) {
                                        if (modelEdNodeArrayList[i].modelNode.isIs_video) {
                                            myVideoUrlIs =
                                                modelEdNodeArrayList[i].modelNode.video_url


                                            val timeStamp =
                                                System.currentTimeMillis().toString()
                                            val file = "Insta_$timeStamp"
                                            val ext = "mp4"
                                            val fileName = "$file.$ext"

                                            Utils.downloader(
                                                activity,
                                                myVideoUrlIs?.replace("\"", ""),
                                                Utils.instaDirPath,
                                                fileName
                                            )

                                            try {
//                                                progressDralogGenaratinglink.dismiss()
                                                Utils.dismissDLoader()
                                            } catch (e: Exception) {
                                                e.printStackTrace()
                                            }


                                            myVideoUrlIs = ""
                                        } else {
                                            myPhotoUrlIs =
                                                modelEdNodeArrayList[i].modelNode.display_resources[modelEdNodeArrayList[i].modelNode.display_resources.size - 1].src


                                            val timeStamp =
                                                System.currentTimeMillis().toString()
                                            val file = "Insta_$timeStamp"
                                            val ext = "png"
                                            val fileName = "$file.$ext"

                                            Utils.downloader(
                                                activity,
                                                myPhotoUrlIs?.replace("\"", ""),
                                                Utils.instaDirPath,
                                                fileName
                                            )
                                            myPhotoUrlIs = ""
                                            try {
//                                                progressDralogGenaratinglink.dismiss()
                                                Utils.dismissDLoader()
                                            } catch (e: Exception) {
                                                e.printStackTrace()
                                            }
                                            // etText.setText("")
                                        }
                                    }
                                } else {
                                    val isVideo =
                                        modelInstagramResponse.modelGraphshortcode.shortcode_media.isIs_video
                                    if (isVideo) {
                                        myVideoUrlIs =
                                            modelInstagramResponse.modelGraphshortcode.shortcode_media.video_url


                                        val timeStamp = System.currentTimeMillis().toString()
                                        val file = "Insta_$timeStamp"
                                        val ext = "mp4"
                                        val fileName = "$file.$ext"

                                        Utils.downloader(
                                            activity,
                                            myVideoUrlIs?.replace("\"", ""),
                                            Utils.instaDirPath,
                                            fileName
                                        )

                                        try {
//                                            progressDralogGenaratinglink.dismiss()
                                            Utils.dismissDLoader()
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                        myVideoUrlIs = ""
                                    } else {
                                        myPhotoUrlIs =
                                            modelInstagramResponse.modelGraphshortcode.shortcode_media.display_resources[modelInstagramResponse.modelGraphshortcode.shortcode_media.display_resources.size - 1].src

                                        val timeStamp = System.currentTimeMillis().toString()
                                        val file = "Insta_$timeStamp"
                                        val ext = "png"
                                        val fileName = "$file.$ext"

                                        Utils.downloader(
                                            activity,
                                            myPhotoUrlIs?.replace("\"", ""),
                                            Utils.instaDirPath,
                                            fileName
                                        )
                                        try {
//                                            progressDralogGenaratinglink.dismiss()
                                            Utils.dismissDLoader()
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                        myPhotoUrlIs = ""
                                    }
                                }


                            } else {
                                Toast.makeText(
                                    activity,
                                    "error",
                                    Toast.LENGTH_SHORT
                                ).show()

                                try {
//                                    progressDralogGenaratinglink.dismiss()
                                    Utils.dismissDLoader()
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }

                            }


                        } catch (e: Exception) {
//                            this@MainActivity.runOnUiThread {
//                                progressDralogGenaratinglink.setMessage("Method 1 failed trying method 2")
//                            }
                            Utils.dismissDLoader()
                            activity.runOnUiThread(Runnable {
                                Toast.makeText(
                                    activity,
                                    "Media not available for download",
                                    Toast.LENGTH_SHORT
                                ).show()
                            })
//                            Toast.makeText(
//                                activity,
//                                "Method 1 failed trying method 2",
//                                Toast.LENGTH_SHORT
//                            ).show()

//                            downloadInstagramImageOrVideoResOkhttpM2(URL!!)

                        }

                    } else {
                        Utils.dismissDLoader()
                        activity.runOnUiThread(Runnable {
                            Toast.makeText(
                                activity,
                                "Media not available for download",
                                Toast.LENGTH_SHORT
                            ).show()
                        })
//                        this@MainActivity.runOnUiThread {
//                            progressDralogGenaratinglink.setMessage("Method 1 failed trying method 2")
//                        }
//                        Toast.makeText(
//                            activity,
//                            "Method 1 failed trying method 2",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                        downloadInstagramImageOrVideoResOkhttpM2(URL!!)
                    }


                } catch (e: Throwable) {
                    e.printStackTrace()
                    println("The request has failed " + e.message)
                    Utils.dismissDLoader()
                    activity.runOnUiThread(Runnable {
                        Toast.makeText(
                            activity,
                            "Media not available for download",
                            Toast.LENGTH_SHORT
                        ).show()
                    })
//                    Toast.makeText(
//                        activity,
//                        "Method 1 failed trying method 2",
//                        Toast.LENGTH_SHORT
//                    ).show()

//                    downloadInstagramImageOrVideoResOkhttpM2(URL!!)
                }
            }
        }.start()
    }
//    }


}