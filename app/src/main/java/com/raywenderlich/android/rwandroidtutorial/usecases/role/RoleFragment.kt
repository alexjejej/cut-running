package com.raywenderlich.android.rwandroidtutorial.usecases.role

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.raywenderlich.android.runtracking.R
import com.raywenderlich.android.runtracking.databinding.FragmentRoleBinding
import com.raywenderlich.android.rwandroidtutorial.provider.RetrofitInstance
import com.raywenderlich.android.rwandroidtutorial.provider.services.RoleService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * A simple [Fragment] subclass.
 * Use the [RoleFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RoleFragment : Fragment() {

    private var _binding: FragmentRoleBinding? = null
    private val binding get() = _binding!!
    private val TAG: String = RoleFragment.Companion::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRoleBinding.inflate(inflater, container, false)
        Log.d(TAG, "Displaying $TAG")
        _binding!!.addRoleBtn.setOnClickListener {
            try {
                val roleId: Int = _binding!!.roleNameTxt.text.toString().toInt()
                getRoles(roleId)
            }
            catch (e: Exception) {
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            }
        }
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = RoleFragment()
    }

    // Realiza la peticion al endpoint roles con metodo get (definido en RoleService)
    private fun getRoles(roleId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val call = RetrofitInstance.getRetrofit().create(RoleService::class.java).getRoleById(roleId)
            val roles = call.body()
            if (call.isSuccessful) {
                Log.d(TAG, roles?.data.toString())
            }
            else {
                Log.e(TAG, "Error en la llamada")
            }
        }
    }
}