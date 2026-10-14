package com.swnishan.materialdatetimepicker.common

import android.content.Context
import android.content.res.Resources
import androidx.annotation.DimenRes

class Utils {
    companion object {
        fun dpToPx(dp: Float): Float = (dp * Resources.getSystem().displayMetrics.density)

        fun dimenToPx(
            context: Context,
            @DimenRes dimenRes: Int,
        ): Int = context.resources.getDimensionPixelSize(dimenRes)
    }
}
