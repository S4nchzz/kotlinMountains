package com.fortune.kotlinappcalculadora.ui.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.fortune.kotlinappcalculadora.R
import com.fortune.kotlinappcalculadora.db.AppDatabaseSqlite
import com.fortune.kotlinappcalculadora.ui.adapters.MountainItem
import com.fortune.kotlinappcalculadora.ui.enums.MountainOperation

class MountainDialog(val mountainOperation: MountainOperation, val mountainItem: MountainItem?, val reloadMountains: () -> Unit) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val dialogView = inflater.inflate(R.layout.dialog_mountain, null)

            val spinner = dialogView.findViewById<Spinner>(R.id.user_spinner)
            loadSpinnerAdapter(spinner, dialogView)

            val nameField = dialogView.findViewById<EditText>(R.id.dialog_mountain_name)
            val heightField = dialogView.findViewById<EditText>(R.id.dialog_mountain_height)

            when(mountainOperation) {
                MountainOperation.CREATE -> {
                    dialogView.findViewById<TextView>(R.id.mountain_dialog_title).setText("Crear montaña")
                }

                MountainOperation.MODIFY -> {
                    dialogView.findViewById<TextView>(R.id.mountain_dialog_title).setText("Modificar montaña")

                    spinner.isEnabled = false
                    nameField.isEnabled = false

                    heightField.setText(mountainItem?.height.toString())

                    nameField.setText(mountainItem?.name)
                    val position = (spinner.adapter as ArrayAdapter<String>).getPosition(mountainItem?.user)
                    spinner.setSelection(position)
                }
            }

            builder.setView(dialogView)
                .setPositiveButton("Guardar") { _, id ->
                    val name = nameField.text.toString()
                    val height = heightField.text.toString()
                    val userChosen = spinner.selectedItem.toString()
                    val id = "$name$userChosen"

                    when (this.mountainOperation) {
                        MountainOperation.CREATE -> {
                            createMountain(dialogView.context, id, name, height, userChosen)
                            this.reloadMountains()
                        }
                        MountainOperation.MODIFY -> {
                            mountainItem?.let {
                                modifyMountain(dialogView.context, mountainItem.id, height)
                                this.reloadMountains()
                            }
                        }
                    }
                }
                .setNeutralButton("Cancelar") { _, _ ->
                    getDialog()?.create()
                }

            builder.create()
        } ?: throw IllegalStateException("La actividad no puede ser null")
    }

    private fun loadSpinnerAdapter(spinner: Spinner, dialogView: View) {
        val nameList: MutableList<String> = mutableListOf()
        val conex = AppDatabaseSqlite(dialogView.context, "exam-db", null, 1)
        val db = conex.readableDatabase

        val cursor = db.rawQuery(
            "SELECT * FROM user WHERE username != ?",
            arrayOf("admin")
        )

        while (cursor.moveToNext()) {
            nameList.add(cursor.getString(cursor.getColumnIndexOrThrow("username")))
        }

        val adapter = ArrayAdapter(dialogView.context, android.R.layout.select_dialog_item, nameList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinner.adapter = adapter
    }

    private fun createMountain(
        dialogContext: Context,
        id: String,
        name: String,
        height: String,
        userChosen: String
    ) {
        val conex = AppDatabaseSqlite(dialogContext, "exam-db", null, 1)
        val db = conex.writableDatabase

        val values = ContentValues().apply {
            put("id", id)
            put("name", name)
            put("height", height)
            put("user", userChosen)
        }

        db.insert("mountain", null, values)
        db.close()
        conex.close()
    }

    private fun modifyMountain(dialogContext: Context, id: String, height: String) {
        val conex = AppDatabaseSqlite(dialogContext, "exam-db", null, 1)
        val db = conex.writableDatabase

        val values = ContentValues().apply {
            put("height", height)
        }

        val whereClause = "id = ?"
        val whereArgs = arrayOf(id)

        db.update("mountain", values, whereClause, whereArgs)
        conex.close()
    }
}