package se.miun.alel2104.dt031g.bathingsites

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.startActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import se.miun.alel2104.dt031g.bathingsites.bathingSiteEntity.BathingSite

class BathingSitesView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
    defStyleRes: Int = 0
) : ConstraintLayout(context, attrs, defStyle, defStyleRes) {

    private var view: View = View.inflate(context, R.layout.bathing_sites_view, this)
    private var numberOfBathingSites = 0
    private val job = SupervisorJob()
    private val applicationScope = CoroutineScope(Dispatchers.IO + job)

    init {
        initialize(attrs)
        initBathingButtonListener()
    }

    private fun initialize(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs,
            R.styleable.BathingSitesView, 0, 0)

        typedArray.recycle()

        setupTitleView()
    }

    fun setupTitleView() {
        applicationScope.launch {
            val dataBase = context?.let { AppDataBase.getDatabase(it) }
            val bathingSiteDao = dataBase?.BathingSiteDao()

            if (bathingSiteDao != null) {
                numberOfBathingSites = bathingSiteDao.getNumberOfBathingSites()
                val bathingSiteTitle = resources.getString(R.string.default_bathing_sites_title, numberOfBathingSites)
                view.findViewById<TextView>(R.id.bathing_sites_title).text = bathingSiteTitle
            }
        }
    }

    private fun initBathingButtonListener() {
        view.setOnClickListener {
            getBathingSites()
        }
    }

    private fun getBathingSites() {
        applicationScope.launch {
            val dataBase = context?.let { AppDataBase.getDatabase(it) }
            val bathingSiteDao = dataBase?.BathingSiteDao()

            if (bathingSiteDao != null) {
                val bathingSiteArrayList = bathingSiteDao.getAllBathingSites()

                bathingSiteArray = bathingSiteArrayList
            }
        }
        val intent = Intent(context, ViewAllBathingSitesActivity::class.java)
        startActivity(context, intent, null)
        releaseDatabase()
    }

    fun releaseDatabase() {
        AppDataBase.getDatabase(context).destroy()
    }

    companion object {
        var bathingSiteArray: List<BathingSite>? = null
    }
}
