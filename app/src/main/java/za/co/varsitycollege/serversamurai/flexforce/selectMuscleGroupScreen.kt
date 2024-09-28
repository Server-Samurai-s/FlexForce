package za.co.varsitycollege.serversamurai.flexforce

import android.graphics.Matrix
import android.graphics.RectF
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SelectMuscleGroupScreen : Fragment() {

    private lateinit var ivBodyMapFront: ImageView
    private lateinit var ivBodyMapBack: ImageView
    private lateinit var overlayFront: HitboxOverlayView
    private lateinit var overlayBack: HitboxOverlayView
    private lateinit var viewSwitcher: ViewSwitcher
    private lateinit var btnSwitchView: Button
    private lateinit var rvMuscleGroups: RecyclerView
    private lateinit var tvClear: TextView
    private lateinit var tvApply: TextView

    // Overlay ImageViews
    private lateinit var overlayNeckFront: ImageView
    private lateinit var overlayShouldersFront: ImageView

    private lateinit var overlayTrapsBack: ImageView
    private lateinit var overlayDeltoidsBack: ImageView


    // Hitboxes for front view
    private val frontHitboxes = mutableListOf<Hitbox>()

    // Hitboxes for back view
    private val backHitboxes = mutableListOf<Hitbox>()

    private val selectedMuscles = mutableListOf<String>()

    private fun setupRecyclerView() {
        rvMuscleGroups.layoutManager = LinearLayoutManager(context)
        rvMuscleGroups.adapter = MuscleGroupAdapter(staticMuscleGroups, selectedMuscles) // Pass the selectedMuscles list
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_select_muscle_group_screen, container, false)

        // Initialize views
        ivBodyMapFront = view.findViewById(R.id.iv_body_map_front)
        ivBodyMapBack = view.findViewById(R.id.iv_body_map_back)
        overlayFront = view.findViewById(R.id.overlay_front)
        overlayBack = view.findViewById(R.id.overlay_back)
        viewSwitcher = view.findViewById(R.id.viewSwitcher)
        btnSwitchView = view.findViewById(R.id.btn_switch_view)
        rvMuscleGroups = view.findViewById(R.id.rv_muscle_groups)
        tvClear = view.findViewById(R.id.tv_clear)
        tvApply = view.findViewById(R.id.tv_apply)

        // Initialize RecyclerView
        rvMuscleGroups.layoutManager = LinearLayoutManager(context)

        // Initialize overlay ImageViews
        overlayNeckFront = view.findViewById(R.id.overlay_neck_front)
        overlayShouldersFront = view.findViewById(R.id.overlay_shoulders_front)

        overlayTrapsBack = view.findViewById(R.id.overlay_traps_back)
        overlayDeltoidsBack = view.findViewById(R.id.overlay_deltoids_back)

        // Set OnTouchListeners
        ivBodyMapFront.setOnTouchListener(frontTouchListener)
        ivBodyMapBack.setOnTouchListener(backTouchListener)

        // Switch View Button
        btnSwitchView.setOnClickListener {
            viewSwitcher.showNext()
        }

        // Initialize hitboxes after the layout is rendered
        ivBodyMapFront.post {
            initializeFrontHitboxes()
            // Set hitboxes and imageView to overlay
            overlayFront.hitboxes = frontHitboxes
            overlayFront.imageView = ivBodyMapFront
            overlayFront.invalidate()
        }

        ivBodyMapBack.post {
            initializeBackHitboxes()
            // Set hitboxes and imageView to overlay
            overlayBack.hitboxes = backHitboxes
            overlayBack.imageView = ivBodyMapBack
            overlayBack.invalidate()
        }

        // Set click listeners for Clear and Apply buttons
        tvClear.setOnClickListener {
            clearSelections()
        }

        tvApply.setOnClickListener {
            //applySelections()
        }

        setupRecyclerView()

        return view
    }

    // Touch listener for front view
    private val frontTouchListener = View.OnTouchListener { v, event ->
        if (event.action == MotionEvent.ACTION_DOWN) {
            val imageView = v as ImageView
            val touchedHitbox = getTouchedHitbox(imageView, event.x, event.y, frontHitboxes)
            touchedHitbox?.let {
                handleMuscleSelection(it.name)
                return@OnTouchListener true
            }
        }
        false
    }

    // Touch listener for back view
    private val backTouchListener = View.OnTouchListener { v, event ->
        if (event.action == MotionEvent.ACTION_DOWN) {
            val imageView = v as ImageView
            val touchedHitbox = getTouchedHitbox(imageView, event.x, event.y, backHitboxes)
            touchedHitbox?.let {
                handleMuscleSelection(it.name)
                return@OnTouchListener true
            }
        }
        false
    }

    // Class to represent a hitbox
    data class Hitbox(
        val name: String,
        val rect: RectF
    )

    data class MuscleGroup(
        val name: String,
        val muscles: List<String>
    )

    private val staticMuscleGroups = listOf(
        MuscleGroup("ARMS", listOf("Forearms", "Biceps", "Triceps")),
        MuscleGroup("SHOULDERS", listOf("Deltoids", "Rotator Cuff")),
        MuscleGroup("CHEST", listOf("Pectorals")),
        MuscleGroup("BACK", listOf("Upper Back", "Trapezius", "Paravertebrals")),
        MuscleGroup("CORE", listOf("Abdominals", "Lower Back", "Oblique", "Abdomen Transverse")),
        MuscleGroup("HIPS AND LEGS", listOf("Adductors", "Gluteus", "Hamstrings", "Calves", "Quadriceps", "Ileopsoas"))
    )


    private fun initializeFrontHitboxes() {
        val drawable = ivBodyMapFront.drawable ?: return
        val drawableWidth = drawable.intrinsicWidth.toFloat()
        val drawableHeight = drawable.intrinsicHeight.toFloat()

        frontHitboxes.clear()

        // Using the same coordinates as backHitboxes
        // Upper Chest (using Upper Back coordinates)
        val chestRect = RectF(
            0.36f * drawableWidth,
            0.17f * drawableHeight,
            0.64f * drawableWidth,
            0.27f * drawableHeight
        )
        frontHitboxes.add(Hitbox("Upper Chest", chestRect))

        // Forearms (Front)
        val leftForearmFrontRect = RectF(
            0.08f * drawableWidth,
            0.35f * drawableHeight,
            0.25f * drawableWidth,
            0.47f * drawableHeight
        )
        frontHitboxes.add(Hitbox("Left Forearm (Front)", leftForearmFrontRect))

        val rightForearmFrontRect = RectF(
            0.73f * drawableWidth,
            0.35f * drawableHeight,
            0.92f * drawableWidth,
            0.46f * drawableHeight
        )
        frontHitboxes.add(Hitbox("Right Forearm (Front)", rightForearmFrontRect))

        // Biceps (using Triceps coordinates)
        val leftBicepsRect = RectF(
            0.15f * drawableWidth,
            0.24f * drawableHeight,
            0.30f * drawableWidth,
            0.35f * drawableHeight
        )
        frontHitboxes.add(Hitbox("Left Biceps", leftBicepsRect))

        val rightBicepsRect = RectF(
            0.70f * drawableWidth,
            0.24f * drawableHeight,
            0.85f * drawableWidth,
            0.35f * drawableHeight
        )
        frontHitboxes.add(Hitbox("Right Biceps", rightBicepsRect))

        // Shoulders (using Rotator Cuff coordinates)
        val leftShoulderRect = RectF(
            0.18f * drawableWidth,
            0.17f * drawableHeight,
            0.35f * drawableWidth,
            0.24f * drawableHeight
        )
        frontHitboxes.add(Hitbox("Shoulders", leftShoulderRect))

        val rightShoulderRect = RectF(
            0.65f * drawableWidth,
            0.17f * drawableHeight,
            0.80f * drawableWidth,
            0.24f * drawableHeight
        )
        frontHitboxes.add(Hitbox("Shoulders", rightShoulderRect))

        // Neck (using Trapezius Back coordinates)
        val neckRect = RectF(
            0.31f * drawableWidth,
            0.08f * drawableHeight,
            0.69f * drawableWidth,
            0.17f * drawableHeight
        )
        frontHitboxes.add(Hitbox("Neck", neckRect))

        // Abdominals (using Paravertebrals coordinates)
        val abdominalsRect = RectF(
            0.31f * drawableWidth,
            0.27f * drawableHeight,
            0.69f * drawableWidth,
            0.43f * drawableHeight
        )
        frontHitboxes.add(Hitbox("Abdominals", abdominalsRect))

        // Hip Flexors (using Gluteus coordinates)
        val hipFlexorsRect = RectF(
            0.42f * drawableWidth,
            0.46f * drawableHeight,
            0.60f * drawableWidth,
            0.54f * drawableHeight
        )
        frontHitboxes.add(Hitbox("Hip Flexors", hipFlexorsRect))

        // Quadriceps (using Hamstrings coordinates)
        val leftQuadricepsRect = RectF(
            0.29f * drawableWidth,
            0.54f * drawableHeight,
            0.48f * drawableWidth,
            0.69f * drawableHeight
        )
        frontHitboxes.add(Hitbox("Left Quadriceps", leftQuadricepsRect))

        val rightQuadricepsRect = RectF(
            0.52f * drawableWidth,
            0.54f * drawableHeight,
            0.68f * drawableWidth,
            0.69f * drawableHeight
        )
        frontHitboxes.add(Hitbox("Right Quadriceps", rightQuadricepsRect))

        // Calves (Front)
        val leftTibialisFrontRect = RectF(
            0.29f * drawableWidth,
            0.72f * drawableHeight,
            0.48f * drawableWidth,
            0.92f * drawableHeight
        )
        frontHitboxes.add(Hitbox("Left Calf (Front)", leftTibialisFrontRect))

        val rightTibialisFrontRect = RectF(
            0.52f * drawableWidth,
            0.72f * drawableHeight,
            0.70f * drawableWidth,
            0.92f * drawableHeight
        )
        frontHitboxes.add(Hitbox("Right Calf (Front)", rightTibialisFrontRect))
    }

    private fun initializeBackHitboxes() {
        val drawable = ivBodyMapBack.drawable ?: return
        val drawableWidth = drawable.intrinsicWidth.toFloat()
        val drawableHeight = drawable.intrinsicHeight.toFloat()

        backHitboxes.clear()

        // Upper Back
        val upperBackRect = RectF(
            0.31f * drawableWidth,
            0.27f * drawableHeight,
            0.69f * drawableWidth,
            0.35f * drawableHeight
        )
        backHitboxes.add(Hitbox("Upper Back", upperBackRect))

        // Lower Back
        val lowerBackRect = RectF(
            0.35f * drawableWidth,
            0.35f * drawableHeight,
            0.65f * drawableWidth,
            0.42f * drawableHeight
        )
        backHitboxes.add(Hitbox("Lower Back", lowerBackRect))

        // Forearms (Back)
        val leftForearmBackRect = RectF(
            0.08f * drawableWidth,
            0.35f * drawableHeight,
            0.25f * drawableWidth,
            0.47f * drawableHeight
        )
        backHitboxes.add(Hitbox("Left Forearm (Back)", leftForearmBackRect))

        val rightForearmBackRect = RectF(
            0.73f * drawableWidth,
            0.35f * drawableHeight,
            0.92f * drawableWidth,
            0.46f * drawableHeight
        )
        backHitboxes.add(Hitbox("Right Forearm (Back)", rightForearmBackRect))

        // Triceps
        val leftTricepsRect = RectF(
            0.15f * drawableWidth,
            0.24f * drawableHeight,
            0.30f * drawableWidth,
            0.35f * drawableHeight
        )
        backHitboxes.add(Hitbox("Left Triceps", leftTricepsRect))

        val rightTricepsRect = RectF(
            0.70f * drawableWidth,
            0.24f * drawableHeight,
            0.85f * drawableWidth,
            0.35f * drawableHeight
        )
        backHitboxes.add(Hitbox("Right Triceps", rightTricepsRect))

        // Rotator Cuff
        val leftDeltoidRect = RectF(
            0.18f * drawableWidth,
            0.17f * drawableHeight,
            0.35f * drawableWidth,
            0.24f * drawableHeight
        )
        backHitboxes.add(Hitbox("Deltoids", leftDeltoidRect))

        val rightDeltoidRect = RectF(
            0.65f * drawableWidth,
            0.17f * drawableHeight,
            0.80f * drawableWidth,
            0.24f * drawableHeight
        )
        backHitboxes.add(Hitbox("Deltoids", rightDeltoidRect))

        // Trapezius (Back)
        val trapeziusBackRect = RectF(
            0.31f * drawableWidth,
            0.08f * drawableHeight,
            0.69f * drawableWidth,
            0.17f * drawableHeight
        )
        backHitboxes.add(Hitbox("Trapezius (Back)", trapeziusBackRect))

        // Paravertebrals
        val paravertebralsRect = RectF(
            0.38f * drawableWidth,
            0.17f * drawableHeight,
            0.62f * drawableWidth,
            0.27f * drawableHeight
        )
        backHitboxes.add(Hitbox("Paravertebrals", paravertebralsRect))

        // Gluteus
        val gluteusRect = RectF(
            0.32f * drawableWidth,
            0.42f * drawableHeight,
            0.67f * drawableWidth,
            0.54f * drawableHeight
        )
        backHitboxes.add(Hitbox("Gluteus", gluteusRect))

        // Hamstrings
        val leftHamstringRect = RectF(
            0.30f * drawableWidth,
            0.54f * drawableHeight,
            0.48f * drawableWidth,
            0.69f * drawableHeight
        )
        backHitboxes.add(Hitbox("Left Hamstring", leftHamstringRect))

        val rightHamstringRect = RectF(
            0.52f * drawableWidth,
            0.54f * drawableHeight,
            0.70f * drawableWidth,
            0.69f * drawableHeight
        )
        backHitboxes.add(Hitbox("Right Hamstring", rightHamstringRect))

        // Calves (Back)
        val leftCalfBackRect = RectF(
            leftHamstringRect.left,
            leftHamstringRect.bottom,
            leftHamstringRect.right,
            leftHamstringRect.bottom + 0.20f * drawableHeight
        )
        backHitboxes.add(Hitbox("Left Calf (Back)", leftCalfBackRect))

        val rightCalfBackRect = RectF(
            rightHamstringRect.left,
            rightHamstringRect.bottom,
            rightHamstringRect.right,
            rightHamstringRect.bottom + 0.20f * drawableHeight
        )
        backHitboxes.add(Hitbox("Right Calf (Back)", rightCalfBackRect))
    }

    private fun getTouchedHitbox(
        imageView: ImageView,
        touchX: Float,
        touchY: Float,
        hitboxes: List<Hitbox>
    ): Hitbox? {
        val drawable = imageView.drawable ?: return null

        // Get image matrix
        val imageMatrix = imageView.imageMatrix
        val inverseMatrix = Matrix()
        if (!imageMatrix.invert(inverseMatrix)) {
            return null
        }

        // Map touch coordinates to drawable coordinates
        val touchPoint = floatArrayOf(touchX, touchY)
        inverseMatrix.mapPoints(touchPoint)
        val x = touchPoint[0]
        val y = touchPoint[1]

        // Check if touch point is within drawable bounds
        val drawableWidth = drawable.intrinsicWidth.toFloat()
        val drawableHeight = drawable.intrinsicHeight.toFloat()
        if (x < 0 || x > drawableWidth || y < 0 || y > drawableHeight) {
            return null
        }

        for (hitbox in hitboxes) {
            if (hitbox.rect.contains(x, y)) {
                return hitbox
            }
        }
        return null
    }

    private fun handleMuscleSelection(muscleName: String) {
        // Toggle muscle selection
        if (selectedMuscles.contains(muscleName)) {
            selectedMuscles.remove(muscleName)
            Toast.makeText(context, "$muscleName deselected", Toast.LENGTH_SHORT).show()
            // Hide the corresponding overlay
            hideMuscleOverlay(muscleName)
        } else {
            selectedMuscles.add(muscleName)
            Toast.makeText(context, "$muscleName selected", Toast.LENGTH_SHORT).show()
            // Show the corresponding overlay
            showMuscleOverlay(muscleName)
        }
        rvMuscleGroups.adapter?.notifyDataSetChanged()

        // Update Apply button text with selected count
        updateApplyButton()
//        applySelections()
    }



    private fun showMuscleOverlay(muscleName: String) {
        when (muscleName) {
            "Neck" -> overlayNeckFront.visibility = View.VISIBLE
            "Shoulders" -> overlayShouldersFront.visibility = View.VISIBLE

            "Trapezius (Back)" -> overlayTrapsBack.visibility = View.VISIBLE
            "Deltoids" -> overlayDeltoidsBack.visibility = View.VISIBLE
            // Add other muscles as needed
        }
    }

    private fun hideMuscleOverlay(muscleName: String) {
        when (muscleName) {
            "Neck" -> overlayNeckFront.visibility = View.GONE
            "Shoulders" -> overlayShouldersFront.visibility = View.GONE

            "Trapezius (Back)" -> overlayTrapsBack.visibility = View.GONE
            "Deltoids" -> overlayDeltoidsBack.visibility = View.GONE
            // Add other muscles as needed
        }
    }

    private fun updateApplyButton() {
        tvApply.text = if (selectedMuscles.isNotEmpty()) {
            "APPLY (${selectedMuscles.size})"
        } else {
            "APPLY"
        }
    }

    private fun clearSelections() {
        // Clear all selections
        selectedMuscles.clear()
        updateApplyButton()

        // Hide all overlays
        overlayNeckFront.visibility = View.GONE
        overlayTrapsBack.visibility = View.GONE
        overlayDeltoidsBack.visibility = View.GONE
        overlayShouldersFront.visibility = View.GONE
        // Hide other overlays as needed

        Toast.makeText(context, "Selections cleared", Toast.LENGTH_SHORT).show()
    }

//    private fun applySelections() {
//        // Group muscles by body part
//        val muscleGroups = listOf(
//            MuscleGroup("ARMS", selectedMuscles.filter { it in listOf("Forearms", "Biceps", "Triceps") }),
//            MuscleGroup("SHOULDERS", selectedMuscles.filter { it in listOf("Deltoids", "Rotator Cuff") }),
//            MuscleGroup("CHEST", selectedMuscles.filter { it == "Pectorals" }),
//            MuscleGroup("BACK", selectedMuscles.filter { it in listOf("Upper Back", "Trapezius", "Paravertebrals") }),
//            MuscleGroup("CORE", selectedMuscles.filter { it in listOf("Abdominals", "Lower Back", "Oblique", "Abdomen Transverse") }),
//            MuscleGroup("HIPS AND LEGS", selectedMuscles.filter { it in listOf("Adductors", "Gluteus", "Hamstrings", "Calves", "Quadriceps", "Ileopsoas") })
//        ).filter { it.muscles.isNotEmpty() }  // Remove groups with no selected muscles
//
//        // Set the adapter for the RecyclerView
//        rvMuscleGroups.adapter = MuscleGroupAdapter(muscleGroups)
//    }
}
