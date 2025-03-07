package com.fortune.kotlinappcalculadora.ui.view

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
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

        loadNMountainsConquered()
    }

    private fun loadNMountainsConquered() {
        val db = conex.readableDatabase

        val query = db.rawQuery(
            "SELECT * FROM mountain WHERE user = ?",
            arrayOf(intent.getStringExtra("username"))
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
            loadListViewAdapter()
            loadNMountainsConquered()
        }
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
        val staticImage = ContextCompat.getDrawable(this, R.drawable.calculadora)

        while (cursor.moveToNext()) {
            val id = cursor.getString(cursor.getColumnIndexOrThrow("id"))
            val mName = cursor.getString(cursor.getColumnIndexOrThrow("name"))
            val mHeight = cursor.getDouble(cursor.getColumnIndexOrThrow("height"))
            val user = cursor.getString(cursor.getColumnIndexOrThrow("user"))

            mountainItemList.add(MountainItem(
                id,
                mName,
                mHeight,
                user,
                staticImage
            ))
        }

        cursor.close()
        db.close()

        return mountainItemList
    }

    private fun manageUserData() {
        val add_mountain = findViewById<Button>(R.id.add_mountain);
        val user_type: Char = intent.getStringExtra("type").toString()[0];

        if (user_type == 'A') {
            add_mountain.visibility = View.VISIBLE

            add_mountain.setOnClickListener {
                MountainDialog({ id, name, height, username ->
                    injectMountain(id, name, height, username)
                }).show(supportFragmentManager, "mountain_dialog")
            }
        }
    }

    private fun injectMountain(id: String, name: String, height: String, username: String) {
        val conex = AppDatabaseSqlite(this, "exam-db", null, 1)
        val db = conex.writableDatabase

        val values = ContentValues().apply {
            put("id", id)
            put("name", name)
            put("height", height)
            put("user", username)
        }

        db.insert("mountain", null, values)
        db.close()
        conex.close()
    }

    private fun adjustScreenInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}