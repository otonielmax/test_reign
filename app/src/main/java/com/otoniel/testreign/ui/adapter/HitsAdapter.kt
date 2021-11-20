package com.otoniel.testreign.ui.adapter

import android.content.Context
import android.text.format.DateUtils
import android.text.format.DateUtils.getRelativeTimeSpanString
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.otoniel.testreign.R
import com.otoniel.testreign.data.model.HitsModel
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.util.*

class HitsAdapter(
    private var list: List<HitsModel>,
    private val listener: HitsListener,
    private val context: Context
) :
    RecyclerView.Adapter<HitsAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(
        view: View,
    ) : RecyclerView.ViewHolder(view) {
        val title: TextView
        val author: TextView

        init {
            // Define click listener for the ViewHolder's View.
            title = view.findViewById(R.id.title)
            author = view.findViewById(R.id.author)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_news, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element

        if (!list[position].title.isNullOrEmpty()) {
            viewHolder.title.text = list[position].title
        } else {
            viewHolder.title.text = list[position].story_title
        }

        if (!list[position].author.isNullOrEmpty()) {
            // val locale = Locale.US
            val pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
            val tz = TimeZone.getTimeZone("GMT")
            val df: SimpleDateFormat = SimpleDateFormat(pattern /*, locale*/)
            df.timeZone = tz

            val c: Calendar = Calendar.getInstance()
            c.timeZone = tz

            // val now: Long = System.currentTimeMillis()

            viewHolder.author.text = list[position].author + " - " +
                    getRelativeTimeSpanString(
                        df.parse(list[position].created_at).time,
                        c.timeInMillis,
                        DateUtils.MINUTE_IN_MILLIS
                    )
        }

        viewHolder.itemView.setOnClickListener(View.OnClickListener {
            listener.selectedHitsItem(position)
        })

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = list.size

    fun updateData(list: List<HitsModel>) {
        this.list = list
        notifyDataSetChanged()
    }

    interface HitsListener {
        fun selectedHitsItem(position: Int)
    }
}