package com.vadym.birthday.ui.gift

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdView
import com.vadym.birthday.R
import com.vadym.birthday.ui.Admob
import com.vadym.birthday.ui.BaseActivity
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class GiftCardActivity : BaseActivity() {
    private val giftVM by viewModel<GiftCardViewModel>()
    private lateinit var cardContainer: FrameLayout
    private var dX = 0f
    private var dY = 0f

    override fun init(savedInstanceState: Bundle?) {
        super.setContentView(R.layout.view_gift_card)

        cardContainer = findViewById(R.id.cardContainer)
        val parentImageGifts = findViewById<RecyclerView>(R.id.parentImageGifts)
        val childImageGifts = findViewById<RecyclerView>(R.id.childImageGifts)
        val cardBackground = findViewById<ImageView>(R.id.cardBackground)

        findViewById<ImageView>(R.id.shareButton).setOnClickListener {
            shareCardContainer()
        }

        val adContainer: AdView = findViewById(R.id.adView)

        if (isNetworkAvailable(this)) {
            adContainer.visibility = View.VISIBLE
            Admob.initializeAdmob(this, adContainer)
        } else {
            adContainer.visibility = View.GONE
        }

        val parentGiftList = listOf(
            R.drawable.menu_bg,
            R.drawable.menu_cake,
            R.drawable.menu_box,
            R.drawable.menu_ballon,
            R.drawable.menu_hlopushka,
            R.drawable.menu_icecream,
            R.drawable.menu_cap,
            R.drawable.menu_butterfly,
            R.drawable.menu_animal,
            R.drawable.menu_flower,
            R.drawable.menu_text
        )

        val childImageMap = mapOf(
            R.drawable.menu_bg to listOf(
                R.drawable.bg1, R.drawable.bg2, R.drawable.bg3, R.drawable.bg4, R.drawable.bg5,
                R.drawable.bg6, R.drawable.bg8, R.drawable.bg9, R.drawable.bg10,
                R.drawable.bg11, R.drawable.bg12, R.drawable.bg13, R.drawable.bg14, R.drawable.bg15, R.drawable.white_bg),
            R.drawable.menu_cake to listOf(R.drawable.cake1, R.drawable.cake2, R.drawable.cake3,
                R.drawable.cake4, R.drawable.cake5, R.drawable.cake6, R.drawable.cake7, R.drawable.cake8,
                R.drawable.cake9, R.drawable.cake10, R.drawable.cake11, R.drawable.cake12, R.drawable.cake13, R.drawable.cake14),
            R.drawable.menu_box to listOf(
                R.drawable.box1, R.drawable.box2, R.drawable.box3, R.drawable.box4,
                R.drawable.box5, R.drawable.box6, R.drawable.box7, R.drawable.box8),
            R.drawable.menu_ballon to listOf(R.drawable.balloon1, R.drawable.balloon2, R.drawable.balloon3,
                R.drawable.balloon4, R.drawable.balloon5, R.drawable.balloon6, R.drawable.balloon7,
                R.drawable.balloon8, R.drawable.balloon9, R.drawable.balloon10, R.drawable.balloon11),
            R.drawable.menu_hlopushka to listOf(R.drawable.hlopushka, R.drawable.hlopushka2,
                R.drawable.hlopushka3, R.drawable.hlopushka4, R.drawable.hlopushka5, R.drawable.hlopushka6, R.drawable.hlopushka8),
            R.drawable.menu_icecream to listOf(R.drawable.ice1, R.drawable.ice2, R.drawable.ice3, R.drawable.ice4,
                R.drawable.ice5, R.drawable.ice6, R.drawable.ice7, R.drawable.ice8, R.drawable.ice9, R.drawable.ice10),
            R.drawable.menu_cap to listOf(R.drawable.cap, R.drawable.cap1, R.drawable.cap2, R.drawable.cap3,
                R.drawable.cap4, R.drawable.cap5, R.drawable.cap6, R.drawable.cap7, R.drawable.cap8),
            R.drawable.menu_butterfly to listOf(R.drawable.butterfly1, R.drawable.butterfly2, R.drawable.butterfly3,
                R.drawable.butterfly4, R.drawable.butterfly5, R.drawable.butterfly6, R.drawable.butterfly7),
            R.drawable.menu_animal to listOf(R.drawable.animal1, R.drawable.animal2, R.drawable.animal3,
                R.drawable.animal4, R.drawable.animal5, R.drawable.animal6, R.drawable.animal7,
                R.drawable.animal8, R.drawable.animal10, R.drawable.animal11, R.drawable.animal12,
                R.drawable.animal13, R.drawable.animal14, R.drawable.animal15, R.drawable.animal16),
            R.drawable.menu_flower to listOf(R.drawable.flower1, R.drawable.flower2, R.drawable.flower3,
                R.drawable.flower4, R.drawable.flower5, R.drawable.flower6, R.drawable.flower7, R.drawable.flower8, R.drawable.flower9),
            R.drawable.menu_text to listOf(R.drawable.hb_text1, R.drawable.hb_text2, R.drawable.hb_text3,
                    R.drawable.hb_text5)
        )

        parentImageGifts.layoutManager = LinearLayoutManager(this, LinearLayout.HORIZONTAL, false)
        childImageGifts.layoutManager = LinearLayoutManager(this, LinearLayout.HORIZONTAL, false)

        parentImageGifts.adapter = GiftMenuAdapter(this, parentGiftList) { selectedImage ->
            val childList = childImageMap[selectedImage] ?: emptyList()
            childImageGifts.adapter = GiftMenuAdapter(this, childList) { selectedChild ->
                when (selectedImage) {
                    R.drawable.menu_bg -> cardBackground.setImageDrawable(ContextCompat.getDrawable(this, selectedChild))
                    R.drawable.menu_cake -> createNewImage(selectedChild)
                    R.drawable.menu_box -> createNewImage(selectedChild)
                    R.drawable.menu_ballon -> createNewImage(selectedChild)
                    R.drawable.menu_hlopushka -> createNewImage(selectedChild)
                    R.drawable.menu_icecream -> createNewImage(selectedChild)
                    R.drawable.menu_cap -> createNewImage(selectedChild)
                    R.drawable.menu_butterfly -> createNewImage(selectedChild)
                    R.drawable.menu_animal -> createNewImage(selectedChild)
                    R.drawable.menu_flower -> createNewImage(selectedChild)
                    R.drawable.menu_text -> createNewImage(selectedChild)
                    else -> Toast.makeText(this, "Clicked on item", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    private fun createNewImage(selectedImgDrawable: Int) {
        val newImage = ImageView(this)
        newImage.setImageResource(selectedImgDrawable)
        newImage.layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        newImage.x = 50F
        newImage.y = 50F

//        val scaleGestureDetector = ScaleGestureDetector(this, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
//            override fun onScale(detector: ScaleGestureDetector): Boolean {
//                val scaleFactor = detector.scaleFactor
//                val newWidth = (newImage.width * scaleFactor).toInt()
//                val newHeight = (newImage.height * scaleFactor).toInt()
//
//                // Prevents image from getting too small or too big
//                if (newWidth in 100..800 && newHeight in 100..800) {
//                    newImage.layoutParams = FrameLayout.LayoutParams(newWidth, newHeight)
//                    newImage.requestLayout()
//                }
//                return true
//            }
//        })

        newImage.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    dX = view.x - event.rawX
                    dY = view.y - event.rawY
                }
                MotionEvent.ACTION_MOVE -> {
                    view.x = event.rawX + dX
                    view.y = event.rawY + dY
                }

                MotionEvent.ACTION_UP -> {
                    if (!isInsideParent(view, cardContainer)) {
                        cardContainer.removeView(view)
                    }
                }
//                MotionEvent.ACTION_POINTER_DOWN, MotionEvent.ACTION_POINTER_UP -> {
//                    // Handle pinch zoom with multiple fingers
//                    scaleGestureDetector.onTouchEvent(event)
//                }
//                MotionEvent.ACTION_MOVE -> {
//                    scaleGestureDetector.onTouchEvent(event)
//                }
            }
            true
        }

        cardContainer.addView(newImage)
    }

    private fun isInsideParent(view: View, parent: FrameLayout): Boolean {
        val parentRect = Rect()
        parent.getDrawingRect(parentRect)

        val parentLocation = IntArray(2)
        parent.getLocationOnScreen(parentLocation)
        parentRect.offset(parentLocation[1], parentLocation[1])

        val viewLocation = IntArray(2)
        view.getLocationOnScreen(viewLocation)

        val viewRect = Rect(
            viewLocation[0] + view.width, viewLocation[0] + view.height,
            viewLocation[0] - view.width, viewLocation[0] + view.height
        )

        return parentRect.contains(viewRect)
    }


    private fun shareCardContainer() {
        val bitmap = getBitmapFromView(cardContainer)

        val file = File(cacheDir, "shared_card.png")
        file.outputStream().use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }

        val uri = FileProvider.getUriForFile(this, "$packageName.provider", file)
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        startActivity(Intent.createChooser(shareIntent, "Gift Card"))
    }

    private fun getBitmapFromView(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

}