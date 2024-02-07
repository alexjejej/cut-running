package com.cut.android.running.utils.dialog

import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.cut.android.running.R
import com.cut.android.running.provider.services.context.ContextProvider
import com.cut.android.running.provider.resources.DrawableResourcesProvider
import com.cut.android.running.provider.resources.StringResourcesProvider
import javax.inject.Inject

class Dialog @Inject constructor(
    private val _stringResourcesProvider: StringResourcesProvider,
    private val _drawableResourcesProvider: DrawableResourcesProvider,
    private val _contextProvider: ContextProvider
){

    /** Incorpora los elementos para un dialog de advertencia **/
    fun warningDialog(message: Int, title: Int) {
        MaterialAlertDialogBuilder(_contextProvider.getContext())
            .setTitle(_stringResourcesProvider.getString(title))
            .setMessage(_stringResourcesProvider.getString(message))
            .setIcon(_drawableResourcesProvider.getDrawable(R.drawable.icon_warning_24))
            .setNeutralButton(_stringResourcesProvider.getString(R.string.default_dialog_button))
            { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }

    /** Incorpora los elementos para un dialog de informacion **/
    fun infoDialog(message: Int, title: Int) {
        MaterialAlertDialogBuilder(_contextProvider.getContext())
            .setTitle(_stringResourcesProvider.getString(title))
            .setMessage(_stringResourcesProvider.getString(message))
            // .setIcon(_drawableResourcesProvider.getDrawable(R.drawable.icon_info_24)) TODO: Definir icono apropiado
            .setNeutralButton(_stringResourcesProvider.getString(R.string.default_dialog_button))
            { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }

    /** Incorpora los elementos para un dialog de error **/
    fun dangerDialog(message: Int, title: Int) {
        MaterialAlertDialogBuilder(_contextProvider.getContext())
            .setTitle(_stringResourcesProvider.getString(title))
            .setMessage(_stringResourcesProvider.getString(message))
            .setIcon(_drawableResourcesProvider.getDrawable(R.drawable.icon_info_24))
            .setNeutralButton(_stringResourcesProvider.getString(R.string.default_dialog_button))
            { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }

    /** Incorpora los elementos para un dialog de proceso exitoso **/
    fun successDialog() {

    }
}