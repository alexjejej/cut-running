
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import com.cut.android.running.databinding.RaceInfoDialogFragmentBinding
import com.cut.android.running.models.Race
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class RaceInfoDialog (
    context: Context,
    binding: RaceInfoDialogFragmentBinding,
    raceManagementViewModel: RaceManagementViewModel
){
    private val _context = context
    private val _binding = binding
    private val _raceManagementViewModel = raceManagementViewModel

    /**
     * Build Mat Dialog
     */
    public fun showDialog(race: Race) {
        setViewValues(race)

        val builder = MaterialAlertDialogBuilder(_context)
            .setView(_binding.root)
            .setPositiveButton("Resgistrar") { dialog, which ->
                _raceManagementViewModel.addUserRelation(999, 999)
            }
            .setNegativeButton("Cancelar") { dialog, which ->
                dialog.cancel()
            }
            .show()
    }

    /**
     * Set view values
     */
    private fun setViewValues (race: Race) {
        _binding.lblRaceName.setText(race.name)
        _binding.lblRaceDate.setText(race.date)
        _binding.lblRaceSede.setText(race.UC.name)
        _binding.lblRaceDescription.setText(race.description)
    }
}