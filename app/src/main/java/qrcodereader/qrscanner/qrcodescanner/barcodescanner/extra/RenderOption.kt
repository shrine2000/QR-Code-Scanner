
package qrcodereader.qrscanner.qrcodescanner.barcodescanner.extra

import com.github.sumimakito.awesomeqr.option.background.Background
import com.github.sumimakito.awesomeqr.option.color.Color
import com.github.sumimakito.awesomeqr.option.logo.Logo
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel

data class RenderOption(
        var content: String = APP_LINK,
        var size: Int = 600,
        var borderWidth: Int = 30,
        var ecl: ErrorCorrectionLevel = ErrorCorrectionLevel.M,
        var patternScale: Float = 0.4f,
        var roundPattern: Boolean = false,
        var clearBorder: Boolean = true,
        var color: Color =  Color(auto = false,
                background = android.graphics.Color.WHITE,
                light = android.graphics.Color.WHITE,
                dark = android.graphics.Color.BLACK
        ),
        var logo: Logo = Logo().duplicate(),
        var background: Background? = null
)
