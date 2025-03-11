package com.fortune.kotlinappcalculadora.ui.view

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.fortune.kotlinappcalculadora.R
import com.fortune.kotlinappcalculadora.db.AppDatabaseSqlite
import com.fortune.kotlinappcalculadora.ui.adapters.MountainAdapter
import com.fortune.kotlinappcalculadora.ui.adapters.MountainItem
import com.fortune.kotlinappcalculadora.ui.enums.MountainOperation
import com.fortune.kotlinappcalculadora.ui.dialogs.MountainDialog

class Mountain : AppCompatActivity() {
    private lateinit var conex: AppDatabaseSqlite

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_mountain)
        adjustScreenInsets()

        conex = AppDatabaseSqlite(this, "exam-db", null, 1)
        manageUserData()
        loadListViewAdapter()

        setConqueredMountains()
    }

    private fun setConqueredMountains() {
        val db = conex.readableDatabase

        var sql: String? = null
        var searchParams: Array<String>? = null

        if (intent.getStringExtra("type").toString()[0] == 'A') {
            sql = "SELECT * FROM mountain"
        } else {
            sql = "SELECT * FROM mountain WHERE user = ?"
            searchParams = arrayOf(intent.getStringExtra("username") ?: "")
        }

        val query = db.rawQuery(
            sql,
            searchParams
        )

        val count = query.count
        db.close()

        findViewById<TextView>(R.id.n_cimas_conquistadas).text = "Nº de cimas conquistadas $count"
    }

    private fun loadListViewAdapter() {
        val listview = findViewById<ListView>(R.id.mountain_listview)
        listview.adapter = MountainAdapter(
            this,
            R.layout.list_item_mountain, getMountainItems(), supportFragmentManager,
            intent.getStringExtra("type")?.get(0) ?: '?'
        ) {
            reloadMountains()
        }
    }

    private fun reloadMountains() {
        loadListViewAdapter()
        setConqueredMountains()
    }

    private fun getMountainItems(): List<MountainItem> {
        val db = conex.readableDatabase
        val cols = arrayOf(
            "id",
            "name",
            "height",
            "user"
        )

        var selection: String? = null
        var selArgs: Array<String>? = null

        if (intent.getStringExtra("type")?.get(0) != 'A') {
            selection = "user = ?"
            selArgs = arrayOf(intent.getStringExtra("username") ?: "")
        }

        val cursor = db.query(
            "mountain",
            cols,
            selection,
            selArgs,
            null,
            null,
            null,
        )

        val mountainItemList: MutableList<MountainItem> = mutableListOf()

        while (cursor.moveToNext()) {
            val id = cursor.getString(cursor.getColumnIndexOrThrow("id"))
            val mName = cursor.getString(cursor.getColumnIndexOrThrow("name"))
            val mHeight = cursor.getDouble(cursor.getColumnIndexOrThrow("height"))
            val user = cursor.getString(cursor.getColumnIndexOrThrow("user"))

            var image: Int = android.R.drawable.ic_menu_mapmode
            val imageDraweable = findMountainImageDrawable(mName)

            mountainItemList.add(
                MountainItem(
                    id,
                    mName,
                    mHeight,
                    user,
                    imageDraweable
                )
            )
        }

        cursor.close()
        db.close()

        return mountainItemList
    }

    private fun findMountainImageDrawable(mName: String?): Drawable? {
        return when (mName?.toLowerCase()) {
            "alcazaba" -> ContextCompat.getDrawable(this, R.drawable.alcazaba_image)
            "almanzor" -> ContextCompat.getDrawable(this, R.drawable.almanzor_image)
            "everest" -> ContextCompat.getDrawable(this, R.drawable.everest_image)
            "fuji" -> ContextCompat.getDrawable(this, R.drawable.fuji_image)
            "maladeta" -> ContextCompat.getDrawable(this, R.drawable.maladeta_image)
            "mulhacen" -> ContextCompat.getDrawable(this, R.drawable.mulhacen_image)
            "posets" -> ContextCompat.getDrawable(this, R.drawable.posets_image)
            "prieta" -> ContextCompat.getDrawable(this, R.drawable.prieta_image)
            "teide" -> ContextCompat.getDrawable(this, R.drawable.teide_image)
            "veleta" -> ContextCompat.getDrawable(this, R.drawable.veleta_image)
            else -> ContextCompat.getDrawable(this, android.R.drawable.ic_menu_mapmode)
        }
    }

    private fun manageUserData() {
        val add_mountain = findViewById<Button>(R.id.add_mountain);
        val user_type: Char = intent.getStringExtra("type").toString()[0];

        if (user_type == 'A') {
            add_mountain.visibility = View.VISIBLE

            add_mountain.setOnClickListener {
                MountainDialog(MountainOperation.CREATE, null, {
                    reloadMountains()
                }).show(supportFragmentManager, "Create mountain")
            }
        }
    }

    private fun adjustScreenInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}