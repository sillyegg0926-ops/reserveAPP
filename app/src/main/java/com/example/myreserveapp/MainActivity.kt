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
        // 1. 從 strings.xml 讀取顏色顯示名稱
        val colorDisplayNames = resources.getStringArray(R.array.background_color_display_names)

        // 2. 建立包含「選擇圖片」的完整選項列表
        val options = colorDisplayNames + "從相簿選擇圖片" // 將圖片選項加到最後

        AlertDialog.Builder(this)
            .setTitle("選擇背景樣式")
            .setItems(options) { _, which ->
                // 3. 判斷使用者的選擇
                if (which < colorDisplayNames.size) {
                    // --- 使用者選擇了顏色 ---

                    // 取得對應的顏色資源名稱 (例如 "bg_color_red")
                    val colorResourceName = resources.getStringArray(R.array.background_color_resource_names)[which]

                    // 根據資源名稱取得顏色 ID，再取得顏色值
                    val colorResId = resources.getIdentifier(colorResourceName, "color", packageName)
                    val colorValue = resources.getColor(colorResId, null)

                    // 呼叫函式來更換背景並儲存設定
                    changeBackgroundColor(colorValue, colorResourceName)

                } else {
                    // --- 使用者選擇了「從相簿選擇圖片」 ---
                    pickImageLauncher.launch("image/*")
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
        val value = sharedPref.getString("BG_VALUE", "bg_color_default")

        if (type == "COLOR") {
            // ★ 修改這裡的邏輯 ★
            try {
                // 根據儲存的資源名稱 (value) 找到顏色 ID
                val colorResId = resources.getIdentifier(value, "color", packageName)
                // 從 ID 取得顏色值
                val color = resources.getColor(colorResId, null)
                mainLayout.setBackgroundColor(color)
            } catch (e: Exception) {
                // 如果找不到資源 (例如您刪除了 colors.xml 中的某個顏色)，就恢復預設
                mainLayout.setBackgroundColor(Color.WHITE)
            }
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