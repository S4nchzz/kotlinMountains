package com.fortune.kotlinappcalculadora.ui.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.fortune.kotlinappcalculadora.R
import com.fortune.kotlinappcalculadora.db.AppDatabaseSqlite

class MountainDialog(val inyectMountainData: (String, String, String, String) -> Unit) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val dialogView = inflater.inflate(R.layout.dialog_mountain, null)

            val spinner = dialogView.findViewById<Spinner>(R.id.user_spinner)
            loadSpinnerAdapter(spinner, dialogView)

            builder.setView(dialogView)
                .setPositiveButton("Guardar") { _, id ->
                    val name = dialogView.findViewById<EditText>(R.id.dialog_mountain_name).text.toString()
                    val height = dialogView.findViewById<EditText>(R.id.dialog_mountain_height).text.toString()
                    val userChosen = spinner.selectedItem.toString()
                    val id = "$name$userChosen"

                    inyectMountainData(id, name, height, userChosen)
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
        adapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item)

        spinner.adapter = adapter
    }
}