package jp.co.moneybook.ui.history

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import jp.co.moneybook.R
import jp.co.moneybook.databinding.FragmentHistoryBinding
import jp.co.moneybook.db.DBLoader
import jp.co.moneybook.ui.CustomSpinner
import java.text.SimpleDateFormat
import java.util.Calendar

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var historyAdapter: HistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val array = resources.getStringArray(R.array.spinner)
        val adapter = object : ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            array
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val textView = super.getDropDownView(position, convertView, parent) as TextView
                textView.setTextColor(Color.WHITE)
                return textView
            }
        }

        binding.spinner.adapter = adapter
        binding.spinner.background.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                //Toast.makeText(requireContext(), p2.toString(), Toast.LENGTH_SHORT).show()
                load(p2) //load(binding.spinner.selectedItemPosition)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

//        binding.spinner.setOnSelected(object : CustomSpinner.OnItemSelected{
//            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
//                Log.d("22222", "22222")
//            }
//        })

        historyAdapter = HistoryAdapter(requireContext())
        binding.historyRecyclerView.setHasFixedSize(true)
        binding.historyRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.historyRecyclerView.adapter = historyAdapter
        load(binding.spinner.selectedItemPosition)

        return root
    }

    private fun load(selected: Int) {
        when (selected) {
            0 -> {
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.HOUR, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)

                changeData(calendar.timeInMillis, calendar.timeInMillis)
            }
            1 -> {
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.HOUR, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - 1)

                changeData(calendar.timeInMillis, calendar.timeInMillis)
            }
            2 -> {
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.HOUR, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)

                calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - 1)
                val sDate = calendar.timeInMillis

                calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - 6)
                val eDate = calendar.timeInMillis

                changeData(sDate, eDate)
            }
            3 -> {
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.HOUR, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)

                calendar.set(
                    Calendar.DAY_OF_MONTH,
                    calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                )
                val sDate = calendar.timeInMillis

                calendar.set(
                    Calendar.DAY_OF_MONTH,
                    calendar.getActualMinimum(Calendar.DAY_OF_MONTH)
                )
                val eDate = calendar.timeInMillis

                changeData(sDate, eDate)
            }
            4 -> {
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.HOUR, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)

                calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1)
                calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - 1)
                val sDate = calendar.timeInMillis

                calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - 29)
                val eDate = calendar.timeInMillis

                changeData(sDate, eDate)
            }
            5 -> {
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.HOUR, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)

                calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1)
                calendar.set(
                    Calendar.DAY_OF_MONTH,
                    calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                )
                val sDate = calendar.timeInMillis

                calendar.set(
                    Calendar.DAY_OF_MONTH,
                    calendar.getActualMinimum(Calendar.DAY_OF_MONTH)
                )
                val eDate = calendar.timeInMillis
                changeData(sDate, eDate)
            }
            6 -> {
                dateDialog()
            }
        }
    }

    private fun changeData(sDate:Long, eDate:Long) {
        historyAdapter.setList(
            DBLoader(requireContext()).historyList(
                sDate,
                eDate
            )
        )

        val revenue = DBLoader(requireContext()).getMaxPay(sDate, eDate, 1)
        val expenditure = DBLoader(requireContext()).getMaxPay(sDate, eDate, 0)
        binding.allRevenue.text = revenue.toString()
        binding.allExpenditure.text = expenditure.toString()
        binding.allTotal.text = (revenue+expenditure).toString()

        if(historyAdapter.itemCount == 0) {
            binding.textNoData.visibility = View.VISIBLE
        } else {
            binding.textNoData.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun dateDialog() {
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_spinner, null)
        val builder = AlertDialog.Builder(requireContext())

        val sDatePicker = view.findViewById<DatePicker>(R.id.s_picker)
        val eDatePicker = view.findViewById<DatePicker>(R.id.e_picker)

        builder.setView(view)

        val dialog = builder.create()
        dialog.run {
            setButton(AlertDialog.BUTTON_POSITIVE, "확인") { d, p1 ->
                val calendar = Calendar.getInstance()
                calendar.set(sDatePicker.year, sDatePicker.month, sDatePicker.dayOfMonth, 0, 0, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val sDate = calendar.timeInMillis

                calendar.set(eDatePicker.year, eDatePicker.month, eDatePicker.dayOfMonth, 0, 0, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val eDate = calendar.timeInMillis

                if (sDate > eDate) {
                    Toast.makeText(requireContext(), "기간이 잘못되었습니다", Toast.LENGTH_SHORT).show()
                } else {
                    changeData(sDate, eDate)

                    val selectDate = SimpleDateFormat("yyyy.MM.dd").format(sDate) + "~" + SimpleDateFormat("yyyy.MM.dd").format(eDate)
                    (binding.spinner.selectedView as (TextView)).text = selectDate
                    d!!.dismiss()
                }
            }
            setButton(AlertDialog.BUTTON_NEGATIVE, "취소") { d, p1 -> d!!.dismiss() }
            show()
            getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
            getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)
        }
    }
}

