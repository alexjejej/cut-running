package com.cut.android.running.usecases.gestioncarreras.adminraceactions

import RaceManagementViewModel
import android.content.Context
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.cut.android.running.databinding.AdminRaceActionsDialogFragmentBinding
import com.cut.android.running.provider.services.navigation.NavigationObj
import com.cut.android.running.usecases.gestioncarreras.AdminUserByRace
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AdminRaceActionsDialog (
    context: Context,
    binding: AdminRaceActionsDialogFragmentBinding,
    parentFragmentManager: FragmentManager,
    raceManagementViewModel: RaceManagementViewModel,
    lifecycleOwner: LifecycleOwner,
    email: String,
    raceId: Int
) {
    private val _context = context
    private val _binding = binding
    private val _fragmentManager = parentFragmentManager
    private val _viewModel = raceManagementViewModel
    private  val _lifecycleOwner = lifecycleOwner
    private val _email = email
    private val _raceId = raceId

    public fun showDialog() {
        val dialog = MaterialAlertDialogBuilder(_context)
            .setView(_binding.root)
            .create()

        _binding.btnShowUsersByRace.setOnClickListener {
            NavigationObj.navigateTo(_fragmentManager, AdminUserByRace.newInstance(_raceId), "AdminUserByRace")
            dialog.cancel()
        }
        _binding.btnShowRanking.setOnClickListener {
            Toast.makeText(_context, "btnShowRanking.setOnClickListener", Toast.LENGTH_LONG).show()
            dialog.cancel()
        }
        _binding.btnRegister.setOnClickListener {
            _viewModel.verifyRelationship.observeOnce(_lifecycleOwner) {
                if (it != null && it) {
                    dialog.cancel()
                    MaterialAlertDialogBuilder(_context)
                        .setTitle("Registro existente")
                        .setMessage("Usted ya se encuetra registrado en la carrera seleccionada")
                        .setPositiveButton("Cerrar") { dialog, which ->
                            dialog.cancel()
                        }
                        .show()
                }
                else {
                    _viewModel.addUserRelation(_email, _raceId)
                }
            }
            _viewModel.verifyUserRaceRelationship(_email, _raceId)
        }
        dialog.show()
    }

    /**
     * Permite desvilcular el observer del live data despues del primer callback
     */
    fun <T> LiveData<T>.observeOnce(owner: LifecycleOwner, observer: (T) -> Unit) {
        observe(owner, object : Observer<T> {
            override fun onChanged(t: T) {
                observer(t)
                removeObserver(this)
            }
        })
    }
}