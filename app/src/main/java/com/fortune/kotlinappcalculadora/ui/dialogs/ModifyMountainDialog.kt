package com.fortune.kotlinappcalculadora.ui.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentValues
import android.os.Bundle
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.fortune.kotlinappcalculadora.R
import com.fortune.kotlinappcalculadora.db.AppDatabaseSqlite
import com.fortune.kotlinappcalculadora.ui.adapters.MountainItem

class ModifyMountainDialog(val mountain: MountainItem?, val userType: Char) : DialogFragment() {
    private lateinit var conex: AppDatabaseSqlite

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val dialogView = inflater.inflate(R.layout.dialog_modify_mountain, null)

            conex = AppDatabaseSqlite(dialogView.context, "exam-db", null, 1)

            val mountainField = dialogView.findViewById<EditText>(R.id.mountain_name)
            mountainField.setText(mountain?.name)

            // Ennable button for admins
            if (userType == 'A') {
                mountainField.isEnabled = true
            }

            builder.setView(dialogView)
                .setPositiveButton("Guardar") { _, _ ->
                    manageMountainUpdate(mountainField.text.toString())
                }
                .setNegativeButton("Cancelar") {_, _ ->
                    dismiss()
                }

            builder.create()
        } ?: throw IllegalStateException("La actividad no puede ser null")
    }

    private fun manageMountainUpdate(fieldUpdatedText: String) {
        val db = conex.writableDatabase

        mountain?.let {
            val values = ContentValues().apply {
                put("id", "$fieldUpdatedText${it.user}")
                put("name", fieldUpdatedText)
                put("height", it.height)
                put("user", it.user)
            }

            val whereClause = "id = ?"
            val whereArgs = arrayOf(it.id)

            db.update("mountain", values, whereClause, whereArgs)
        }
    }
}