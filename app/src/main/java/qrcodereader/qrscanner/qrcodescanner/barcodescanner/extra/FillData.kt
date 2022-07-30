
package qrcodereader.qrscanner.qrcodescanner.barcodescanner.extra

import android.graphics.drawable.Drawable
import com.afollestad.recyclical.datasource.DataSource
import com.afollestad.recyclical.datasource.emptyDataSourceTyped
import qrcodereader.qrscanner.qrcodescanner.barcodescanner.R
import qrcodereader.qrscanner.qrcodescanner.barcodescanner.model.HomeScreen

object FillData {

    fun getHomeScreenData(): DataSource<HomeScreen>{
        val arrayList = emptyDataSourceTyped<HomeScreen>()
        arrayList.add(HomeScreen("Scan", R.drawable.ic_baseline_qr_code_scanner_24))
        arrayList.add(HomeScreen("Generate", R.drawable.ic_baseline_generate_24))
        arrayList.add(HomeScreen("Feedback", R.drawable.ic_baseline_feedback_24))
        arrayList.add(HomeScreen("More", R.drawable.ic_baseline_more_24))
        arrayList.add(HomeScreen("Rate us", R.drawable.ic_baseline_star_rate_24))
        return arrayList
    }
}