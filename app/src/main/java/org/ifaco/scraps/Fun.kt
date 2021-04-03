package org.ifaco.scraps

import android.animation.*
import android.content.Context
import android.util.DisplayMetrics
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import kotlin.math.abs

class Fun {
    companion object {
        lateinit var c: Context
        var dm = DisplayMetrics()


        fun init(that: AppCompatActivity) {
            c = that.applicationContext
            dm = that.resources.displayMetrics
        }

        fun explode(
            c: Context, v: View, alpha: Float = 1f, dur: Long = 522, max: Float = 4f,
            src: Int = R.drawable.circle_ca
        ) {
            var parent: ConstraintLayout?
            try {
                parent = v.parent as ConstraintLayout
            } catch (ignored: java.lang.Exception) {
                return
            }
            if (parent == null) return

            var ex = View(c)
            var exLP = ConstraintLayout.LayoutParams(0, 0)
            exLP.topToTop = v.id
            exLP.leftToLeft = v.id
            exLP.rightToRight = v.id
            exLP.bottomToBottom = v.id
            ex.background = ContextCompat.getDrawable(c, src)
            ex.translationX = v.translationX
            ex.translationY = v.translationY
            ex.scaleX = v.scaleX
            ex.scaleY = v.scaleY
            ex.alpha = alpha
            parent.addView(ex, parent.indexOfChild(v), exLP)

            var explode = AnimatorSet().setDuration(dur)
            var hide = ObjectAnimator.ofFloat(ex, "alpha", 0f)
            hide.startDelay = explode.duration / 4
            explode.playTogether(
                ObjectAnimator.ofFloat(ex, "scaleX", ex.scaleX * max),
                ObjectAnimator.ofFloat(ex, "scaleY", ex.scaleY * max),
                hide
            )
            explode.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    parent.removeView(ex)
                }
            })
            explode.start()
        }

        fun dp(px: Int = 0) = (dm.density * px.toFloat()).toInt()

        fun vis(v: View, b: Boolean = true) {
            v.visibility = if (b) View.VISIBLE else View.GONE
        }

        fun toDeg(angle: Float) = angle * (Math.PI / 180)

        fun fromDeg(num: Float) = num / (Math.PI / 180)

        fun whirl(v: View, rev: Boolean = false): ValueAnimator? =
            ValueAnimator.ofFloat(0f, 360f).apply {
                duration = 920
                addUpdateListener {
                    (v.layoutParams as ConstraintLayout.LayoutParams).apply {
                        circleAngle = it.animatedValue as Float
                        v.layoutParams = this
                    }
                }
                repeatCount = ValueAnimator.INFINITE
                interpolator = LinearInterpolator()
                if (rev) interpolator = TimeInterpolator { abs(it - 1f) }
                start()
            }
    }
}