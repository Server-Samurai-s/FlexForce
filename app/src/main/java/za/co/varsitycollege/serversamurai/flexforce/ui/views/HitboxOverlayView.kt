package za.co.varsitycollege.serversamurai.flexforce.ui.views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import za.co.varsitycollege.serversamurai.flexforce.ui.fragments.workout.SelectMuscleGroupScreen

class HitboxOverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var hitboxes: List<SelectMuscleGroupScreen.Hitbox> = emptyList()
    var imageView: ImageView? = null

    private val paint = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.RED
        strokeWidth = 2f
        isAntiAlias = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        imageView?.let { iv ->
            val drawable = iv.drawable ?: return

            // Get the image matrix values and place them in an array
            val values = FloatArray(9)
            iv.imageMatrix.getValues(values)

            // Extract the scale and translation values
            val scaleX = values[Matrix.MSCALE_X]
            val scaleY = values[Matrix.MSCALE_Y]
            val translateX = values[Matrix.MTRANS_X] + iv.left
            val translateY = values[Matrix.MTRANS_Y] + iv.top

            canvas.save()
            canvas.translate(translateX, translateY)
            canvas.scale(scaleX, scaleY)

            // Draw each hitbox
            for (hitbox in hitboxes) {
                canvas.drawRect(hitbox.rect, paint)
            }

            canvas.restore()
        }
    }
}
