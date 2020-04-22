package com.jeremy.lym.rectindicatorview

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.widget.LinearLayout
import android.widget.TextView
import androidx.viewpager.widget.ViewPager

/**
 * Created by Jeremy on 2020/4/21
 * github: https://github.com/JeremyLeeL
 * 用法：this.data = List<Sting>
 * 可配置变量见 R.styleable.RectIndicatorView
 * ps:未做默认宽高的计算，需要中xml中给定 ‘MeasureSpec.EXACTLY’ 的宽高
 */
class RectIndicatorView @JvmOverloads constructor(
        context: Context,
        attributeSet: AttributeSet? = null,
        defStyleAttr: Int = 0) : LinearLayout(context, attributeSet, defStyleAttr) {
    companion object{
        const val TAG = "RectIndicatorView"
    }

    /**文字未选中颜色*/
    private var textColorNormal: Int

    /**文字选中后颜色*/
    private var textColorSelected: Int

    /**文字大小*/
    private var textSize: Float

    /**边框颜色*/
    private var rectNormalColor: Int

    /**被选中文字边框颜色*/
    private var rectSelectedColor: Int

    private var paint: Paint

    /**边框粗细*/
    private var strokeWidth: Float

    /**是否显示viewPager切换动画*/
    private var showViewPagerAnimation: Boolean

    /**是否手动滑动viewPager（包含显示viewPager切换动画的场景）*/
    private var isScrollManual = false
    private val onePieceWidth by lazy { width.toFloat() / data!!.size.toFloat() }

    /**控制动画进度的变量*/
    private var fraction = 0f
        set(value) {
            field = value
            invalidate()
        }

    /**当前动画是否在播放中（未开启viewPager切换动画时使用）*/
    private var animationPlaying = false

    /**动画播放器（未开启viewPager切换动画时使用）*/
    private val animator by lazy {
        val animator = ObjectAnimator.ofFloat(this, "fraction", 1f)
        animator.addListener(object : Animator.AnimatorListener{
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                handler.postDelayed({//动画播完了之后还会刷新一帧（调用onDraw一次）
                    lastPosition = currentPosition
                    animationPlaying = false
                }, 100)
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
                animationPlaying = true
            }
        })
        animator
    }

    /**上一次选中的position*/
    private var lastPosition = 0

    /**当前选中的position*/
    private var currentPosition = 0

    /**当前点击position和当前选中position之间的差值*/
    private var number = 1
    private var viewPager: ViewPager? = null

    /**选中框滑动的方向（未开启viewPager切换动画时使用）*/
    private var direction = Direction.RIGHT

    private var isFirstLoad = true

    /**数据源*/
    var data: List<String>? = null
        set(data) {
            field = data
            if (!data.isNullOrEmpty()) {
                removeAllViews()
                data.forEachIndexed { index, string ->
                    val textView = TextView(context)
                    val layoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f)
                    layoutParams.gravity = Gravity.CENTER_VERTICAL
                    textView.gravity = Gravity.CENTER
                    textView.text = string
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
                    if (index == currentPosition) {
                        textView.setTextColor(textColorSelected)
                    } else {
                        textView.setTextColor(textColorNormal)
                    }
                    textView.layoutParams = layoutParams
                    addView(textView)
                }
            }
        }

    init {
        val typeArray = context.obtainStyledAttributes(attributeSet, R.styleable.RectIndicatorView, defStyleAttr, 0)
        textColorNormal = typeArray.getColor(R.styleable.RectIndicatorView_rectTextColorNormal, Color.parseColor("#6E8CB8"))
        textColorSelected = typeArray.getColor(R.styleable.RectIndicatorView_rectTextColorSelected, Color.parseColor("#FFCC08"))
        textSize = typeArray.getDimension(R.styleable.RectIndicatorView_rectTextSize, sp2Px(16f))
        rectNormalColor = typeArray.getColor(R.styleable.RectIndicatorView_rectNormalColor, Color.parseColor("#6E8CB8"))
        rectSelectedColor = typeArray.getColor(R.styleable.RectIndicatorView_rectSelectedColor, Color.parseColor("#FFCC08"))
        strokeWidth = typeArray.getDimension(R.styleable.RectIndicatorView_rectStrokeWidth, dp2Px(1f))
        showViewPagerAnimation = typeArray.getBoolean(R.styleable.RectIndicatorView_showViewPagerAnimation, true)
        typeArray.recycle()

        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = strokeWidth

        orientation = HORIZONTAL
        setWillNotDraw(false)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        data?.let {
            paint.color = rectNormalColor
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
            paint.color = rectSelectedColor
            val left: Float
            if (showViewPagerAnimation || isScrollManual){
                left = onePieceWidth * currentPosition + onePieceWidth * fraction
            }else {
                if (lastPosition == currentPosition) {//视图重绘时，避免选择框移位
                    left = currentPosition * onePieceWidth
                    canvas.drawRect(left, 0f, left + onePieceWidth, height.toFloat(), paint)
                    return@let
                }
                left = if (direction == Direction.RIGHT) {
                    onePieceWidth * lastPosition + onePieceWidth * number * fraction
                } else {
                    onePieceWidth * lastPosition - onePieceWidth * number * fraction
                }
            }
            canvas.drawRect(left, 0f, left + onePieceWidth, height.toFloat(), paint)
            isFirstLoad = false
        }
    }

    fun setUpWithViewPager(viewPager: ViewPager){
        this.viewPager = viewPager
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(p0: Int) {
                Log.e(TAG, "onPageScrollStateChanged: $p0")
                when(p0){
                    1 ->{
                        isScrollManual = true
                    }
                    0 ->{
                        isScrollManual = false
                        lastPosition = currentPosition
                    }
                }
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                currentPosition = position
                fraction = positionOffset

                Log.e(TAG, "position: $position, positionOffset: $positionOffset")
            }

            override fun onPageSelected(position: Int) {
                setCheckText(position)
                if (showViewPagerAnimation || isScrollManual)
                    return
                currentPosition = position
                animator.start()
            }

        })
    }

    fun setCurrentPosition(position: Int){
        if (position != 0) {
            viewPager?.setCurrentItem(position, showViewPagerAnimation)
            animator.start()
        }
    }

    private fun setCheckText(position: Int){
        for (i in 0 until childCount) {
            val textView = getChildAt(i) as TextView
            if (i == position) {
                textView.setTextColor(rectSelectedColor)
            } else {
                textView.setTextColor(rectNormalColor)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        if (event.action == MotionEvent.ACTION_DOWN){
            if (showViewPagerAnimation){
                val position = getTouchPosition(x)
                viewPager?.setCurrentItem(position, showViewPagerAnimation)
                return true
            }
            if (animationPlaying){
                return super.onTouchEvent(event)
            }

            val position = getTouchPosition(x)
            when{
                position > currentPosition ->{
                    number = position - currentPosition
                    direction = Direction.RIGHT
                    viewPager?.setCurrentItem(position, showViewPagerAnimation)
                    animator.start()
                }
                position < currentPosition ->{
                    number = currentPosition - position
                    direction = Direction.LEFT
                    viewPager?.setCurrentItem(position, showViewPagerAnimation)
                    animator.start()
                }
            }
        }
        return super.onTouchEvent(event)
    }

    private fun getTouchPosition(touchX: Float): Int{
        if (!data.isNullOrEmpty()) {
            Log.e(TAG, "clickPosition: ${(touchX / onePieceWidth).toInt()}")
            return (touchX / onePieceWidth).toInt()
        }
        return 0
    }

    private fun dp2Px(dp: Float): Float{
        val scale = Resources.getSystem().displayMetrics.density
        return dp * scale + 0.5f
    }
    private fun sp2Px(sp: Float): Float{
        val fontScale = Resources.getSystem().displayMetrics.scaledDensity
        return sp * fontScale + 0.5f
    }

    private enum class Direction{
        LEFT,
        RIGHT
    }
}