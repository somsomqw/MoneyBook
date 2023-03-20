package jp.co.moneybook.ui.history

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import jp.co.moneybook.R
import jp.co.moneybook.model.Item
import jp.co.moneybook.ui.edit.ContentAdapter
import java.text.SimpleDateFormat

class HistoryAdapter(val context : Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private val array = ArrayList<Any>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view : View
        if (viewType == 0) {
            view = LayoutInflater.from(parent.context).inflate(R.layout.item_history_header , parent, false)
            return HeaderView(view)
        } else {
            view = LayoutInflater.from(parent.context).inflate(R.layout.item_content , parent, false)
            return HolderView(view)
        }

    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == 0) {
            val day = array[position] as Long
            val date = SimpleDateFormat("yyyy.MM.dd(EEE)").format(day)
            val view = holder as HeaderView
            view.textDay.text = date
        } else {
            val item = array[position] as Item
            val view = holder as HolderView
            view.textType.text = item.type2
            view.textMemo.text = item.content
            view.textPay.text = item.pay.toString()

            when(item.type){
                0-> {view.textPay.setTextColor(context.resources.getColor(android.R.color.holo_blue_dark, null)) }
                1-> {view.textPay.setTextColor(context.resources.getColor(android.R.color.holo_red_dark, null)) }
            }
        }
    }

    override fun getItemCount(): Int {
        return array.size
    }

    override fun getItemViewType(position: Int): Int {
        if (array[position] is Long) {
            return 0
        }
        if (array[position] is Item){
            return 1
        }
        return super.getItemViewType(position)
    }

    fun setList(array: ArrayList<Any>) {
        this.array.clear()
        this.array.addAll(array)
        notifyDataSetChanged()
    }

    private class HeaderView(view : View): RecyclerView.ViewHolder(view) {
        val textDay : TextView = view.findViewById(R.id.text_day)
    }
    private class HolderView(view: View) : RecyclerView.ViewHolder(view) {
        val textType : TextView = view.findViewById(R.id.text_type)
        val textMemo : TextView = view.findViewById(R.id.text_memo)
        val textPay : TextView = view.findViewById(R.id.text_pay)
    }

}