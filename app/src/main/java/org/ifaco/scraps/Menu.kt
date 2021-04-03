package org.ifaco.scraps

import android.animation.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.blure.complexview.ComplexView
import org.ifaco.scraps.Fun.Companion.c
import org.ifaco.scraps.Fun.Companion.dm
import org.ifaco.scraps.Fun.Companion.dp
import org.ifaco.scraps.Fun.Companion.fromDeg
import org.ifaco.scraps.Fun.Companion.toDeg
import org.ifaco.scraps.Fun.Companion.vis
import kotlin.math.*

class Menu : AppCompatActivity() {
    lateinit var body: ConstraintLayout
    lateinit var firstSlide: ConstraintLayout
    lateinit var stars: ConstraintLayout
    lateinit var play: ComplexView
    lateinit var playBG: ConstraintLayout
    lateinit var playMark: ImageView
    lateinit var energy1: View
    lateinit var energy2: View
    lateinit var secondSlide: ConstraintLayout
    lateinit var projects: RecyclerView

    var loaded = false
    var slide = 0
    var tapToExit = false
    var projAdap: ProjAdap? = null
    var sliding = false

    companion object {
        lateinit var navHandler: Handler
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu)

        body = findViewById(R.id.body)
        firstSlide = findViewById(R.id.firstSlide)
        stars = findViewById(R.id.stars)
        play = findViewById(R.id.play)
        playBG = findViewById(R.id.playBG)
        playMark = findViewById(R.id.playMark)
        energy1 = findViewById(R.id.energy1)
        energy2 = findViewById(R.id.energy2)
        secondSlide = findViewById(R.id.secondSlide)
        projects = findViewById(R.id.projects)

        Fun.init(this)


        // Handlers
        navHandler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                if (msg.obj !is Project) return
                val p = msg.obj as Project
                startActivity(Intent(c, p.jClass))
            }
        }

        // Play Button
        playBG.setOnClickListener {
            Fun.explode(c, play, 0.15f)
            slide(1)
        }
        if (!loaded) AnimatorSet().apply {
            duration = 2000
            playTogether(
                ObjectAnimator.ofFloat(play, "scaleX", 1f),
                ObjectAnimator.ofFloat(play, "scaleY", 1f),
                ObjectAnimator.ofFloat(play, "rotation", 0f)
            )
            interpolator = DecelerateInterpolator()
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    loaded = true
                }
            })
            start()
            val delay = duration / 2
            object : CountDownTimer(delay, delay) {
                override fun onTick(p0: Long) {}
                override fun onFinish() {
                    Fun.explode(c, play, 0.3f, 1500, 8f)
                }
            }.start()
        } else {
            play.scaleX = 1f
            play.scaleY = 1f
            play.rotation = 0f
        }

        // Energy Animations
        val egd = GradientDrawable()
        egd.shape = GradientDrawable.OVAL
        egd.gradientType = GradientDrawable.RADIAL_GRADIENT
        val ca = ContextCompat.getColor(c, R.color.CA)
        val caAlpha1 = Color.argb(92, Color.red(ca), Color.green(ca), Color.blue(ca))
        egd.colors = intArrayOf(ca, ca, caAlpha1, Color.TRANSPARENT)
        egd.gradientRadius = dm.density * 10f
        energy1.background = egd
        energy2.background = egd
        //whirl(energy1)
        //whirl(energy2, true)
        vis(energy1, false)
        vis(energy2, false)

        // Bouncers
        val bouncersNum = (dm.widthPixels.toFloat() * dm.heightPixels.toFloat() / 50000f).toInt()
        for (z in 0 until bouncersNum) {
            var bouncer = bouncer(c)
            stars.addView(bouncer)
            bounce(bouncer, (0..360).random().toFloat())//Random.nextInt(360).toFloat()
        }

        // Second Slide
        secondSlide.translationY = dm.heightPixels.toFloat()
        secondSlide.setOnClickListener { }
        projAdap = ProjAdap(
            c, listOf(
                Project("My Wi-Fi", "2020.10.31", org.ifaco.scraps.proj.MyWiFi::class.java)
            )
        )
        projects.adapter = projAdap

        //startActivity(Intent(c, org.ifaco.scraps.proj.MyWiFi::class.java))
    }

    override fun onSaveInstanceState(state: Bundle) {// With "outPersistentState" doesn't work!
        state.putBoolean("loaded", loaded)
        super.onSaveInstanceState(state)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        restoration(savedInstanceState)
    }

    override fun onBackPressed() {
        if (slide == 1) {
            slide(0); return; }
        if (!tapToExit) {
            Toast.makeText(c, R.string.tapToExit, Toast.LENGTH_LONG).show()
            tapToExit = true
            object : CountDownTimer(3000, 3000) {
                override fun onTick(p0: Long) {}
                override fun onFinish() {
                    tapToExit = false
                }
            }.start(); return
        }
        moveTaskToBack(true)
        Process.killProcess(Process.myPid())
        kotlin.system.exitProcess(1)
    }


    fun restoration(state: Bundle?) {
        if (state == null) return
        loaded = state.getBoolean("loaded", false)
    }

    fun bouncer(c: Context) = View(c).apply {
        layoutParams = ConstraintLayout.LayoutParams(dp(15), dp(15)).apply {
            var par = ConstraintLayout.LayoutParams.PARENT_ID
            topToTop = par
            leftToLeft = par
            rightToRight = par
            bottomToBottom = par
        }
        background = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            gradientType = GradientDrawable.RADIAL_GRADIENT
            val ca = ContextCompat.getColor(c, R.color.mBouncer)
            val caAlpha1 = Color.rgb(Color.red(ca), Color.green(ca), Color.blue(ca))
            colors = intArrayOf(ca, ca, caAlpha1, Color.TRANSPARENT)
            gradientRadius = layoutParams.width / 2f
        }
        alpha = 0.5f
    }

    fun bounce(v: View, d: Float) {
        val halfSW = dm.widthPixels / 2
        val halfSH = dm.heightPixels / 2
        var tX = v.translationX
        var tY = v.translationY
        var adjacent: Float
        var opposite: Float
        var hypotenuse: Float
        var angleC: Float
        var nextDeg: Float
        val trbl = floatArrayOf(
            abs(-halfSH - tY) - (v.layoutParams.height / 2f),
            abs(halfSW - tX) - (v.layoutParams.width / 2f),
            abs(halfSH - tY) - (v.layoutParams.height / 2f),
            abs(-halfSW - tX) - (v.layoutParams.width / 2f)
        )
        val angles = FloatArray(8)
        angles[0] = aSine((sin(toDeg(90f)).toFloat() * trbl[1]) / sep(trbl[1], trbl[0]))
        angles[1] = otherAng(angles[0])
        angles[2] = aSine((sin(toDeg(90f)).toFloat() * trbl[2]) / sep(trbl[2], trbl[1]))
        angles[3] = otherAng(angles[2])
        angles[4] = aSine((sin(toDeg(90f)).toFloat() * trbl[3]) / sep(trbl[3], trbl[2]))
        angles[5] = otherAng(angles[4])
        angles[6] = aSine((sin(toDeg(90f)).toFloat() * trbl[0]) / sep(trbl[0], trbl[3]))
        angles[7] = otherAng(angles[6])

        when {// Summarize at the final moment
            d >= 0f && d < 0f + angles[0] -> {// 1
                adjacent = trbl[0]
                angleC = otherAng(d)
                opposite = (adjacent * sin(toDeg(d)).toFloat()) / sin(toDeg(angleC)).toFloat()
                tX += opposite
                tY -= adjacent
                nextDeg = 90f + angleC
            }
            d >= 0f + angles[0] && d < 90f -> {// 2
                adjacent = trbl[1]
                angleC = otherAng(90f - d)
                opposite = (adjacent * sin(toDeg(90f - d)).toFloat()) / sin(toDeg(angleC)).toFloat()
                tX += adjacent
                tY -= opposite
                nextDeg = 270f + angleC
            }
            d >= 90f && d < 90f + angles[2] -> {// 3
                adjacent = trbl[1]
                angleC = otherAng(d - 90f)
                opposite = (adjacent * sin(toDeg(d - 90f)).toFloat()) / sin(toDeg(angleC)).toFloat()
                tX += adjacent
                tY += opposite
                nextDeg = 180f + angleC
            }
            d >= 90f + angles[2] && d < 180f -> {// 4
                adjacent = trbl[2]
                angleC = otherAng(180f - d)
                opposite =
                    (adjacent * sin(toDeg(180f - d)).toFloat()) / sin(toDeg(angleC)).toFloat()
                tX += opposite
                tY += adjacent
                nextDeg = 90f - angleC
            }
            d >= 180f && d < 180f + angles[4] -> {// 5
                adjacent = trbl[2]
                angleC = otherAng(d - 180f)
                opposite =
                    (adjacent * sin(toDeg(d - 180f)).toFloat()) / sin(toDeg(angleC)).toFloat()
                tX -= opposite
                tY += adjacent
                nextDeg = 270f + angleC
            }
            d >= 180f + angles[4] && d < 270f -> {// 6
                adjacent = trbl[3]
                angleC = otherAng(270f - d)
                opposite =
                    (adjacent * sin(toDeg(270f - d)).toFloat()) / sin(toDeg(angleC)).toFloat()
                tX -= adjacent
                tY += opposite
                nextDeg = 180f - angleC
            }
            d >= 270f && d < 270f + angles[6] -> {// 7
                adjacent = trbl[3]
                angleC = otherAng(d - 270f)
                opposite =
                    (adjacent * sin(toDeg(d - 270f)).toFloat()) / sin(toDeg(angleC)).toFloat()
                tX -= adjacent
                tY -= opposite
                nextDeg = angleC
            }
            d >= 270f + angles[6] && d < 360f -> {// 8
                adjacent = abs(-halfSH - tY) - (v.layoutParams.height / 2f)
                angleC = otherAng(360f - d)
                opposite =
                    (adjacent * sin(toDeg(360f - d)).toFloat()) / sin(toDeg(angleC)).toFloat()
                tX -= opposite
                tY -= adjacent
                nextDeg = 270f - angleC
            }
            else -> {
                (v.parent as ViewGroup).removeView(v); return; }
        }
        hypotenuse = (adjacent * sin(toDeg(90f)).toFloat()) / sin(toDeg(angleC)).toFloat()

        AnimatorSet().apply {
            duration = (hypotenuse * 0.25f).toLong()
            interpolator = LinearInterpolator()////////
            playTogether(
                ObjectAnimator.ofFloat(v, "translationX", tX),
                ObjectAnimator.ofFloat(v, "translationY", tY)
            )
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    bounce(v, nextDeg)
                }
            })
            start()
        }
    }

    fun sep(b: Float, c: Float) = sqrt(
        (b.toDouble().pow(2.0) + c.toDouble().pow(2.0)) - ((2f * b * c) * cos(toDeg(90f)))
    ).toFloat()

    fun aSine(x: Float) = fromDeg(asin(x)).toFloat()

    fun otherAng(a: Float) = 180f - 90f - a

    fun slide(i: Int) {
        if (slide == i || sliding) return
        sliding = true
        AnimatorSet().apply {
            duration = 650
            playTogether(
                ObjectAnimator.ofFloat(firstSlide, "scaleX", if (i == 0) 1f else 0.8f),
                ObjectAnimator.ofFloat(firstSlide, "scaleY", if (i == 0) 1f else 0.8f),
                ObjectAnimator.ofFloat(
                    secondSlide, "translationY", if (i == 1) 0f else dm.heightPixels.toFloat()
                )
            )
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator?) {
                    if (i == 1) vis(secondSlide)
                }

                override fun onAnimationEnd(animation: Animator?) {
                    if (i == 0) vis(secondSlide, false)
                    sliding = false
                }
            })
            start()
        }
        slide = i
    }


    data class Project(val name: String, val startDate: String, val jClass: Class<*>)
}
