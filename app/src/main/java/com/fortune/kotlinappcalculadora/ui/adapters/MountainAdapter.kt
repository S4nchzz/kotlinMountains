package com.fortune.kotlinappcalculadora.ui.adapters

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import com.fortune.kotlinappcalculadora.R
import com.fortune.kotlinappcalculadora.db.AppDatabaseSqlite
import com.fortune.kotlinappcalculadora.ui.dialogs.ModifyMountainDialog

class MountainAdapter(context: Context, list_item_res: Int, items: List<MountainItem>, private val fragmentManager: androidx.fragment.app.FragmentManager, val userType: Char, val reloadMountains: () -> Unit) : ArrayAdapter<MountainItem>(context, list_item_res, items) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_item_mountain, parent, false)

        val item = getItem(position)

        val v_image = view.findViewById<ImageView>(R.id.image_mountain)
        val v_name = view.findViewById<TextView>(R.id.name_height)

        item?.let {
            v_image.setImageDrawable(it.image)
            v_name.text = "${it.name} - ${it.height}"
        }

        view.setOnClickListener {
            val popup = PopupMenu(it.context, it)
            val inflater: MenuInflater = popup.menuInflater
            inflater.inflate(R.menu.item_mountain_menu, popup.menu)

            val conex = AppDatabaseSqlite(view.context, "exam-db", null, 1)
            popup.setOnMenuItemClickListener { menuItem ->
                when(menuItem.itemId) {
                    R.id.modificar_montana -> {
                        ModifyMountainDialog(item, userType).show(this.fragmentManager, "modifyMountainDialog")
                        true
                    }

                    R.id.eliminar_montana -> {
                        AlertDialog.Builder(view.context)
                            .setTitle("¿Estas seguro de que quieres eliminar la montaña?")
                            .setPositiveButton("Si") { dialog, id ->
                                val db = conex.writableDatabase
                                val whereClause = "id = ?"
                                val whereArgs = arrayOf(item?.id)

                                db.delete("mountain", whereClause, whereArgs)
                                popup.dismiss()

                                this@MountainAdapter.reloadMountains()
                            }
                            .setNegativeButton("No") { _, _ ->
                                popup.dismiss()
                                return@setNegativeButton
                            }
                            .show()
                        true
                    }

                    else -> false
                }
            }

            popup.show()
        }

        return view
    }
}