package jp.co.moneybook.ui.edit

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import jp.co.moneybook.R
import jp.co.moneybook.db.DBLoader
import jp.co.moneybook.model.Item

class ContentAdapter(private val context: Context, val onChangeData: OnChangeData) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private val array = ArrayList<Item>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_content, parent, false)
        return HolderView(view)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val item = array[position]
        val view = holder as HolderView
        view.textType.text = item.type2
        view.textMemo.text = item.content
        view.textPay.text = item.pay.toString()

        when(item.type){
            0-> {view.textPay.setTextColor(context.resources.getColor(android.R.color.holo_blue_dark, null)) }
            1-> {view.textPay.setTextColor(context.resources.getColor(android.R.color.holo_red_dark, null)) }
        }

        Log.d("aaaaaa", "1111")
        holder.itemView.setOnClickListener(object: View.OnClickListener{
            override fun onClick(p0: View?) {
                Log.d("aaaaaa", "22")
                dialog(item, position)
            }
        })
    }

    override fun getItemCount(): Int {
        return array.size
    }

    fun setList(array: ArrayList<Item>) {
        this.array.clear()
        this.array.addAll(array)
        notifyDataSetChanged()

        var revenueMax = 0
        var expenditureMax = 0
        if(array.size>0){
            val item = array.get(0)
            revenueMax = DBLoader(context).getMaxPay(item.datetime, 0)
            expenditureMax = DBLoader(context).getMaxPay(item.datetime, 1)
        }
        onChangeData.onChange(revenueMax, expenditureMax)

    }

    private class HolderView(view: View) : RecyclerView.ViewHolder(view) {
        val textType : TextView = view.findViewById(R.id.text_type)
        val textMemo : TextView = view.findViewById(R.id.text_memo)
        val textPay : TextView = view.findViewById(R.id.text_pay)
    }

    private fun editDialog(item: Item, position: Int){
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_add, null)
        val builder = AlertDialog.Builder(context)
        builder.setTitle("修正する")
        builder.setView(view)

        val rg = view.findViewById<RadioGroup>(R.id.radio_group)
        var type = item.type
        when (type){
            0-> { rg.check(R.id.radio_revenue)}
            1-> { rg.check(R.id.radio_expenditure)}
        }

        rg.setOnCheckedChangeListener { p0, p1 ->
            when(p1){
                R.id.radio_revenue->{type = 0}
                R.id.radio_expenditure->{type = 1}
            }
        }
        val editContent = view.findViewById<EditText>(R.id.edit_content)
        val editType2 = view.findViewById<EditText>(R.id.edit_type2)
        val editPay = view.findViewById<EditText>(R.id.edit_pay)

        editContent.setText(item.content)
        editType2.setText(item.type2)
        var pay = item.pay
        if(pay <0){
            pay *= -1
        }
        editPay.setText(pay.toString())

        val dialog = builder.create()
        dialog.run {
            setButton(AlertDialog.BUTTON_POSITIVE, "save") { d, p1 ->
                val content = editContent.text.toString()
                val type2 = editType2.text.toString()

                if(editPay.text.trim() == ""){
                    pay = 0
                    editPay.setText(pay.toString())
                } else {
                    pay = editPay.text.toString().toInt()
                }

                DBLoader(context).update(item.id, type, type2, content, pay)
                val newItem = DBLoader(context).getItem(item.id)
                if(newItem != null){
                    array.set(position, newItem)

                    var revenueMax = 0
                    var expenditureMax = 0
                    if(array.size>0){
                        revenueMax = DBLoader(context).getMaxPay(item.datetime, 0)
                        expenditureMax = DBLoader(context).getMaxPay(item.datetime, 1)
                    }
                    onChangeData.onChange(revenueMax, expenditureMax)

                    notifyDataSetChanged()
                }
                d!!.dismiss()
            }
            setButton(AlertDialog.BUTTON_NEGATIVE, "cancel") { d, p1 -> d!!.dismiss()}
            show()
            getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
            getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)
        }
    }

    private fun dialog(item: Item, position: Int) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(item.content)
        val list : Array<String> = arrayOf("수정", "삭제")
        Log.d("aaaaaa", "33333")
        builder.setItems(list, object : DialogInterface.OnClickListener{
            override fun onClick(p0: DialogInterface?, p1: Int) {
                when(p1){
                    0->{
                        editDialog(item, position)
                    }
                    1->{
                        DBLoader(context).delete(item.id)
                        array.remove(item)
                        notifyDataSetChanged()

                        var revenueMax = 0
                        var expenditureMax = 0
                        if(array.size>0){
                            revenueMax = DBLoader(context).getMaxPay(item.datetime, 0)
                            expenditureMax = DBLoader(context).getMaxPay(item.datetime, 1)
                        }
                        onChangeData.onChange(revenueMax, expenditureMax)
                    }
                }
                p0!!.dismiss()
            }
        })
        val dialog = builder.create()
        dialog.run {
            setButton(AlertDialog.BUTTON_POSITIVE, "취소", object : DialogInterface.OnClickListener{
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    p0!!.dismiss()
                }
            })
            show()
            getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
        }
    }

    interface OnChangeData {
        fun onChange(revenue: Int, expenditure: Int) {

        }
    }

}