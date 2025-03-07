package com.fortune.kotlinappcalculadora.ui.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentValues
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.fortune.kotlinappcalculadora.R
import com.fortune.kotlinappcalculadora.db.AppDatabaseSqlite
import com.fortune.kotlinappcalculadora.ui.adapters.MountainItem

class ModifyMountainDialog(val mountain: MountainItem?, val reloadMountains: () -> Unit) : DialogFragment() {
    private lateinit var conex: AppDatabaseSqlite

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val dialogView = inflater.inflate(R.layout.dialog_modify_mountain, null)

            conex = AppDatabaseSqlite(dialogView.context, "exam-db", null, 1)

            val mountainNameField = dialogView.findViewById<TextView>(R.id.mountain_name)
            mountainNameField.setText(mountain?.name)

            val mountainHeightField = dialogView.findViewById<EditText>(R.id.mountain_height)
            mountainHeightField.setText(mountain?.height.toString())

            builder.setView(dialogView)
                .setPositiveButton("Guardar") { _, _ ->
                    manageMountainUpdate(mountainHeightField.text.toString())
                    reloadMountains()
                }
                .setNegativeButton("Cancelar") {_, _ ->
                    dismiss()
                }

            builder.create()
        } ?: throw IllegalStateException("La actividad no puede ser null")
    }

    private fun manageMountainUpdate(mountainHeightValue: String) {
        val db = conex.writableDatabase

        mountain?.let {
            val values = ContentValues().apply {
                put("id", it.id)
                put("name", it.name)
                put("height", mountainHeightValue.toDouble())
                put("user", it.user)
            }

            val whereClause = "id = ?"
            val whereArgs = arrayOf(it.id)

            db.update("mountain", values, whereClause, whereArgs)
        }
    }
}