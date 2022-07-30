
package qrcodereader.qrscanner.qrcodescanner.barcodescanner.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItems
import com.afollestad.recyclical.setup
import com.afollestad.recyclical.withItem
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import qrcodereader.qrscanner.qrcodescanner.barcodescanner.R
import qrcodereader.qrscanner.qrcodescanner.barcodescanner.activity.utils.AppRater
import qrcodereader.qrscanner.qrcodescanner.barcodescanner.extra.*
import qrcodereader.qrscanner.qrcodescanner.barcodescanner.model.HomeScreen
import qrcodereader.qrscanner.qrcodescanner.barcodescanner.model.HomeScreenViewHolder

lateinit var recyclerView: RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var text_view_home_screen: TextView
    private lateinit var text_view_version_number: TextView

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView = findViewById(R.id.recycler_view)
        text_view_home_screen = findViewById(R.id.text_view_home_screen)
        text_view_version_number = findViewById(R.id.text_view_version_number)
        text_view_version_number.apply {
            text = "Version - ${packageManager.getPackageInfo(packageName, 0).versionName}"
        }

        val typeface = ResourcesCompat.getFont(this, R.font.work_sans_regular)
        text_view_home_screen.typeface = typeface

        setupRecyclerView()

        AppRater.appLaunched(this)

    }

    private fun setupRecyclerView() {
        recyclerView.setup {

            withDataSource(FillData.getHomeScreenData())
            withLayoutManager(GridLayoutManager(this@MainActivity, 2))
            withItem<HomeScreen, HomeScreenViewHolder>(R.layout.recyclerview_item) {
                onBind(::HomeScreenViewHolder) { index, item ->
                    menuName.apply {
                        val typeface = ResourcesCompat.getFont(this@MainActivity, R.font.work_sans_regular)
                        setTypeface(typeface)
                        text = item.menuName
                    }

                    icon.apply {
                        val drawableCompat = ContextCompat.getDrawable(this@MainActivity, item.iconDrawable)
                        val backgroundDrawable = DrawableCompat.wrap(drawableCompat!!).mutate()
                        DrawableCompat.setTint(backgroundDrawable, resources.getColor(R.color.home_screen_drawable))
                        setImageDrawable(backgroundDrawable)
                    }
                }
                onClick { index ->
                    when (FillData.getHomeScreenData()[index].menuName) {
                        "Scan" -> {
                            startActivity(Intent(this@MainActivity, ScanActivity::class.java))
                        }
                        "Generate" -> {
                            startActivity(Intent(this@MainActivity, GenerateQrCode::class.java))
                        }
                        "Feedback" -> {
                            val listFeedback = listOf(
                                    "Suggestion for new feature",
                                    "Report a bug ",
                                    "I like this app \uD83D\uDC4D",
                                    "Other"
                            )

                            simpleListDialog("Select your message", listFeedback) { t ->
                                val m = if (t == "Other") "Please enter your feedback" else t
                                sendFeedback(m)
                            }

                        }
                        "Rate us" -> {
                            val dialog = MaterialDialog(this@MainActivity)
                            val timer = object : CountDownTimer(7000, 1000) {
                                override fun onTick(millisUntilFinished: Long) {
                                    dialog.apply {
                                        cancelOnTouchOutside(false)
                                        title(text = "Rate us")
                                        message(text = "If you enjoy using our app take a moment to rate it. Send a feedback for any bugs/crashes.\n\nOpening play store in ${millisUntilFinished / 1000}s")
                                        show()
                                    }
                                }

                                override fun onFinish() {
                                    openUrl(APP_LINK)
                                    dialog.dismiss()
                                }
                            }

                            timer.start()
                        }

                        "More" -> {
                            val listFeedback = listOf(
                                    "Open Source Licence",
                                    "Privacy Policy",
                                    "FAQ"
                            )

                            simpleListDialog("More", listFeedback) { t ->
                                when (t) {
                                    "Open Source Licence" -> {
                                        startActivity(Intent(this@MainActivity, OssLicensesMenuActivity::class.java))
                                        OssLicensesMenuActivity.setActivityTitle("Notices")
                                    }

                                    "Privacy Policy" -> {
                                        openUrl(APP_PRIVACY_POLICY_LINK)
                                    }

                                    "FAQ" -> {
                                        MaterialDialog(this@MainActivity).show {
                                            message(R.string.qr_faq)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                onLongClick { index ->
                    vibrateDevice(this@MainActivity)
                }
            }
        }
    }


    private fun simpleListDialog(
            title: String = "",
            list: List<String>,
            onSelected: (String) -> Unit = {}
    ) {
        var selectedString: String

        MaterialDialog(this).show {
            debugMode(false)
            cornerRadius(16f)
            title(text = title)
            listItems(items = list) { _, _, text ->
                selectedString = text.toString()
                onSelected.invoke(selectedString)
            }
        }
    }

    private fun sendFeedback(message: String = "") {
        val isGmail = GAIL_PACKAGE_ID.isAppInstalled()
        var body = ""
        try {
            body = packageManager.getPackageInfo(packageName, 0).versionName
            body =
                    message + "\n\n-----------------------------\n Following details are collected to find the bugs/issues." +
                            " If you are not interested to share this you may delete it. \n" +
                            " \n Device OS version: " +
                            Build.VERSION.RELEASE + "\n App Version: " + body + "\n Device Brand: " + Build.BRAND +
                            "\n Device Model: " + Build.MODEL + "\n Device Manufacturer: " + Build.MANUFACTURER + "\n\n"
        } catch (e: Exception) {

        }

        try {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "message/rfc822"
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.email)))
            intent.putExtra(Intent.EXTRA_SUBJECT, "QR Code Scanner - Query")
            intent.putExtra(Intent.EXTRA_TEXT, body)

            if (isGmail) {
                intent.setPackage(GAIL_PACKAGE_ID)
            }

            this.startActivity(Intent.createChooser(intent, "Select Email App"))

        } catch (e: Exception) {

        }
    }

    private fun String.isAppInstalled(): Boolean {
        val pm = packageManager!!
        try {
            pm.getPackageInfo(this, PackageManager.GET_ACTIVITIES)
            return true
        } catch (e: PackageManager.NameNotFoundException) {

        }
        return false
    }

    private fun openUrl(string: String = "") {

        val uri: Uri = Uri.parse(string)

        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_MULTIPLE_TASK

        try {
            startActivity(intent)
        } catch (e: java.lang.RuntimeException) {
            startActivity(
                    Intent(Intent.ACTION_VIEW,
                            Uri.parse(string)))
        }
    }
}
