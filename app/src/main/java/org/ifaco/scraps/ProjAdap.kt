package org.ifaco.scraps

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import org.ifaco.scraps.Fun.Companion.vis
import org.ifaco.scraps.Menu.Project

class ProjAdap(val c: Context, val list: List<Project>) :
    RecyclerView.Adapter<ProjAdap.MyViewHolder>() {

    class MyViewHolder(val v: ConstraintLayout) : RecyclerView.ViewHolder(v)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_project, parent, false) as ConstraintLayout
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(h: MyViewHolder, i: Int) {
        val main = h.v[mainPos] as LinearLayout
        val name = main[namePos] as TextView
        val date = main[datePos] as TextView
        val borderBottom = h.v[borderBottomPos]

        // Texts
        name.text = "${i + 1}. ${list[i].name}"
        date.text = list[h.layoutPosition].startDate

        // Border Bottom
        vis(borderBottom, i != list.size - 1)

        // Clicks
        h.v.setOnClickListener {
            Menu.navHandler.obtainMessage(0, list[h.layoutPosition]).sendToTarget()
        }
    }

    override fun getItemCount() = list.size


    companion object {
        const val mainPos = 0
        const val namePos = 0
        const val datePos = 1
        const val borderBottomPos = 1
    }
}