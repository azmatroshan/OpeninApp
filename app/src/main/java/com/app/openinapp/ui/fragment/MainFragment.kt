package com.app.openinapp.ui.fragment

import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.openinapp.R
import com.app.openinapp.data.model.Link
import com.app.openinapp.databinding.FragmentMainBinding
import com.app.openinapp.ui.adapter.LinkAdapter
import com.app.openinapp.ui.viewmodel.LinkViewModel
import com.app.openinapp.utils.DayTime
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Suppress("DEPRECATION")
class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding
    private lateinit var graph: LineChart
    private lateinit var mViewModel: LinkViewModel
    private lateinit var linkAdapter: LinkAdapter
    private lateinit var supportWANumber: String
    private var topLinks: List<Link> = emptyList()
    private var recentLinks: List<Link> = emptyList()
    private var isSelectedTopLinks: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        graph = binding.graph

        graph.description.isEnabled = false

        mViewModel = ViewModelProvider(this)[LinkViewModel::class.java]

        val currentTime = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        binding.wishTv.text = when(currentTime){
            in 0..11 -> DayTime.MORNING.greeting
            in 12..16 -> DayTime.AFTERNOON.greeting
            else -> DayTime.EVENING.greeting
        }

        binding.recentLinks.setOnClickListener {
            if(isSelectedTopLinks){
                isSelectedTopLinks = false

                binding.recentLinks.setBackgroundResource(R.drawable.custom_tab_indicator)
                binding.recentLinks.setTextColor(Color.WHITE)
                binding.topLinks.background = null
                binding.topLinks.setTextColor(resources.getColor(R.color.wish))

                linkAdapter.setData(recentLinks)
            }
        }

        binding.topLinks.setOnClickListener {
            if(!isSelectedTopLinks){
                isSelectedTopLinks = true

                binding.topLinks.setBackgroundResource(R.drawable.custom_tab_indicator)
                binding.topLinks.setTextColor(Color.WHITE)
                binding.recentLinks.background = null
                binding.recentLinks.setTextColor(resources.getColor(R.color.wish))

                linkAdapter.setData(topLinks)
            }
        }

        binding.viewAnalytics.setOnClickListener {
            Toast.makeText(requireContext(), "This feature isn't implemented yet!", Toast.LENGTH_SHORT).show()
        }

        binding.viewAllLinks.setOnClickListener {
            Toast.makeText(requireContext(), "This feature isn't implemented yet!", Toast.LENGTH_SHORT).show()
        }

        binding.faq.setOnClickListener {
            Toast.makeText(requireContext(), "This feature isn't implemented yet!", Toast.LENGTH_SHORT).show()
        }

        binding.talkWithUs.setOnClickListener {
            val uri = Uri.parse("https://api.whatsapp.com/send?phone=$supportWANumber")
            val intent = Intent(Intent.ACTION_VIEW, uri)

            if(isWhatsappInstalled()){
                startActivity(intent)
            } else{
                Toast.makeText(requireContext(), "WhatsApp is not installed on your device!", Toast.LENGTH_SHORT).show()
            }
        }

        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.progress_dialog)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        mViewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            if(isLoading) dialog.show()
            else dialog.dismiss()
        }

        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        linkAdapter = LinkAdapter()
        recyclerView.adapter = linkAdapter

        observeData()
        fetchData()

    }

    private fun observeData() {
        mViewModel.data.observe(viewLifecycleOwner) { response ->
            binding.todayClicks.text = response.today_clicks.toString()
            if(response.top_location!=""){
                binding.topLocation.text = response.top_location
            }
            if(response.top_source!=""){
                binding.topSource.text = response.top_source
            }
            topLinks = response.data.top_links
            recentLinks = response.data.recent_links
            supportWANumber = response.support_whatsapp_number

            linkAdapter.setData(topLinks)

            val chartData = response.data.overall_url_chart
            displayLineChart(chartData)
        }
    }

    private fun fetchData() {
        mViewModel.fetchData()
    }

    private fun displayLineChart(chartData: Map<String, Int>) {
        val dataPoints = convertChartData(chartData)

        val lineDataSet = LineDataSet(dataPoints.toList(), "")
        lineDataSet.setDrawValues(false)
        lineDataSet.setDrawCircleHole(false)
        lineDataSet.setDrawCircles(false)
        lineDataSet.setDrawFilled(true)
        val lineData = LineData(lineDataSet)

        graph.data = lineData
        graph.invalidate()

        graph.description.isEnabled = false
        graph.legend.isEnabled = false

        val xAxis = graph.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawAxisLine(true)
        xAxis.setDrawGridLines(true)
        xAxis.enableGridDashedLine(5f, 4f, 0f)
        graph.axisRight.isEnabled = false

        val leftAxis = graph.axisLeft
        leftAxis.setDrawAxisLine(true)
        leftAxis.setDrawGridLines(true)
        leftAxis.enableGridDashedLine(5f, 4f, 0f)

        graph.setTouchEnabled(false)
        graph.isDragEnabled = false
        graph.setScaleEnabled(false)
        graph.isDoubleTapToZoomEnabled = false
    }


    private fun convertChartData(chartData: Map<String, Int>): Array<Entry> {
        val entries = mutableListOf<Entry>()
        val labels = mutableListOf<String>()

        chartData.forEach { (dateString, value) ->
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd MMM", Locale.getDefault())

            val date = inputFormat.parse(dateString)
            val formattedDate = outputFormat.format(date!!)

            val entry = Entry(entries.size.toFloat(), value.toFloat())
            entries.add(entry)
            labels.add(formattedDate)
        }

        val xAxisFormatter = IndexAxisValueFormatter(labels)

        val xAxis = graph.xAxis
        xAxis.valueFormatter = xAxisFormatter

        return entries.toTypedArray()
    }

    private fun isWhatsappInstalled(packageName: String = "com.whatsapp"): Boolean{
        val packageManager = requireContext().packageManager
        return try {
            packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }
}