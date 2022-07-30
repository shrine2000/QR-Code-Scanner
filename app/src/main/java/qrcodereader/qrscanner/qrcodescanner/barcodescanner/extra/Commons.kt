

package qrcodereader.qrscanner.qrcodescanner.barcodescanner.extra

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import com.google.zxing.BarcodeFormat
import java.lang.reflect.Method


private var toast: Toast? = null

fun vibrateDevice(context: Context) {
    val vibrator = getSystemService(context, Vibrator::class.java)
    vibrator?.let {
        if (Build.VERSION.SDK_INT >= 26) {
            it.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            it.vibrate(100)
        }
    }
}


fun Context.toast(string: String) {
    toast?.cancel()

    toast = Toast.makeText(this, string, Toast.LENGTH_SHORT).apply {
        show()
    }
}

fun Activity.makeStatusBarTransparent() {
    window.apply {
        clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        } else {
            decorView.systemUiVisibility = SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
        statusBarColor = Color.TRANSPARENT
    }

}
fun View.setMarginTop(marginTop: Int) {
    val menuLayoutParams = this.layoutParams as ViewGroup.MarginLayoutParams
    menuLayoutParams.setMargins(0, marginTop, 0, 0)
    this.layoutParams = menuLayoutParams
}

object SupportedBarcodeFormats {
    val FORMATS = listOf(
        BarcodeFormat.QR_CODE,
        BarcodeFormat.DATA_MATRIX,
        BarcodeFormat.AZTEC,
        BarcodeFormat.PDF_417,
        BarcodeFormat.EAN_13,
        BarcodeFormat.EAN_8,
        BarcodeFormat.UPC_E,
        BarcodeFormat.UPC_A,
        BarcodeFormat.CODE_128,
        BarcodeFormat.CODE_93,
        BarcodeFormat.CODE_39,
        BarcodeFormat.CODABAR,
        BarcodeFormat.ITF
    )
}

enum class QRCodeType {
    WEBSITE,
    TEXT,
    WIFI,
   // EVENT,
   // CONTACT,
    EMAIL,
    PHONE,
    SMS
}

val generateQRCodeType = mutableListOf<Pair<QRCodeType, String>>().apply {
    add(Pair(QRCodeType.WEBSITE, "Website"))
    add(Pair(QRCodeType.TEXT, "Text"))
    add(Pair(QRCodeType.WIFI, "WiFi"))
   // add(Pair(QRCodeType.EVENT, "Event"))
   // add(Pair(QRCodeType.CONTACT, "Contact"))
    add(Pair(QRCodeType.EMAIL, "Email"))
    add(Pair(QRCodeType.PHONE, "Phone"))
    add(Pair(QRCodeType.SMS, "SMS"))
}




fun getWifiQrCodeString(ssid: String, type: String, password: String, hidden: Boolean): String{
    return "WIFI:S:$ssid;T:$type;P:$password;H:$hidden;"
}


fun getEmailQrCodeString(to: String, sub: String, body: String) : String{
    return  "MATMSG:TO:$to;SUB:$sub;BODY:$body;;"
}


fun getBitcoinQrCodeString(address: String, amount: String, message: String): String{
    return  "bitcoin:$address?amount=$amount&message=$message"
}

fun getAllColors(){
    val arr = arrayListOf<String>()
    val c: Class<*> = Colors::class.java
    val method: Array<Method> = c.methods


    method.forEach {
            it.invoke(it)
    }

    val modifers = c.modifiers

}


private val DIALOG_COLORS = intArrayOf(
    Colors.brickRedColor(),
    Colors.infoBlueColor(),
    Colors.successColor(),
    Colors.warmGrayColor(),
    Colors.warningColor(),
    Colors.watermelonColor(),
    Colors.waveColor(),
    Colors.almondColor(),
    Colors.antiqueWhiteColor(),
    Colors.babyBlueColor(),
    Colors.backgroundDarkColor(),
    Colors.backgroundHoloDarkColor(),
    Colors.bananaColor(),
    Colors.beigeColor(),
    Colors.dangerColor(),
    Colors.oldLaceColor(),
    Colors.oliveColor(),
    Colors.oliveDrabColor(),
    Colors.orchidColor(),
    Colors.ivoryColor(),
    Colors.eggshellColor(),
    Colors.seafoamColor(),
    Colors.seashellColor(),
    Colors.goldColor(),
    Colors.goldColor(),
    Colors.goldenrodColor(),
    Colors.ghostWhiteColor(),
    Colors.grapeColor(),
    Colors.grassColor(),
    Colors.snowColor(),
    Colors.black25PercentColor(),
    Colors.cactusGreenColor(),
    Colors.cantaloupeColor(),
    Colors.cardTableColor(),
    Colors.carrotColor(),
    Colors.charcoalColor(),
    Colors.chartreuseColor(),
    Colors.chiliPowderColor(),
    Colors.chocolateColor(),
    Colors.coffeeColor(),
    Colors.cornflowerColor(),
    Colors.tealColor(),
    Colors.tomatoColor(),
    Colors.yellowGreenColor(),
    Colors.icebergColor(),
    Colors.indianRedColor(),
    Colors.indigoColor(),
    Colors.paleGreenColor(),
    Colors.palePurpleColor(),
    Colors.paleRoseColor(),
    Colors.pinkColor(),
    Colors.pinkColor(),
    Colors.pinkLipstickColor(),
    Colors.easterPinkColor(),
    Colors.pastelOrangeColor(),
    Colors.pastelPurpleColor(),
    Colors.pastelBlueColor(),
    Colors.periwinkleColor(),
    Colors.peachColor(),
    Colors.siennaColor(),
    Colors.steelBlueColor(),
    Colors.strawberryColor(),
    Colors.denimColor(),
    Colors.dimForegroundDisabledHoloDarkColor(),
    Colors.dimForegroundDisabledHoloLightColor(),
    Colors.dimForegroundHoloDarkColor(),
    Colors.dimForegroundHoloLightColor(),
    Colors.dustColor(),
    Colors.fadedBlueColor(),
    Colors.fuschiaColor(),
    Colors.grapefruitColor(),
    Colors.hollyGreenColor(),
    Colors.honeydewColor(),
    Colors.lavenderColor(),
    Colors.lightCreamColor(),
    Colors.limeColor(),
    Colors.linenColor(),
    Colors.chartreuseColor(),
    Colors.coolGrayColor(),
    Colors.violetColor(),
    Colors.mandarinColor(),
    Colors.maroonColor(),
    Colors.moneyGreenColor(),
    Colors.mudColor(),
    Colors.mustardColor()
)

fun getColors(): IntArray {
    return DIALOG_COLORS.sortedWith { o1, o2 ->
        o1.compareTo(o2)
    }.toSet().toIntArray()
}