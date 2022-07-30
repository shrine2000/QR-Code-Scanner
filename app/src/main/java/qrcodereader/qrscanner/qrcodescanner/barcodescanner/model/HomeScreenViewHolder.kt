
package qrcodereader.qrscanner.qrcodescanner.barcodescanner.model

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView


import qrcodereader.qrscanner.qrcodescanner.barcodescanner.R

class HomeScreenViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
    val menuName: TextView = itemView.findViewById(R.id.tv_scan)
    val icon: ImageView = itemView.findViewById(R.id.image_view_icon)
}
