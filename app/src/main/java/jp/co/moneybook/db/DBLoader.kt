package jp.co.moneybook.db

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.widget.Toast
import jp.co.moneybook.model.Item

class DBLoader(val context: Context) {

    private var db : SQLHelper

    init {
        db = SQLHelper(context)
    }

    fun add(type: Int, type2: String, content: String, pay: Int, datetime: Long) {
        val contentValue = ContentValues()
        contentValue.put(Constant.COL_TYPE, type)
        contentValue.put(Constant.COL_TYPE2, type2)
        contentValue.put(Constant.COL_CONTENT, content)
        contentValue.put(Constant.COL_DATE, datetime)

        var money = pay
        if(type == 1){
            money *= -1
        }
        contentValue.put(Constant.COL_PAY, money)

        db.writableDatabase.insert(Constant.TABLE_NAME, null, contentValue)
        Toast.makeText(context, "追加しました。", Toast.LENGTH_SHORT).show()
        db.close()
    }

    fun delete(id: Int) {
        db.writableDatabase.delete(Constant.TABLE_NAME, Constant.COL_ID+"=?", arrayOf(id.toString()))
        Toast.makeText(context, "削除しました。", Toast.LENGTH_SHORT).show()
        db.close()
    }

    fun update(id: Int, type: Int, type2: String, content: String, pay: Int) {
        val contentValue = ContentValues()
        contentValue.put(Constant.COL_TYPE, type)
        contentValue.put(Constant.COL_TYPE2, type2)
        contentValue.put(Constant.COL_CONTENT, content)

        var money = pay
        if(type == 1){
            money *= -1
        }
        contentValue.put(Constant.COL_PAY, money)

        db.writableDatabase.update(Constant.TABLE_NAME, contentValue, Constant.COL_ID + "=?", arrayOf((id.toString())))
        db.close()
        Toast.makeText(context, "修正しました。", Toast.LENGTH_SHORT).show()
    }

    @SuppressLint("Range")
    fun getItem(id:Int): Item? {
        var item : Item?= null
        val sql = "select * from " +Constant.TABLE_NAME + " where " + Constant.COL_ID + " = " + id
        val cursor = db.readableDatabase.rawQuery(sql, null)
        if(cursor.moveToFirst()){
            val id = cursor.getInt(cursor.getColumnIndex(Constant.COL_ID))
            val type = cursor.getInt(cursor.getColumnIndex(Constant.COL_TYPE))
            val type2 = cursor.getString(cursor.getColumnIndex(Constant.COL_TYPE2))
            val content = cursor.getString(cursor.getColumnIndex(Constant.COL_CONTENT))
            val pay = cursor.getInt(cursor.getColumnIndex(Constant.COL_PAY))
            val datetime = cursor.getLong(cursor.getColumnIndex(Constant.COL_DATE))
            item = Item(id, type, type2, content, pay, datetime)
        }
        db.close()
        return item
    }

    @SuppressLint("Range")
    fun getList(datetime : Long): ArrayList<Item> {
        val array = ArrayList<Item>()
        val sql = "select * from " + Constant.TABLE_NAME + " where " + Constant.COL_DATE + "=" + datetime

        val cursor = db.readableDatabase.rawQuery(sql, null)
        while (cursor.moveToNext()){
            val id = cursor.getInt(cursor.getColumnIndex(Constant.COL_ID))
            val type = cursor.getInt(cursor.getColumnIndex(Constant.COL_TYPE))
            val type2 = cursor.getString(cursor.getColumnIndex(Constant.COL_TYPE2))
            val content = cursor.getString(cursor.getColumnIndex(Constant.COL_CONTENT))
            val pay = cursor.getInt(cursor.getColumnIndex(Constant.COL_PAY))
            val datetime = cursor.getLong(cursor.getColumnIndex(Constant.COL_DATE))

            val item = Item(id, type, type2, content, pay, datetime)
            array.add(item)
        }
        db.close()
        return array
    }

    fun getMaxPay(sDate: Long, eDate: Long, type: Int) : Int {
        // type -1 = total max
        var pay = 0
        val sql = "select sum(" + Constant.COL_PAY + " ) from " + Constant.TABLE_NAME + " where " +
                Constant.COL_DATE + " <= " + sDate +
                " and " + Constant.COL_DATE + ">=" + eDate + " and " + Constant.COL_TYPE + " = " + type
        if(type == -1) {
            val sql = "select sum(" + Constant.COL_PAY + " ) from " + Constant.TABLE_NAME + " where " +
                    Constant.COL_DATE + " <= " + sDate +
                    " and " + Constant.COL_DATE + ">=" + eDate
        }
        val cursor = db.readableDatabase.rawQuery(sql, null)
        if (cursor.moveToFirst()) {
            pay = cursor.getInt(0)
        }
        db.close()
        return pay
    }

    @SuppressLint("Range")
    fun historyList(sDate: Long, eDate: Long) : ArrayList<Any>{
        val array = ArrayList<Any>()
        val sql = "select * from " + Constant.TABLE_NAME + " where " + Constant.COL_DATE + " <= " + sDate +
         " and " + Constant.COL_DATE + ">=" + eDate + " order by " + Constant.COL_DATE + " desc"
        val cursor = db.readableDatabase.rawQuery(sql, null)

        var itemDate : Long = 0
        while (cursor.moveToNext()){
            val id = cursor.getInt(cursor.getColumnIndex(Constant.COL_ID))
            val type = cursor.getInt(cursor.getColumnIndex(Constant.COL_TYPE))
            val type2 = cursor.getString(cursor.getColumnIndex(Constant.COL_TYPE2))
            val content = cursor.getString(cursor.getColumnIndex(Constant.COL_CONTENT))
            val pay = cursor.getInt(cursor.getColumnIndex(Constant.COL_PAY))
            val datetime = cursor.getLong(cursor.getColumnIndex(Constant.COL_DATE))

            if(itemDate != datetime){
                itemDate = datetime
                if(itemDate == 0.toLong()) return array
                array.add(itemDate)
                val item = Item(id, type, type2, content, pay, datetime)
                array.add(item)
            } else {
                val item = Item(id, type, type2, content, pay, datetime)
                array.add(item)
            }


        }
        cursor.close()
        db.close()
        return array
    }
}