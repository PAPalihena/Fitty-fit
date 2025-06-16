package com.example.fittyfit.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.util.Log
import android.view.MotionEvent
import com.example.fittyfit.R
import com.google.android.material.card.MaterialCardView

class NotificationPanelView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var overlay: View? = null
    private var panelCard: MaterialCardView? = null
    private var closeButton: ImageButton? = null
    private var isShowing = false

    companion object {
        private var globalIsShowing = false
        private const val TAG = "NotificationPanelView"
    }

    init {
        try {
            LayoutInflater.from(context).inflate(R.layout.notification_panel, this, true)
            
            overlay = findViewById(R.id.overlay)
            panelCard = findViewById(R.id.panelCard)
            closeButton = findViewById(R.id.closeButton)
            
            Log.d(TAG, "Initializing NotificationPanelView")
            Log.d(TAG, "Close button found: ${closeButton != null}")
            Log.d(TAG, "Panel card found: ${panelCard != null}")
            
            // Set initial state
            visibility = View.GONE
            isShowing = false
            globalIsShowing = false
            overlay?.alpha = 0f
            panelCard?.translationX = width.toFloat()
            
            // Set click listeners
            overlay?.setOnClickListener { 
                Log.d(TAG, "Overlay clicked")
                hide()
            }
            
            // Set up close button click listener
            closeButton?.let { button ->
                Log.d(TAG, "Setting up close button click listener")
                button.setOnClickListener { 
                    Log.d(TAG, "Close button clicked")
                    hide()
                }
                button.isClickable = true
                button.isFocusable = true
            }

            // Set touch listener for the entire view
            setOnTouchListener { _, event ->
                if (isShowing && event.action == MotionEvent.ACTION_DOWN) {
                    val x = event.x
                    val panelX = panelCard?.x ?: 0f
                    val panelWidth = panelCard?.width ?: 0
                    
                    // If touch is outside the panel, hide it
                    if (x < panelX || x > panelX + panelWidth) {
                        Log.d(TAG, "Touch outside panel detected")
                        hide()
                        return@setOnTouchListener true
                    }
                }
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in init: ${e.message}")
            e.printStackTrace()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        Log.d(TAG, "onAttachedToWindow called")
        // Reset state when attached
        visibility = View.GONE
        isShowing = false
        globalIsShowing = false
        overlay?.alpha = 0f
        panelCard?.translationX = width.toFloat()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (!isShowing) {
            panelCard?.translationX = w.toFloat()
        }
    }

    fun show() {
        if (isShowing) return
        
        try {
            Log.d(TAG, "Showing panel")
            visibility = View.VISIBLE
            isShowing = true
            globalIsShowing = true
            
            // Animate overlay
            overlay?.alpha = 0f
            overlay?.animate()
                ?.alpha(1f)
                ?.setDuration(200)
                ?.start()
            
            // Animate panel
            panelCard?.translationX = width.toFloat()
            panelCard?.animate()
                ?.translationX(0f)
                ?.setDuration(300)
                ?.start()
        } catch (e: Exception) {
            Log.e(TAG, "Error in show: ${e.message}")
            e.printStackTrace()
        }
    }

    fun hide() {
        if (!isShowing) return
        
        try {
            Log.d(TAG, "Hiding panel")
            isShowing = false
            globalIsShowing = false
            
            // Animate overlay
            overlay?.animate()
                ?.alpha(0f)
                ?.setDuration(200)
                ?.start()
            
            // Animate panel
            panelCard?.animate()
                ?.translationX(width.toFloat())
                ?.setDuration(300)
                ?.withEndAction {
                    visibility = View.GONE
                }
                ?.start()
        } catch (e: Exception) {
            Log.e(TAG, "Error in hide: ${e.message}")
            e.printStackTrace()
        }
    }

    fun isPanelShowing(): Boolean = isShowing
} 