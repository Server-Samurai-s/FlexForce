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

    // Overlay Front ImageViews
    private lateinit var overlayNeckFront: ImageView
    private lateinit var overlayShouldersFront: ImageView
    private lateinit var overlayChestFront: ImageView
    private lateinit var overlayBicepsFront: ImageView
    private lateinit var overlayObliquesFront: ImageView
    private lateinit var overlayAbsFront: ImageView
    private lateinit var overlayInnerThighsFront: ImageView
    private lateinit var overlayForearmsFront: ImageView
    private lateinit var overlayQuadsFront: ImageView
    private lateinit var overlayTibsFront: ImageView
    private lateinit var overlayHipsFront: ImageView

    // Overlay Back ImageViews
    private lateinit var overlayTrapsBack: ImageView
    private lateinit var overlayDeltoidsBack: ImageView
    private lateinit var overlayParaBack: ImageView
    private lateinit var overlayTricepsBack: ImageView
    private lateinit var overlayUpperBack: ImageView
    private lateinit var overlayLowerBack: ImageView
    private lateinit var overlayGlutesBack: ImageView
    private lateinit var overlayForearmsBack: ImageView
    private lateinit var overlayHamstringsBack: ImageView
    private lateinit var overlayCalvesBack: ImageView
    private lateinit var overlayHipsBack: ImageView


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
        overlayChestFront = view.findViewById(R.id.overlay_chest_front)
        overlayBicepsFront = view.findViewById(R.id.overlay_biceps_front)
        overlayObliquesFront = view.findViewById(R.id.overlay_obliques_front)
        overlayAbsFront = view.findViewById(R.id.overlay_abs_front)
        overlayInnerThighsFront = view.findViewById(R.id.overlay_inner_thighs_front)
        overlayForearmsFront = view.findViewById(R.id.overlay_forearms_front)
        overlayQuadsFront = view.findViewById(R.id.overlay_quads_front)
        overlayTibsFront = view.findViewById(R.id.overlay_tibs_front)
        overlayHipsFront = view.findViewById(R.id.overlay_hips_front)


        overlayTrapsBack = view.findViewById(R.id.overlay_traps_back)
        overlayDeltoidsBack = view.findViewById(R.id.overlay_deltoids_back)
        overlayParaBack = view.findViewById(R.id.overlay_para_back)
        overlayTricepsBack = view.findViewById(R.id.overlay_triceps_back)
        overlayUpperBack = view.findViewById(R.id.overlay_upper_back)
        overlayLowerBack = view.findViewById(R.id.overlay_lower_back)
        overlayGlutesBack = view.findViewById(R.id.overlay_glutes_back)
        overlayForearmsBack = view.findViewById(R.id.overlay_forearms_back)
        overlayHamstringsBack = view.findViewById(R.id.overlay_hamstrings_back)
        overlayCalvesBack = view.findViewById(R.id.overlay_calves_back)
        overlayHipsBack = view.findViewById(R.id.overlay_hips_back)

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
        frontHitboxes.add(Hitbox("Chest", chestRect))

        // Forearms (Front)
        val leftForearmFrontRect = RectF(
            0.08f * drawableWidth,
            0.35f * drawableHeight,
            0.25f * drawableWidth,
            0.47f * drawableHeight
        )
        frontHitboxes.add(Hitbox("Forearms", leftForearmFrontRect))

        val rightForearmFrontRect = RectF(
            0.73f * drawableWidth,
            0.35f * drawableHeight,
            0.92f * drawableWidth,
            0.46f * drawableHeight
        )
        frontHitboxes.add(Hitbox("Forearms", rightForearmFrontRect))

        // Biceps (using Triceps coordinates)
        val leftBicepsRect = RectF(
            0.15f * drawableWidth,
            0.24f * drawableHeight,
            0.30f * drawableWidth,
            0.35f * drawableHeight
        )
        frontHitboxes.add(Hitbox("Biceps", leftBicepsRect))

        val rightBicepsRect = RectF(
            0.70f * drawableWidth,
            0.24f * drawableHeight,
            0.85f * drawableWidth,
            0.35f * drawableHeight
        )
        frontHitboxes.add(Hitbox("Biceps", rightBicepsRect))

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
            0.40f * drawableWidth,
            0.27f * drawableHeight,
            0.60f * drawableWidth,
            0.43f * drawableHeight
        )
        frontHitboxes.add(Hitbox("Abdominals", abdominalsRect))

        val leftObliqueRect = RectF(
            0.30f * drawableWidth,
            0.27f * drawableHeight,
            0.40f * drawableWidth,
            0.43f * drawableHeight
        )
        frontHitboxes.add(Hitbox("Obliques", leftObliqueRect))

        val rightObliqueRect = RectF(
            0.60f * drawableWidth,
            0.27f * drawableHeight,
            0.70f * drawableWidth,
            0.43f * drawableHeight
        )
        frontHitboxes.add(Hitbox("Obliques", rightObliqueRect))

        val leftHipRect = RectF(
            0.25f * drawableWidth,
            0.43f * drawableHeight,
            0.38f * drawableWidth,
            0.50f * drawableHeight
        )
        frontHitboxes.add(Hitbox("Hips", leftHipRect))

        val rightHipRect = RectF(
            0.62f * drawableWidth,
            0.43f * drawableHeight,
            0.73f * drawableWidth,
            0.50f * drawableHeight
        )
        frontHitboxes.add(Hitbox("Hips", rightHipRect))

        // Hip Flexors (using Gluteus coordinates)
        val hipFlexorsRect = RectF(
            0.42f * drawableWidth,
            0.46f * drawableHeight,
            0.60f * drawableWidth,
            0.54f * drawableHeight
        )
        frontHitboxes.add(Hitbox("Inner Thighs", hipFlexorsRect))

        // Quadriceps (using Hamstrings coordinates)
        val leftQuadricepsRect = RectF(
            0.29f * drawableWidth,
            0.50f * drawableHeight,
            0.48f * drawableWidth,
            0.69f * drawableHeight
        )
        frontHitboxes.add(Hitbox("Quadriceps", leftQuadricepsRect))

        val rightQuadricepsRect = RectF(
            0.52f * drawableWidth,
            0.50f * drawableHeight,
            0.68f * drawableWidth,
            0.69f * drawableHeight
        )
        frontHitboxes.add(Hitbox("Quadriceps", rightQuadricepsRect))

        // Calves (Front)
        val leftTibialisFrontRect = RectF(
            0.29f * drawableWidth,
            0.72f * drawableHeight,
            0.48f * drawableWidth,
            0.92f * drawableHeight
        )
        frontHitboxes.add(Hitbox("Tibialis", leftTibialisFrontRect))

        val rightTibialisFrontRect = RectF(
            0.52f * drawableWidth,
            0.72f * drawableHeight,
            0.70f * drawableWidth,
            0.92f * drawableHeight
        )
        frontHitboxes.add(Hitbox("Tibialis", rightTibialisFrontRect))
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
        backHitboxes.add(Hitbox("Forearms", leftForearmBackRect))

        val rightForearmBackRect = RectF(
            0.73f * drawableWidth,
            0.35f * drawableHeight,
            0.92f * drawableWidth,
            0.46f * drawableHeight
        )
        backHitboxes.add(Hitbox("Forearms", rightForearmBackRect))

        // Triceps
        val leftTricepsRect = RectF(
            0.15f * drawableWidth,
            0.24f * drawableHeight,
            0.30f * drawableWidth,
            0.35f * drawableHeight
        )
        backHitboxes.add(Hitbox("Triceps", leftTricepsRect))

        val rightTricepsRect = RectF(
            0.70f * drawableWidth,
            0.24f * drawableHeight,
            0.85f * drawableWidth,
            0.35f * drawableHeight
        )
        backHitboxes.add(Hitbox("Triceps", rightTricepsRect))

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
        backHitboxes.add(Hitbox("Trapezius", trapeziusBackRect))

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
            0.38f * drawableWidth,
            0.42f * drawableHeight,
            0.62f * drawableWidth,
            0.54f * drawableHeight
        )
        backHitboxes.add(Hitbox("Gluteus", gluteusRect))

        val leftHipRect = RectF(
            0.28f * drawableWidth,
            0.42f * drawableHeight,
            0.38f * drawableWidth,
            0.48f * drawableHeight
        )
        backHitboxes.add(Hitbox("Hips", leftHipRect))

        val rightHipRect = RectF(
            0.62f * drawableWidth,
            0.42f * drawableHeight,
            0.72f * drawableWidth,
            0.48f * drawableHeight
        )
        backHitboxes.add(Hitbox("Hips", rightHipRect))


        // Hamstrings
        val leftHamstringRect = RectF(
            0.30f * drawableWidth,
            0.54f * drawableHeight,
            0.48f * drawableWidth,
            0.69f * drawableHeight
        )
        backHitboxes.add(Hitbox("Hamstrings", leftHamstringRect))

        val rightHamstringRect = RectF(
            0.52f * drawableWidth,
            0.54f * drawableHeight,
            0.70f * drawableWidth,
            0.69f * drawableHeight
        )
        backHitboxes.add(Hitbox("Hamstrings", rightHamstringRect))

        // Calves (Back)
        val leftCalfBackRect = RectF(
            leftHamstringRect.left,
            leftHamstringRect.bottom,
            leftHamstringRect.right,
            leftHamstringRect.bottom + 0.20f * drawableHeight
        )
        backHitboxes.add(Hitbox("Calves", leftCalfBackRect))

        val rightCalfBackRect = RectF(
            rightHamstringRect.left,
            rightHamstringRect.bottom,
            rightHamstringRect.right,
            rightHamstringRect.bottom + 0.20f * drawableHeight
        )
        backHitboxes.add(Hitbox("Calves", rightCalfBackRect))
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
            "Chest" -> overlayChestFront.visibility = View.VISIBLE
            "Biceps" -> overlayBicepsFront.visibility = View.VISIBLE
            "Obliques" -> overlayObliquesFront.visibility = View.VISIBLE
            "Abdominals" -> overlayAbsFront.visibility = View.VISIBLE
            "Inner Thighs" -> overlayInnerThighsFront.visibility = View.VISIBLE
            "Forearms" -> {
                overlayForearmsFront.visibility = View.VISIBLE
                overlayForearmsBack.visibility = View.VISIBLE
            }
            "Quadriceps" -> overlayQuadsFront.visibility = View.VISIBLE
            "Tibialis" -> overlayTibsFront.visibility = View.VISIBLE
            "Hips" -> {
                overlayHipsFront.visibility = View.VISIBLE
                overlayHipsBack.visibility = View.VISIBLE
            }


            "Trapezius" -> overlayTrapsBack.visibility = View.VISIBLE
            "Deltoids" -> overlayDeltoidsBack.visibility = View.VISIBLE
            "Paravertebrals" -> overlayParaBack.visibility = View.VISIBLE
            "Triceps" -> overlayTricepsBack.visibility = View.VISIBLE
            "Upper Back" -> overlayUpperBack.visibility = View.VISIBLE
            "Lower Back" -> overlayLowerBack.visibility = View.VISIBLE
            "Gluteus" -> overlayGlutesBack.visibility = View.VISIBLE
            "Hamstrings" -> overlayHamstringsBack.visibility = View.VISIBLE
            "Calves" -> overlayCalvesBack.visibility = View.VISIBLE

        }
    }

    private fun hideMuscleOverlay(muscleName: String) {
        when (muscleName) {
            "Neck" -> overlayNeckFront.visibility = View.GONE
            "Shoulders" -> overlayShouldersFront.visibility = View.GONE
            "Chest" -> overlayChestFront.visibility = View.GONE
            "Biceps" -> overlayBicepsFront.visibility = View.GONE
            "Obliques" -> overlayObliquesFront.visibility = View.GONE
            "Abdominals" -> overlayAbsFront.visibility = View.GONE
            "Inner Thighs" -> overlayInnerThighsFront.visibility = View.GONE
            "Forearms" -> {
                overlayForearmsFront.visibility = View.GONE
                overlayForearmsBack.visibility = View.GONE
            }
            "Quadriceps" -> overlayQuadsFront.visibility = View.GONE
            "Tibialis" -> overlayTibsFront.visibility = View.GONE
            "Hips" -> {
                overlayHipsFront.visibility = View.GONE
                overlayHipsBack.visibility = View.GONE
            }

            "Trapezius" -> overlayTrapsBack.visibility = View.GONE
            "Deltoids" -> overlayDeltoidsBack.visibility = View.GONE
            "Paravertebrals" -> overlayParaBack.visibility = View.GONE
            "Triceps" -> overlayTricepsBack.visibility = View.GONE
            "Upper Back" -> overlayUpperBack.visibility = View.GONE
            "Lower Back" -> overlayLowerBack.visibility = View.GONE
            "Gluteus" -> overlayGlutesBack.visibility = View.GONE
            "Hamstrings" -> overlayHamstringsBack.visibility = View.GONE
            "Calves" -> overlayCalvesBack.visibility = View.GONE
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
        overlayShouldersFront.visibility = View.GONE
        overlayChestFront.visibility = View.GONE
        overlayBicepsFront.visibility = View.GONE
        overlayObliquesFront.visibility = View.GONE
        overlayAbsFront.visibility = View.GONE
        overlayInnerThighsFront.visibility = View.GONE
        overlayForearmsFront.visibility = View.GONE
        overlayQuadsFront.visibility = View.GONE
        overlayTibsFront.visibility = View.GONE
        overlayHipsFront.visibility = View.GONE

        overlayTrapsBack.visibility = View.GONE
        overlayDeltoidsBack.visibility = View.GONE
        overlayParaBack.visibility = View.GONE
        overlayTricepsBack.visibility = View.GONE
        overlayUpperBack.visibility = View.GONE
        overlayForearmsBack.visibility = View.GONE
        overlayLowerBack.visibility = View.GONE
        overlayGlutesBack.visibility = View.GONE
        overlayHamstringsBack.visibility = View.GONE
        overlayCalvesBack.visibility = View.GONE
        overlayHipsBack.visibility = View.GONE


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
