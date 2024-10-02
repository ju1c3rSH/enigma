package homes.gensokyo.enigma.widget

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.widget.LinearLayout

//import homes.gensokyo.zhiweixiaoyuan_rebuild.databinding.ZeroTitleViewBinding
//written by sky233
class ZeroLineLayout(context: Context, attr: AttributeSet) : LinearLayout(context, attr) {

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(paramMotionEvent: MotionEvent): Boolean {
        Log.d("ZeroLineLayout", paramMotionEvent.toString())
        if (isClickable) {
            if (paramMotionEvent.action != 0) {
                if (3 == paramMotionEvent.action || 1 == paramMotionEvent.action) animateToNormal()
                return super.onTouchEvent(paramMotionEvent)
            }
            animateToPress()
        }
        //return super.onTouchEvent(paramMotionEvent)
        return super.onTouchEvent(paramMotionEvent)
    }

    @SuppressLint("Recycle")
    private fun animateToNormal() {
        val animatorSet = AnimatorSet()
        animatorSet.duration = 250L
        val objectAnimator1 = ObjectAnimator.ofFloat(this, "scaleX", 0.92f, 1.0f)
        val objectAnimator2 = ObjectAnimator.ofFloat(this, "scaleY", 0.92f, 1.0f)
        val objectAnimator3 = ObjectAnimator.ofFloat(this, "alpha", 0.7f, 1.0f)
        animatorSet.play(objectAnimator1).with(objectAnimator2).with(objectAnimator3)
        animatorSet.start()
    }

    @SuppressLint("Recycle")
    private fun animateToPress() {
        val animatorSet = AnimatorSet()
        animatorSet.duration = 250L
        val objectAnimator1 = ObjectAnimator.ofFloat(this, "scaleX", 1.0f, 0.92f)
        val objectAnimator2 = ObjectAnimator.ofFloat(this, "scaleY", 1.0f, 0.92f)
        val objectAnimator3 = ObjectAnimator.ofFloat(this, "alpha", 1.0f, 0.7f)
        animatorSet.play(objectAnimator1).with(objectAnimator2).with(objectAnimator3)
        animatorSet.start()
    }


}