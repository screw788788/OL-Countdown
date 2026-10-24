package com.example.olcountdown

import android.app.Activity
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import java.util.Calendar

class MainActivity : Activity() {

    private lateinit var daysTv: TextView
    private lateinit var daysLbl: TextView
    private lateinit var hmsTv: TextView
    private lateinit var pctTv: TextView
    private lateinit var msgTv: TextView
    private lateinit var bar: ProgressBar
    private val handler = Handler(Looper.getMainLooper())

    // O/L starts 8 December 2026
    private val target = Calendar.getInstance().apply {
        set(2026, Calendar.DECEMBER, 8, 0, 0, 0); set(Calendar.MILLISECOND, 0)
    }.timeInMillis
    private val start = Calendar.getInstance().apply {
        set(2026, Calendar.JANUARY, 1, 0, 0, 0); set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    private val messages = listOf(
        "Small steps every day beat last-minute panic.",
        "Past papers are your best friend. Do one today.",
        "Sleep is part of studying. Don't skip it.",
        "You don't have to be perfect, just keep going.",
        "Revise the hard topics first while your mind is fresh.",
        "Drink water, take breaks, then get back to it.",
        "Every page you revise now is one less worry later.",
        "Teach a topic to someone else. It sticks better.",
        "Phone away for 45 minutes. Then you can check it.",
        "You've already come this far. Finish strong."
    )

    private val tick = object : Runnable {
        override fun run() { update(); handler.postDelayed(this, 1000) }
    }

    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()

    private fun tv(size: Float, color: Int, bold: Boolean = false): TextView = TextView(this).apply {
        textSize = size
        setTextColor(color)
        gravity = Gravity.CENTER
        if (bold) typeface = Typeface.DEFAULT_BOLD
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(dp(24), dp(24), dp(24), dp(24))
            background = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                intArrayOf(Color.parseColor("#0f2027"), Color.parseColor("#203a43"), Color.parseColor("#2c5364")))
        }

        val title = tv(20f, Color.parseColor("#9fd8ff"), true).apply { text = "G.C.E. O/L EXAM" }
        val sub = tv(14f, Color.parseColor("#bbbbbb")).apply { text = "starts 8 December 2026" }

        daysTv = tv(110f, Color.WHITE, true)
        daysLbl = tv(18f, Color.parseColor("#9fd8ff"))
        hmsTv = tv(34f, Color.WHITE, true).apply { typeface = Typeface.MONOSPACE }

        bar = ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal).apply {
            max = 1000
            progressTintList = ColorStateList.valueOf(Color.parseColor("#00e5a0"))
            progressBackgroundTintList = ColorStateList.valueOf(Color.parseColor("#33ffffff"))
        }
        pctTv = tv(13f, Color.parseColor("#bbbbbb"))
        msgTv = tv(18f, Color.parseColor("#ffd966")).apply { setPadding(dp(8), dp(28), dp(8), 0) }

        root.addView(title)
        root.addView(sub)
        root.addView(daysTv)
        root.addView(daysLbl)
        root.addView(hmsTv, LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(12) })
        root.addView(bar, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(14))
            .apply { topMargin = dp(32) })
        root.addView(pctTv, LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(6) })
        root.addView(msgTv)
        setContentView(root)
    }

    override fun onResume() { super.onResume(); handler.post(tick) }
    override fun onPause() { super.onPause(); handler.removeCallbacks(tick) }

    private fun update() {
        val now = System.currentTimeMillis()
        var left = target - now
        if (left <= 0) {
            daysTv.text = "0"
            daysLbl.text = "days left"
            hmsTv.text = "00:00:00"
            bar.progress = 1000
            pctTv.text = "100% of the way there"
            msgTv.text = "It's here. All the best for your O/L! You've got this!"
            return
        }
        val s = left / 1000
        val d = s / 86400
        val h = (s % 86400) / 3600
        val m = (s % 3600) / 60
        val sec = s % 60
        daysTv.text = d.toString()
        daysLbl.text = if (d == 1L) "day left" else "days left"
        hmsTv.text = String.format("%02d:%02d:%02d", h, m, sec)

        val progress = ((now - start).toDouble() / (target - start)).coerceIn(0.0, 1.0)
        bar.progress = (progress * 1000).toInt()
        pctTv.text = String.format("%.1f%% of the year to your exam is done", progress * 100)

        val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        msgTv.text = messages[dayOfYear % messages.size]
    }
}
