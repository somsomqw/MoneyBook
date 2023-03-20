package jp.co.moneybook.ui.history

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
        val adapter = object: ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_dropdown_item, array){
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val textView = super.getDropDownView(position, convertView, parent) as TextView
                textView.setTextColor(Color.WHITE)
                return textView
            }
        }
        binding.spinner.adapter = adapter
        binding.spinner.background.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                Toast.makeText(requireContext(), p2.toString(), Toast.LENGTH_SHORT).show()
                load(p2) //load(binding.spinner.selectedItemPosition)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
        historyAdapter = HistoryAdapter(requireContext())
        binding.historyRecyclerView.setHasFixedSize(true)
        binding.historyRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.historyRecyclerView.adapter = historyAdapter


        return root
    }

    private fun load(selected : Int) {
        when (selected) {
            0 -> {
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.HOUR, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)

                historyAdapter.setList(
                    DBLoader(requireContext()).historyList(
                        calendar.timeInMillis,
                        calendar.timeInMillis
                    )
                )
            }
            1 -> {
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.HOUR, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) -1)

                historyAdapter.setList(
                    DBLoader(requireContext()).historyList(
                        calendar.timeInMillis,
                        calendar.timeInMillis
                    )
                )
            }
            2 -> {
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.HOUR, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)

                calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) -1)
                val sDate = calendar.timeInMillis

                calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) -6)
                val eDate = calendar.timeInMillis

                historyAdapter.setList(
                    DBLoader(requireContext()).historyList(
                        sDate,
                        eDate
                    )
                )
            }
            3 -> {
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.HOUR, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)

                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
                val sDate = calendar.timeInMillis

                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH))
                val eDate = calendar.timeInMillis

                historyAdapter.setList(
                    DBLoader(requireContext()).historyList(
                        sDate,
                        eDate
                    )
                )
            }
            4 -> {
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.HOUR, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)

                calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) -1)
                calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) -1)
                val sDate = calendar.timeInMillis

                calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) -29)
                val eDate = calendar.timeInMillis

                historyAdapter.setList(
                    DBLoader(requireContext()).historyList(
                        sDate,
                        eDate
                    )
                )
            }
            5 -> {
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.HOUR, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)

                calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) -1)
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
                val sDate = calendar.timeInMillis

                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH))
                val eDate = calendar.timeInMillis

                historyAdapter.setList(
                    DBLoader(requireContext()).historyList(
                        sDate,
                        eDate
                    )
                )
            }
            6 -> {

            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}