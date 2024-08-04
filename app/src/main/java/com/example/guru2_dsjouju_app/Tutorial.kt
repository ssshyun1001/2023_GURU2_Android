package com.example.guru2_dsjouju_app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Context
import android.util.Log
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
    private val images = intArrayOf(R.drawable.tutorial_1, R.drawable.tutorial_2, R.drawable.tutorial_3, R.drawable.tutorial_4, R.drawable.tutorial_5)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tutorial)

        viewPager = findViewById(R.id.viewPager)
        dotsLayout = findViewById(R.id.dotsLayout)
        prevButton = findViewById(R.id.prev_button)
        nextButton = findViewById(R.id.next_button)
        backButton = findViewById(R.id.tuto_back_btn)

        // ViewPager에 어댑터 설정
        val adapter = TutorialAdapter(this, images)
        viewPager.adapter = adapter
        setupDots()
        selectDot(0)

        // ViewPager 페이지 변경 시 호출될 콜백 등록
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                selectDot(position)
            }
        })

        // 이전 버튼 클릭 시 처리
        prevButton.setOnClickListener {
            val currentItem = viewPager.currentItem
            if (currentItem > 0) {
                viewPager.currentItem = currentItem - 1
            }
        }

        // 다음 버튼 클릭 시 처리
        nextButton.setOnClickListener {
            val currentItem = viewPager.currentItem
            if (currentItem < images.size - 1) {
                viewPager.currentItem = currentItem + 1
            }
        }
        // 뒤로 가기 버튼 클릭 시 처리
        backButton.setOnClickListener { finish() }
    }

    // 도트 초기화
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

    // 선택된 도트 표시
    private fun selectDot(index: Int) {
        for (i in 0 until dotsLayout.childCount) {
            val dot = dotsLayout.getChildAt(i)
            dot.isSelected = i == index
        }
    }

    // ViewPager 어댑터
    inner class TutorialAdapter(private val context: Context, private val images: IntArray) :
        RecyclerView.Adapter<TutorialAdapter.TutorialViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TutorialViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.tutorial_item, parent, false)
            return TutorialViewHolder(view)
        }

        override fun onBindViewHolder(holder: TutorialViewHolder, position: Int) {
            Log.d("TutorialAdapter", "Binding image at position $position")
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

// dp를 픽셀로 변환하는 확장 함수
fun Context.dpToPx(dp: Int): Int {
    return (dp * resources.displayMetrics.density).toInt()
}
