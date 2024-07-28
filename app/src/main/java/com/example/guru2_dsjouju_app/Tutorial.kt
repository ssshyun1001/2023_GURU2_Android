package com.example.guru2_dsjouju_app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2

class Tutorial : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var dotsLayout: LinearLayout
    private lateinit var prevButton: Button
    private lateinit var nextButton: Button
    private lateinit var backButton: Button
    private val images = intArrayOf(R.drawable.t_example_01, R.drawable.t_example_02, R.drawable.t_example_03, R.drawable.t_example_04, R.drawable.t_example_05)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tutorial)

        viewPager = findViewById(R.id.viewPager)
        dotsLayout = findViewById(R.id.dotsLayout)
        prevButton = findViewById(R.id.prev_button)
        nextButton = findViewById(R.id.next_button)
        backButton = findViewById(R.id.tuto_back_btn)

        var adapter = TutorialAdapter(this, images)
        viewPager.adapter = adapter

        setupDots()
        selectDot(0)

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                selectDot(position)
            }
        })

        prevButton.setOnClickListener {
            val currentItem = viewPager.currentItem
            if (currentItem > 0) {
                viewPager.currentItem = currentItem - 1
            }
        }

        nextButton.setOnClickListener {
            val currentItem = viewPager.currentItem
            if (currentItem < images.size - 1) {
                viewPager.currentItem = currentItem + 1
            }
        }

        backButton.setOnClickListener { finish() }
    }

    private fun setupDots() {
        dotsLayout.removeAllViews()  // Clear existing dots
        for (i in images.indices) {
            val dot = View(this).apply {
                setBackgroundResource(R.drawable.dot_selector)
                val params = LinearLayout.LayoutParams(
                    context.dpToPx(12),  // Convert dp to pixels using Context extension function
                    context.dpToPx(12)
                ).apply {
                    setMargins(context.dpToPx(8), 0, context.dpToPx(8), 0)
                }
                layoutParams = params
            }
            dotsLayout.addView(dot)
        }
    }

    private fun selectDot(index: Int) {
        for (i in 0 until dotsLayout.childCount) {
            val dot = dotsLayout.getChildAt(i)
            dot.isSelected = i == index
        }
    }

    inner class TutorialAdapter(private val context: Context, private val images: IntArray) :
        RecyclerView.Adapter<TutorialAdapter.TutorialViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TutorialViewHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.tutorial_item, parent, false)
            return TutorialViewHolder(view)
        }

        override fun onBindViewHolder(holder: TutorialViewHolder, position: Int) {
            holder.imageView.setImageResource(images[position])
        }

        override fun getItemCount(): Int {
            return images.size
        }

        inner class TutorialViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val imageView: ImageView = itemView.findViewById(R.id.tutorialImage)
        }
    }
}

fun Context.dpToPx(dp: Int): Int {
    return (dp * resources.displayMetrics.density).toInt()
}
