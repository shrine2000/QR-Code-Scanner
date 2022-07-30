

package qrcodereader.qrscanner.qrcodescanner.barcodescanner.activity

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.RectF
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.InputType
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import androidx.core.content.FileProvider
import androidx.core.widget.doOnTextChanged
import coil.load
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.color.colorChooser
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.input.input
import com.afollestad.materialdialogs.list.listItems
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.fondesa.kpermissions.allGranted
import com.fondesa.kpermissions.extension.isPermissionGranted
import com.fondesa.kpermissions.extension.permissionsBuilder

import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.sumimakito.awesomeqr.AwesomeQrRenderer
import com.github.sumimakito.awesomeqr.RenderResult
import com.github.sumimakito.awesomeqr.option.RenderOption
import com.github.sumimakito.awesomeqr.option.logo.Logo
import com.google.android.material.textview.MaterialTextView
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper
import qrcodereader.qrscanner.qrcodescanner.barcodescanner.R
import qrcodereader.qrscanner.qrcodescanner.barcodescanner.extra.*
import java.io.*
import java.net.URI
import java.util.*


private lateinit var dataRenderOptions: qrcodereader.qrscanner.qrcodescanner.barcodescanner.extra.RenderOption
private lateinit var qr_result: Bitmap

class GenerateQrCode : AppCompatActivity() {

    private lateinit var switch_rounded_pattern: SwitchCompat
    private lateinit var switch_clear_border: SwitchCompat
    private lateinit var text_view_type: MaterialTextView
    private lateinit var text_view_message: MaterialTextView
    private lateinit var text_view_size: MaterialTextView
    private lateinit var text_view_border_width: MaterialTextView
    private lateinit var text_view_pattern_scale: MaterialTextView
    private lateinit var text_view_color: MaterialTextView
    private lateinit var text_view_background: MaterialTextView
    private lateinit var text_view_logo: MaterialTextView
    private lateinit var text_view_color_blank: MaterialTextView
    private lateinit var text_view_color_non_blank: MaterialTextView
    private lateinit var text_view_color_background: MaterialTextView

    private lateinit var image_view_qr_code_preview: ImageView
    private lateinit var image_view_logo_preview: ImageView

    private lateinit var button_share: Button
    private lateinit var button_save: Button



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generate_qr_code)
        setSupportActionBar(findViewById(R.id.toolbar))
        findViewById<Toolbar>(R.id.toolbar).apply {
            setBackgroundColor(android.graphics.Color.WHITE)
            navigationIcon?.mutate()?.let {
                it.setTint(android.graphics.Color.BLACK)
                navigationIcon = it
            }

            setNavigationOnClickListener {
                this@GenerateQrCode.finish()
            }
            title = "Generate"
            setTitleTextColor(android.graphics.Color.BLACK)
        }

        initLayout()


        dataRenderOptions = qrcodereader.qrscanner.qrcodescanner.barcodescanner.extra.RenderOption()

        initQRCodeType()
        handleOnClick()
        generateQrCode()
        initMessage(generateQRCodeType[1])
    }

    private fun handleOnClick() {
        button_save.setOnClickListener { buttonSave() }
        button_share.setOnClickListener { buttonShare() }
        switch_clear_border.setOnClickListener { generateQrCode() }
        switch_rounded_pattern.setOnClickListener { generateQrCode() }

        text_view_size.setOnClickListener { setSize() }
        text_view_border_width.setOnClickListener { setBorder() }
        text_view_pattern_scale.setOnClickListener { setPatternScale() }
        // text_view_color.setOnClickListener { setColor() }
        // text_view_background.setOnClickListener { setBackground() }
        text_view_logo.setOnClickListener { setLogo() }

        text_view_color_blank.setOnClickListener { setColorBlank() }
        text_view_color_non_blank.setOnClickListener { setColorNonBlank() }
        text_view_color_background.setOnClickListener { setColorBackground() }


        text_view_color_background.setBackgroundColor(dataRenderOptions.color.background)
        text_view_color_non_blank.setBackgroundColor(dataRenderOptions.color.dark)
        text_view_color_blank.setBackgroundColor(dataRenderOptions.color.light)
    }

    private fun setColorBackground() {

        MaterialDialog(this).show {
            title(text = "Select Light Color")
            colorChooser(
                colors = getColors(),
                allowCustomArgb = true,
                showAlphaSelector = true
            ) { dialog, color ->
                text_view_color_background.setBackgroundColor(color)
                dataRenderOptions.color.background = color
                generateQrCode()
            }
            positiveButton(text = "OK"){
                generateQrCode()
            }
        }
    }

    private fun setColorNonBlank() {
        MaterialDialog(this).show {
            title(text = "Select Dark Color")
            colorChooser(
                colors = getColors(),
                    allowCustomArgb = true,
                    showAlphaSelector = true
            ) { dialog, color ->
                text_view_color_non_blank.setBackgroundColor(color)
                dataRenderOptions.color?.dark = color
                generateQrCode()
            }
            positiveButton(text = "OK"){

            }
        }
    }

    private fun setColorBlank() {
        MaterialDialog(this).show {
            title(text = "Select Light Color")
            colorChooser(
                colors = getColors(),
                allowCustomArgb = true,
                showAlphaSelector = true
            ) { dialog, color ->
                text_view_color_blank.setBackgroundColor(color)
                dataRenderOptions.color.light = color
                generateQrCode()
            }
            positiveButton(text = "OK") {
                generateQrCode()
            }
        }
    }

    private fun setLogo() {
        ImagePicker.with(this)
                .cropSquare()                 //Crop image(Optional), Check Customization for more option
                .compress(1024)            //Final image size will be less than 1 MB(Optional)
                .maxResultSize(
                        1080,
                        1080
                )    //Final image resolution will be less than 1080 x 1080(Optional)
                .start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                //Image Uri will not be null for RESULT_OK
                val fileUri = data?.data!!
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, fileUri)

                val logo = Logo()
                logo.bitmap = bitmap
                logo.borderRadius = 10 // radius for logo's corners
                logo.borderWidth = 10 // width of the border to be added around the logo
                logo.scale = 0.245f // scale for the logo in the QR code
                logo.clippingRect = RectF(
                        0f,
                        0f,
                        150f,
                        150f
                )// crop the logo image before applying it to the QR code

                image_view_logo_preview.load(bitmap) {
                    crossfade(true)
                }

                dataRenderOptions.logo = logo

                generateQrCode()
            }
            ImagePicker.RESULT_ERROR -> {
                Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            }
            else -> {
                Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setColor() {

    }

    private fun setPatternScale() {
        val list = arrayOf(0.3f, 0.4f, 0.5f, 0.6f)
        MaterialDialog(this).show {
            listItemsSingleChoice(
                    items = list.map { it.toString() + "f" },
                    initialSelection = list.indexOf(dataRenderOptions.patternScale)
            ) { _: MaterialDialog, i: Int, _: CharSequence ->
                dataRenderOptions.patternScale = list[i].toFloat()
                generateQrCode()
            }
        }
    }

    private fun setBorder() {
        val list = arrayOf(10, 20, 30, 40, 50, 60, 70, 80)
        MaterialDialog(this).show {
            listItemsSingleChoice(
                    items = list.map { "$it px" },
                    initialSelection = list.indexOf(dataRenderOptions.borderWidth)
            ) { _: MaterialDialog, i: Int, _: CharSequence ->
                dataRenderOptions.borderWidth = list[i]
                generateQrCode()
            }
        }
    }

    private fun setBackground() {
        TODO("Not yet implemented")
    }

    private fun setSize() {
        val list = arrayOf(300, 500, 600, 800, 1000, 1200, 1500, 2000)
        MaterialDialog(this).show {
            listItemsSingleChoice(
                    items = list.map { "$it px" },
                    initialSelection = list.indexOf(dataRenderOptions.size)
            ) { _: MaterialDialog, i: Int, _: CharSequence ->
                dataRenderOptions.size = list[i]
                generateQrCode()
            }
        }
    }


    private fun initMessage(pair: Pair<QRCodeType, String>) {

        text_view_message.setOnClickListener {
            when (pair.first) {
                QRCodeType.PHONE -> {
                    MaterialDialog(this).show {
                        title(text = "Phone")

                        var input = ""
                        input(
                                hint = "Phone",
                                inputType = InputType.TYPE_CLASS_PHONE
                        ) { _: MaterialDialog, charSequence: CharSequence ->
                            input = charSequence.toString()
                            dataRenderOptions.content = "tel:$input"
                            generateQrCode()
                        }
                        negativeButton(text = "Dismiss") {
                            this.dismiss()
                        }
                    }
                }
                QRCodeType.WIFI -> {
                    MaterialDialog(this).show {
                        title(text = "WiFi")
                        var network_type = ""
                        val type = arrayOf("WPA/WPA2", "WEP", "None")
                        customView(R.layout.custom_view_wifi)
                        val view = getCustomView()
                        val edit_text_network_name = view.findViewById<EditText>(R.id.edit_text_network_name)
                        val edit_text_password = view.findViewById<EditText>(R.id.edit_text_password)
                        val check_box_is_hidden = view.findViewById<CheckBox>(R.id.check_box_is_hidden)
                        val spinner_encryption = view.findViewById<Spinner>(R.id.spinner_encryption)
                        ArrayAdapter(
                                applicationContext,
                                android.R.layout.simple_spinner_item,
                                type
                        ).also { adapter ->
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            spinner_encryption.adapter = adapter
                            spinner_encryption.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                                override fun onNothingSelected(parent: AdapterView<*>?) {

                                }

                                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                    network_type = type[position]
                                }
                            }
                        }

                        dataRenderOptions.content = getWifiQrCodeString(edit_text_network_name.text.toString(),
                                network_type,
                                edit_text_password.text.toString(),
                                check_box_is_hidden.isChecked
                        )

                        generateQrCode()

                        positiveButton {
                            generateQrCode()
                            dismiss()
                        }
                    }

                }
                QRCodeType.EMAIL -> {
                    MaterialDialog(this).show {
                        title(text = "Email")
                        customView(R.layout.custom_view_email)
                        val view = getCustomView()
                        val edit_text_email = view.findViewById<EditText>(R.id.edit_text_email)

                        val edit_text_subject = view.findViewById<EditText>(R.id.edit_text_subject)
                        val edit_text_message_email = view.findViewById<EditText>(R.id.edit_text_message_email)

                        positiveButton(text = "OK") {
                            dataRenderOptions.content = getEmailQrCodeString(
                                    edit_text_email.text.toString(),
                                    edit_text_subject.text.toString(),
                                    edit_text_message_email.text.toString()
                            )

                            generateQrCode()
                            dismiss()
                        }
                    }
                }

                QRCodeType.SMS -> {

                    MaterialDialog(this).show {
                        title(text = "SMS")
                        customView(R.layout.custom_view_sms)
                        val view = getCustomView()
                        val edit_text_phone_number = view.findViewById<EditText>(R.id.edit_text_phone_number)
                        val edit_text_message_sms = view.findViewById<EditText>(R.id.edit_text_message_sms)


                        edit_text_message_sms.doOnTextChanged { text, start, before, count ->

                            dataRenderOptions.content = "SMSTO:${edit_text_phone_number.text}:${edit_text_message_sms.text}"

                            generateQrCode()

                        }

                        positiveButton {
                            dataRenderOptions.content = "SMSTO:${edit_text_phone_number.text}:${edit_text_message_sms.text}"

                            generateQrCode()
                            dismiss()
                        }
                    }
                }

                QRCodeType.WEBSITE -> {
                    MaterialDialog(this).show {
                        var input = ""
                        input(
                                hint = "Enter Url"
                        ) { _: MaterialDialog, charSequence: CharSequence ->
                            input = charSequence.toString()
                            dataRenderOptions.content = input
                            generateQrCode()
                        }
                        negativeButton(text = "Dismiss") {
                            generateQrCode()
                            this.dismiss()
                        }
                    }
                }


                QRCodeType.TEXT -> {
                    MaterialDialog(this).show {
                        cornerRadius(16f)
                        title(text = "Text")

                        input { materialDialog: MaterialDialog, charSequence: CharSequence ->
                            dataRenderOptions.content = "" + charSequence.toString()
                            generateQrCode()
                        }

                        negativeButton(text = "Dismiss") {
                            this.dismiss()
                        }
                    }
                }
            }
        }
    }

    private fun initQRCodeType() {
        text_view_type.setOnClickListener {
            MaterialDialog(this).show {
                cornerRadius(16f)
                listItems(items = generateQRCodeType.map { it.second }) { materialDialog: MaterialDialog, i: Int, charSequence: CharSequence ->
                    initMessage(generateQRCodeType[i])
                    text_view_type.text = generateQRCodeType[i].second
                }
            }
        }
    }

    private fun generateQrCode() {

        val renderOption = RenderOption()
        renderOption.content = dataRenderOptions.content
        renderOption.size = dataRenderOptions.size
        renderOption.borderWidth = dataRenderOptions.borderWidth
        renderOption.ecl = dataRenderOptions.ecl
        renderOption.patternScale = dataRenderOptions.patternScale
        renderOption.roundedPatterns = switch_rounded_pattern.isChecked
        renderOption.clearBorder = switch_clear_border.isChecked
        renderOption.color = dataRenderOptions.color
        // renderOption.background = background // set a background, keep reading to find more about it
        renderOption.logo = dataRenderOptions.logo
        AwesomeQrRenderer.renderAsync(renderOption, { result ->
            when {
                result.bitmap != null -> {
                    runOnUiThread {
                        qr_result = result.bitmap!!
                        image_view_qr_code_preview.load(result.bitmap) {
                            crossfade(true)
                        }
                    }
                }
                result.type == RenderResult.OutputType.GIF -> {
                    // If your Background is a GifBackground, the image
                    // will be saved to the output file set in GifBackground
                    // instead of being returned here. As a result, the
                    // result.bitmap will be null.
                }
                result.type == RenderResult.OutputType.Still -> {

                }
                result.type == RenderResult.OutputType.Blend -> {
                    // Oops, something gone wrong.
                }
                else -> {
                    // Oops, something gone wrong.
                }
            }
        }, { exception ->
            exception.printStackTrace()
            runOnUiThread {
                toast(exception.toString())
            }
        })

    }

    private fun initLayout() {
        switch_clear_border = findViewById(R.id.switch_clear_border)
        switch_rounded_pattern = findViewById(R.id.switch_rounded_pattern)
        text_view_type = findViewById(R.id.text_view_type)
        image_view_qr_code_preview = findViewById(R.id.image_view_qr_code_preview)
        image_view_logo_preview = findViewById(R.id.image_view_logo_preview)
        text_view_message = findViewById(R.id.text_view_message_material)
        button_save = findViewById(R.id.button_save)
        button_share = findViewById(R.id.button_share)

        text_view_size = findViewById(R.id.text_view_size)
        text_view_border_width = findViewById(R.id.text_view_border_width)
        text_view_pattern_scale = findViewById(R.id.text_view_pattern_scale)
        text_view_color = findViewById(R.id.text_view_color)
        // text_view_background = findViewById(R.id.text_view_background)
        text_view_logo = findViewById(R.id.text_view_logo)

        text_view_color_blank = findViewById(R.id.text_view_color_blank)
        text_view_color_non_blank = findViewById(R.id.text_view_color_non_blank)
        text_view_color_background = findViewById(R.id.text_view_color_background)

        val scrollView = findViewById<View>(R.id.scroll_view) as ScrollView
        OverScrollDecoratorHelper.setUpOverScroll(scrollView)
    }


    private fun buttonShare() {
        val imageUri = saveImage(qr_result, System.currentTimeMillis().toString())
        imageUri?.let {
            shareQrCode(imageUri)
        }
    }

    private fun buttonSave() {
        saveImage(
                qr_result, System.currentTimeMillis().toString()
        )
    }

    private fun saveImage(bitmap: Bitmap, name: String): Uri? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val resolver = contentResolver
            val contentValues = ContentValues()
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/" + "Qr-Code-Scanner")
            val imageUri =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            val fos = resolver.openOutputStream(imageUri!!)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            fos!!.flush()
            fos.close()

            toast("Saved to gallery")

            return imageUri

        } else {
            if (isPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                val imagesDir = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DCIM
                ).toString() + File.separator + "Qr-Code-Scanner"
                val file = File(imagesDir)
                if (!file.exists()) {
                    file.mkdir()
                }
                val image = File(imagesDir, "$name.png")

                val fos = FileOutputStream(image)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
                fos.flush()
                fos.close()

                return getImageContentUri(image)
            } else {

                val request by lazy {
                    permissionsBuilder(Manifest.permission.WRITE_EXTERNAL_STORAGE).build()
                }

                request.addListener {
                    if (it.allGranted()) {
                        toast("Write permission obtained. Try again")
                    } else toast("Kindly grant permission to write external storage to save QR code")
                }
                return null
            }
        }
    }

    private fun getImageContentUri(imageFile: File): Uri? {
        val filePath = imageFile.absolutePath
        val cursor: Cursor = contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, arrayOf(MediaStore.Images.Media._ID),
                MediaStore.Images.Media.DATA + "=? ", arrayOf(filePath), null)!!
        return if (cursor.moveToFirst()) {
            val id: Int = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID))
            cursor.close()
            Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + id)
        } else {
            if (imageFile.exists()) {
                val values = ContentValues()
                values.put(MediaStore.Images.Media.DATA, filePath)
                contentResolver.insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            } else {
                null
            }
        }
    }

    private fun shareQrCode(uri: Uri) {
        val file = FileUtils.getFileFromUri(applicationContext, uri)

        if (file.exists()) {
            val bmpUri = FileProvider.getUriForFile(
                    applicationContext,
                    "qrcodereader.qrscanner.qrcodescanner.barcodescanner",
                    file
            )
            val intent = Intent().apply {
                this.action = Intent.ACTION_SEND
                this.putExtra(Intent.EXTRA_STREAM, bmpUri)
                this.putExtra(Intent.EXTRA_TEXT, "")
                this.type = "image/jpeg"
            }
            startActivity(intent)
        } else {
            toast("File not found")
        }
    }
}


