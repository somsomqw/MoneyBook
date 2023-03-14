package jp.co.moneybook

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import jp.co.moneybook.databinding.ActivityWriteBinding
import java.text.SimpleDateFormat
import java.util.*

class WriteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWriteBinding
    private val date = Date()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        date.time = intent.extras!!.getLong("datetime")
        val date = SimpleDateFormat("yyyy.MM.dd(EEE)").format(date)

        val toolbar = binding.toolbar
        toolbar.title = date
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}