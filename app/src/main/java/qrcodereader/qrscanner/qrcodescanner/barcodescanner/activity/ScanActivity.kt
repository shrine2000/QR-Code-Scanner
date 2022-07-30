
package qrcodereader.qrscanner.qrcodescanner.barcodescanner.activity

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onCancel
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.budiyev.android.codescanner.*
import com.fondesa.kpermissions.allGranted
import com.fondesa.kpermissions.extension.isPermissionGranted
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.fondesa.kpermissions.extension.send
import com.google.zxing.Result
import qrcodereader.qrscanner.qrcodescanner.barcodescanner.R
import qrcodereader.qrscanner.qrcodescanner.barcodescanner.extra.SupportedBarcodeFormats
import qrcodereader.qrscanner.qrcodescanner.barcodescanner.extra.applySystemWindowInsets
import qrcodereader.qrscanner.qrcodescanner.barcodescanner.extra.toast


class ScanActivity : AppCompatActivity() {

    companion object {
        private val PERMISSIONS = Manifest.permission.CAMERA
    }

    private var maxZoom: Int = 0
    private val zoomStep = 5
    private lateinit var codeScanner: CodeScanner
    private lateinit var image_view_flash: ImageView
    private lateinit var image_view_scan_from_file: ImageView
    private lateinit var scanner_view: CodeScannerView
    private lateinit var seek_bar_zoom: SeekBar
    private lateinit var layout_flash_container: FrameLayout
    private lateinit var layout_scan_from_file_container: FrameLayout
    private lateinit var button_decrease_zoom: ImageView
    private lateinit var button_increase_zoom: ImageView


    private val request by lazy {
        permissionsBuilder(Manifest.permission.CAMERA).build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)

        initToolbar()
        supportEdgeToEdge()
        initZoomSeekBar()
        initFlashButton()

        requestPermission()
        handleDecreaseZoomClicked()
        handleIncreaseZoomClicked()
        handleZoomChanged()
    }

    private fun initToolbar() {

    }

    private fun requestPermission() {
        request.send { result ->
            when {
                // result.anyPermanentlyDenied() -> showPermanentlyDeniedDialog(result)
                // result.anyShouldShowRationale() -> showRationaleDialog(result, request)
                result.allGranted() -> {
                    initScanner()
                }
            }
        }
    }


    private fun initScanner() {
        scanner_view = findViewById(R.id.scanner_view)
        codeScanner = CodeScanner(this, scanner_view).apply {
            camera = CodeScanner.CAMERA_BACK
            autoFocusMode = AutoFocusMode.SAFE
            formats = SupportedBarcodeFormats.FORMATS
            scanMode = ScanMode.SINGLE
            isAutoFocusEnabled = true
            isFlashEnabled = false
            isTouchFocusEnabled = false
            decodeCallback = DecodeCallback(::handleScannedBarcode)
            startPreview()
        }
    }

    private fun handleScannedBarcode(result: Result) {
        runOnUiThread {
            MaterialDialog(this@ScanActivity).show {
                cornerRadius(16f)
                cancelOnTouchOutside(false)
                message(text = result.toString())
                positiveButton(text = "SHARE") {
                    shareIntent(result.toString())
                }

                negativeButton(text = "SCAN") {
                    this.dismiss()
                    restartPreview()
                }

                neutralButton(text = "COPY") {
                    val clipboard: ClipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("QR-Code-App", result.toString())
                    clipboard.setPrimaryClip(clip)
                    toast("Copied")
                    restartPreview()
                }

                onDismiss {
                    restartPreview()
                }

                onCancel {
                    restartPreview()
                }
            }
        }
    }


    private fun supportEdgeToEdge() {
        image_view_flash = findViewById(R.id.image_view_flash)
        image_view_scan_from_file = findViewById(R.id.image_view_scan_from_file)
        image_view_flash.applySystemWindowInsets(applyTop = true)
        image_view_scan_from_file.applySystemWindowInsets(applyTop = true)
    }


    private fun restartPreview() {
        if (::codeScanner.isInitialized){
            codeScanner.startPreview()
        }
     }

    private fun toggleFlash() {
        image_view_flash.isActivated = image_view_flash.isActivated.not()
        codeScanner.isFlashEnabled = codeScanner.isFlashEnabled.not()
    }


    private fun initZoomSeekBar() {
        seek_bar_zoom = findViewById(R.id.seek_bar_zoom)
    }

    private fun initFlashButton() {
        layout_flash_container = findViewById(R.id.layout_flash_container)
        layout_flash_container.setOnClickListener {
            toggleFlash()
        }

    }

    private fun handleScanFromFileClicked() {
        layout_scan_from_file_container = findViewById(R.id.layout_scan_from_file_container)
        layout_scan_from_file_container.setOnClickListener {

        }
    }

    private fun handleZoomChanged() {
        seek_bar_zoom.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    codeScanner.zoom = progress
                }
            }
        })
    }

    private fun handleDecreaseZoomClicked() {
        button_decrease_zoom = findViewById(R.id.button_decrease_zoom)
        button_decrease_zoom.setOnClickListener {
            decreaseZoom()
        }
    }

    private fun handleIncreaseZoomClicked() {
        button_increase_zoom = findViewById(R.id.button_increase_zoom)
        button_increase_zoom.setOnClickListener {
            increaseZoom()
        }
    }

    private fun decreaseZoom() {
        codeScanner.apply {
            if (zoom > zoomStep) {
                zoom -= zoomStep
            } else {
                zoom = 0
            }
            seek_bar_zoom.progress = zoom
        }
    }

    private fun increaseZoom() {
        codeScanner.apply {
            if (zoom < maxZoom - zoomStep) {
                zoom += zoomStep
            } else {
                zoom = maxZoom
            }
            seek_bar_zoom.progress = zoom
        }
    }

    private fun shareIntent(message: String) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_SUBJECT, "")
        intent.putExtra(Intent.EXTRA_TEXT, message)
        startActivity(Intent.createChooser(intent, "Share"))
    }

    override fun onResume() {
        super.onResume()
        if (this.isPermissionGranted(PERMISSIONS)) {
            initZoomSeekBar()
            if(::codeScanner.isInitialized){
                codeScanner.startPreview()
            }
        }
    }

    override fun onPause() {
       if(::codeScanner.isInitialized){
           codeScanner.releaseResources()
       }
        super.onPause()
    }
}