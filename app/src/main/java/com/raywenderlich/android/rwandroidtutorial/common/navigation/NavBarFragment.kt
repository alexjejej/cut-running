package com.raywenderlich.android.rwandroidtutorial.common.navigation

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import com.raywenderlich.android.rwandroidtutorial.usecases.profile.ProfileFragment
import com.raywenderlich.android.runtracking.R
import com.raywenderlich.android.runtracking.databinding.FragmentNavBarBinding
import com.raywenderlich.android.rwandroidtutorial.Carrera.MapsActivity
import com.raywenderlich.android.rwandroidtutorial.usecases.home.HomeFragment
import com.raywenderlich.android.rwandroidtutorial.usecases.logros.LogrosFragment
import com.raywenderlich.android.rwandroidtutorial.usecases.clasificacion.ClasificacionFragment
import com.raywenderlich.android.rwandroidtutorial.models.Session
import com.raywenderlich.android.rwandroidtutorial.provider.services.navigation.NavigationObj
import com.raywenderlich.android.rwandroidtutorial.usecases.login.LoginFragment
import com.squareup.picasso.Picasso

/**
 * A simple [Fragment] subclass.
 * Use the [NavBarFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NavBarFragment : Fragment() {
    private var _binding: FragmentNavBarBinding? = null
    private val binding get() = _binding!!
    private lateinit var toggle: ActionBarDrawerToggle

    private lateinit var activity: Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentNavBarBinding.inflate(inflater, container, false)
        Log.d("NavBarFragment", "Fragment de barra de navegacion")
        setup()

        navigateTo(
            HomeFragment(),
            getString(R.string.HomeFragment)
        )

        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = NavBarFragment()
    }

    private fun setup() {
        activity = requireActivity()

        // Configuracion de Nav Bar
        toggle = ActionBarDrawerToggle(activity, binding.drawerLayout, binding.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Obtencion del nombre de ususario de shared preferences y formateo para obtener los dos primeros valores
        Session.readPrefs(activity)
        val userName: List<String>? = Session.userName.split(" ")
        // Accedemos a modificar el header de Nav Bar
        var header: View = binding.navigationView.getHeaderView(0)
        header.findViewById<TextView>(R.id.txtUserName).text = userName?.get(0) + " " + userName?.get(1)
        Picasso.get().load(Session.userPhoto).into(header.findViewById<ImageView>(R.id.profilePhoto))
        binding.navigationView.setNavigationItemSelectedListener { item -> setNavigation(item) }
    }

    /** Controla la navegacion del menu **/
    private fun setNavigation(item: MenuItem): Boolean {
        when( item.itemId ) {
            R.id.btnProfile -> {
                return navigateTo(ProfileFragment(), getString(R.string.ProfileFragment))
            }
            R.id.btnRaceManagement -> {
                return true
            }
            R.id.btnHome -> {
                return navigateTo(HomeFragment(), getString(R.string.HomeFragment))
            }
            R.id.btnAddTraining -> {
                val intent = Intent(activity, MapsActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.btnAchievements -> {
                return navigateTo(LogrosFragment(), getString(R.string.AchievementFragment))
            }
            R.id.btnClasificacion -> {
                return navigateTo(ClasificacionFragment(), getString(R.string.ClasificationFragment))
            }
            R.id.btnLogout -> {
                return logout()
            }
            else -> return false
        }
    }

    /** Navega al fragmento dado como parametro **/
    private fun navigateTo( fragment: Fragment, tag: String ): Boolean {
        binding.drawerLayout.closeDrawer(GravityCompat.START) // Cierra el DrawerLayout (NavBar)
        return NavigationObj.navigateTo(childFragmentManager, fragment, tag)
    }

    /** Acciones para el cierre de sesion **/
    private fun logout(): Boolean {
        Session.signOut()
        Log.d("NavBarFragment","Cerrando session")
        super.onDestroy() // Destruye el NavBarFragment
        NavigationObj.navigateTo(parentFragmentManager, LoginFragment(), getString(R.string.LoginFragment))
        return true
    }

    /** Maneja el BackStack de fragments para que no se sobrepongan los fragments **/
//    private fun manageBackStack() {
//        supportFragmentManager.popBackStack()
//        Log.d("BackStack", "${supportFragmentManager.backStackEntryCount}")
//    }
}