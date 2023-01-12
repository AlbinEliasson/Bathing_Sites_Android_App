package se.miun.alel2104.dt031g.bathingsites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import se.miun.alel2104.dt031g.bathingsites.bathingSiteEntity.BathingSite

class ViewAllBathingSitesActivity : AppCompatActivity() {
    private var recyclerView: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_all_bathing_sites)

        val bathingSiteList: ArrayList<BathingSite>? = BathingSitesView.bathingSiteArray?.let {
            ArrayList(
                it
            )
        }

        recyclerView = findViewById(R.id.bathingSitesRecycler)

        if (bathingSiteList != null) {
            setAdapter(bathingSiteList)
        }
    }

    private fun setAdapter(bathingSiteList: ArrayList<BathingSite>) {
        val adapter = BathingSiteRecyclerAdapter(bathingSiteList)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(applicationContext)

        recyclerView?.layoutManager = layoutManager
        recyclerView?.itemAnimator = DefaultItemAnimator()
        recyclerView?.adapter = adapter
    }

    //https://stackoverflow.com/questions/43012903/trying-to-create-a-simple-recyclerview-in-kotlin-but-the-adapter-is-not-applyin
    internal class BathingSiteRecyclerAdapter(
        private val bathingSiteList: ArrayList<BathingSite>
    ) : RecyclerView.Adapter<BathingSiteRecyclerAdapter.ViewHolder>() {


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
                BathingSiteRecyclerAdapter.ViewHolder {

            val itemView = LayoutInflater.from(parent.context).inflate(
                R.layout.bathing_site_items, parent, false)
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: BathingSiteRecyclerAdapter.ViewHolder, position: Int) {
            val name = holder.itemView.context.getString(R.string.edit_text_bathing_site_name)
            val description = holder.itemView.context.getString(
                R.string.edit_text_bathing_site_address)
            val address = holder.itemView.context.getString(R.string.edit_text_bathing_site_address)
            val latitude = holder.itemView.context.getString(
                R.string.edit_text_bathing_site_latitude)
            val longitude = holder.itemView.context.getString(
                R.string.edit_text_bathing_site_longitude)
            val grade = holder.itemView.context.getString(R.string.edit_text_bathing_site_grade)
            val waterTmp = holder.itemView.context.getString(
                R.string.edit_text_bathing_site_water_temperature)
            val waterTmpDate = holder.itemView.context.getString(
                R.string.edit_text_bathing_site_water_temperature_date)
            val bathingName = name + " " + bathingSiteList[position].name

            holder.bathingSiteName!!.text = bathingName

            holder.itemView.setOnClickListener {
                val newLine = holder.itemView.context.getString(R.string.new_line)
                val specificBathingSite = bathingSiteList[position]

                val fullBathingSiteInfo = name + " " + specificBathingSite.name + newLine +
                        description + " " + specificBathingSite.description + newLine + address +
                        " " + specificBathingSite.address + newLine + latitude + " " +
                        specificBathingSite.latitude + newLine + longitude + " " +
                        specificBathingSite.longitude + newLine + grade + " " +
                        specificBathingSite.grade + newLine + waterTmp + " " +
                        specificBathingSite.waterTmp + newLine + waterTmpDate + " " +
                        specificBathingSite.waterTmpDate

                holder.bathingSiteName!!.text = fullBathingSiteInfo
            }
        }

        override fun getItemCount(): Int {
            return bathingSiteList.size
        }

        internal inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var bathingSiteName: TextView? = null

            init {
                bathingSiteName = itemView.findViewById(R.id.nameOfBathingSite)
            }
        }
    }
}
