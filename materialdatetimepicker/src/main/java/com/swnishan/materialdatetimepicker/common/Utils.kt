package com.swnishan.materialdatetimepicker.common

import android.content.Context
import android.content.res.Resources
import androidx.annotation.DimenRes

class Utils {
    companion object {
        fun dpToPx(dp: Float): Float {
            return (dp * Resources.getSystem().displayMetrics.density)
        }

        fun dimenToPx(context: Context, @DimenRes dimenRes: Int): Int {
            return context.resources.getDimensionPixelSize(dimenRes)
        }
    }
}
