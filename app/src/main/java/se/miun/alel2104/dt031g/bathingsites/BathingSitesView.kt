package se.miun.alel2104.dt031g.bathingsites

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout

class BathingSitesView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
    defStyleRes: Int = 0
) : ConstraintLayout(context, attrs, defStyle, defStyleRes) {

    private var view: View = View.inflate(context, R.layout.bathing_sites_view, this)
    private var numberOfBathingSites = 0

    init {
        initialize(attrs)
        initBathingButtonListener()
    }

    private fun initialize(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs,
            R.styleable.BathingSitesView, 0, 0)
        val bathingSitesTitle = typedArray.getString(
            R.styleable.BathingSitesView_bathing_sites_title).toString()

        typedArray.recycle()

        setupTitleView()
    }

    private fun setupTitleView() {
        val bathingSiteTitle = resources.getString(R.string.default_bathing_sites_title, numberOfBathingSites)
        view.findViewById<TextView>(R.id.bathing_sites_title).text = bathingSiteTitle

    }

    private fun initBathingButtonListener() {
        view.setOnClickListener {
            numberOfBathingSites += 1
            setupTitleView()
            getBathingSites()
        }
    }

    private fun getBathingSites() {
        val dataBase = context?.let { AppDataBase.getDatabase(it) }
        val bathingSiteDao = dataBase?.BathingSiteDao()

        if (bathingSiteDao != null) {
            println(bathingSiteDao.getAllBathingSites())
        }
    }
}
