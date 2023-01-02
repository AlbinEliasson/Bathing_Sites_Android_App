package se.miun.alel2104.dt031g.bathingsites

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout

class BathingSitesView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
    defStyleRes: Int = 0
) : ConstraintLayout(context, attrs, defStyle, defStyleRes) {

    private var view: View = View.inflate(context, R.layout.bathing_sites_view, this)

    init {
        initialize(attrs)
    }

    private fun initialize(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs,
            R.styleable.BathingSitesView, 0, 0)
        val bathingSitesTitle = typedArray.getString(
            R.styleable.BathingSitesView_bathing_sites_title).toString()

        typedArray.recycle()
    }

}