package com.example.myreserveapp

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private lateinit var mainLayout: ConstraintLayout

    // 用來處理選取圖片後的結果
    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                // 1. 設定背景圖片
                val drawable = try {
                    val inputStream = contentResolver.openInputStream(it)
                    Drawable.createFromStream(
                        inputStream,
                        it.toString()
                    )
                } catch (e: Exception) {
                    null
                }
                mainLayout.background = drawable

                // 2. 儲存設定 (儲存圖片 URI 字串)
                saveBackgroundPreference("IMAGE", it.toString())
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        mainLayout = findViewById(R.id.main)




        // 讀取並應用上次儲存的背景
        loadSavedBackground()

        val buttonStart = findViewById<Button>(R.id.buttonStart)
        buttonStart.setOnClickListener {
            val intent = Intent(this, MainActivity2::class.java)
            startActivity(intent)
        }

        // 設定更換背景按鈕
        val btnChangeBackground = findViewById<Button>(R.id.btnChangeBackground)
        btnChangeBackground.setOnClickListener {
            showChangeBackgroundDialog()
        }
    }

    private fun showChangeBackgroundDialog() {
        val options = arrayOf(
            "更換顏色 (紅)",
            "更換顏色 (藍)",
            "更換顏色 (綠)",
            "從相簿選擇圖片",
            "恢復預設"
        )

        AlertDialog.Builder(this)
            .setTitle("選擇背景樣式")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> changeBackgroundColor(Color.RED, "RED")
                    1 -> changeBackgroundColor(Color.BLUE, "BLUE")
                    2 -> changeBackgroundColor(Color.GREEN, "GREEN")
                    3 -> pickImageLauncher.launch("image/*") // 開啟相簿
                    4 -> changeBackgroundColor(Color.WHITE, "WHITE") // 恢復預設
                }
            }
            .show()
    }

    private fun changeBackgroundColor(color: Int, colorName: String) {
        mainLayout.setBackgroundColor(color)
        saveBackgroundPreference("COLOR", colorName)
    }

    // 儲存偏好設定 (使用 SharedPreferences)
    private fun saveBackgroundPreference(type: String, value: String) {
        val sharedPref = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("BG_TYPE", type)   // 類型: COLOR 或 IMAGE
            putString("BG_VALUE", value) // 值: 顏色名稱 或 圖片URI
            apply()
        }
    }

    // 讀取偏好設定
    private fun loadSavedBackground() {
        val sharedPref = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        val type = sharedPref.getString("BG_TYPE", "COLOR")
        val value = sharedPref.getString("BG_VALUE", "WHITE")

        if (type == "COLOR") {
            val color = when (value) {
                "RED" -> Color.RED
                "BLUE" -> Color.BLUE
                "GREEN" -> Color.GREEN
                else -> Color.WHITE
            }
            mainLayout.setBackgroundColor(color)
        } else if (type == "IMAGE") {
            try {
                val uri = Uri.parse(value)
                // 注意：長期儲存圖片 URI 需要 Persistable Permission，這裡做簡單示範
                // 如果圖片被刪除或權限失效，可能會讀取失敗，這裡加個 try-catch
                val inputStream = contentResolver.openInputStream(uri)
                val drawable = Drawable.createFromStream(
                    inputStream,
                    uri.toString()
                )
                mainLayout.background = drawable
            } catch (e: Exception) {
                mainLayout.setBackgroundColor(Color.WHITE) // 讀取失敗就回預設值
            }
        }
    }
}