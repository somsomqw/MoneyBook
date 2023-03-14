package jp.co.moneybook.ui.edit

import android.app.AlertDialog
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.DatePicker
import android.widget.EditText
import android.widget.RadioGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import jp.co.moneybook.R
import jp.co.moneybook.databinding.FragmentEditBinding
import jp.co.moneybook.db.DBLoader
import java.text.SimpleDateFormat
import java.util.*

class EditFragment : Fragment() {
    private var _binding: FragmentEditBinding? = null
    private val binding get() = _binding!!
    private val onClickListener = View.OnClickListener { v ->
        when (v!!.id) {
            R.id.btn_left->{
                calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)-1)
                changeDate()
            }
            R.id.btn_right->{
                calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)+1)
                changeDate()
            }
            R.id.text_day->{
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    calendarDialog()
                }
            }
            R.id.btn_add-> {
                addDialog()
            }
        }
    }
    private lateinit var adapter : ContentAdapter
    private lateinit var calendar: Calendar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditBinding.inflate(inflater, container, false)


        binding.btnLeft.setOnClickListener(onClickListener)
        binding.btnRight.setOnClickListener(onClickListener)
        binding.textDay.setOnClickListener(onClickListener)
        binding.btnAdd.setOnClickListener(onClickListener)

        adapter = ContentAdapter(requireContext(), object: ContentAdapter.OnChangeData{
            override fun onChange(revenue: Int, expenditure: Int) {
                binding.textRevenue.text = revenue.toString()
                binding.textExpenditure.text = expenditure.toString()
                binding.textPay.text = (revenue+expenditure).toString()
            }
        })
        val recyclerView = binding.recyclerView
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        changeDate()


        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun changeDate() {
        val date = SimpleDateFormat("yyyy.MM.dd(EEE)").format(calendar.time)
        binding.textDay.text = date
        adapter.setList(DBLoader(requireContext()).getList(calendar.timeInMillis))

        if(adapter.itemCount == 0) {
            binding.textNoData.visibility = View.VISIBLE
        } else {
            binding.textNoData.visibility = View.GONE
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun calendarDialog() {

        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_datepicker, null)
        val builder = AlertDialog.Builder(requireContext())
        val datePickerDialog = view.findViewById<DatePicker>(R.id.date_picker)
        builder.setView(view)

        datePickerDialog.updateDate(calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH))

        val dialog = builder.create()
        dialog.run {
            setButton(AlertDialog.BUTTON_POSITIVE, "선택") { d, p1 ->
                val year = datePickerDialog.year
                val month = datePickerDialog.month
                val day = datePickerDialog.dayOfMonth
                calendar.set(year, month, day, 0, 0, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                changeDate()
                d!!.dismiss()
            }
            setButton(AlertDialog.BUTTON_NEGATIVE, "취소") { d, p1 ->
                d!!.dismiss()
            }
            show()
            getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
            getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)
        }


//        val datePicker = DatePickerDialog(requireContext(), object :DatePickerDialog.OnDateSetListener{
//            override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
//                Toast.makeText(requireContext(), p1.toString()+p2.toString()+p3.toString(), Toast.LENGTH_SHORT).show()
//                calendar.set(p1, p2, p3)
//            }
//        }, calendar.get(Calendar.YEAR),
//            calendar.get(Calendar.MONTH),
//            calendar.get(Calendar.DAY_OF_MONTH))
//
//        datePicker.setButton(DatePickerDialog.BUTTON_POSITIVE, "ok") { p0, p1 ->
//            changeDate()
//            p0.dismiss()
//        }
//        datePicker.show()
//        datePicker.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
    }

    private fun addDialog(){
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add, null)
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(view)

        val rg = view.findViewById<RadioGroup>(R.id.radio_group)
        var type = 1
        rg.setOnCheckedChangeListener { p0, p1 ->
            when(p1){
                R.id.radio_revenue->{type = 0}
                R.id.radio_expenditure->{type = 1}
            }
        }
        val editContent = view.findViewById<EditText>(R.id.edit_content)
        val editType2 = view.findViewById<EditText>(R.id.edit_type2)
        val editPay = view.findViewById<EditText>(R.id.edit_pay)
        var pay = 0
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
                DBLoader(requireContext()).add(type, type2, content, pay, calendar.timeInMillis)
                changeDate()
                d!!.dismiss()
            }
            setButton(AlertDialog.BUTTON_NEGATIVE, "cancel") { d, p1 -> d!!.dismiss()}
            show()
            getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
            getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)
        }
    }
}