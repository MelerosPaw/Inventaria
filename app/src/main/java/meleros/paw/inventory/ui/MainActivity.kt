package meleros.paw.inventory.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.window.OnBackInvokedDispatcher
import androidx.activity.viewModels
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.MenuProvider
import meleros.paw.inventory.R
import meleros.paw.inventory.databinding.ActivityMainBinding
import meleros.paw.inventory.ui.viewmodel.ItemsViewModel

class MainActivity : AppCompatActivity(), ItemListFragment.SelectionModeResponsive {

  private lateinit var appBarConfiguration: AppBarConfiguration
  private lateinit var binding: ActivityMainBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)

    setSupportActionBar(binding.toolbar)

    val navController = findNavController(R.id.nav_host_fragment_content_main)
    appBarConfiguration = AppBarConfiguration(navController.graph)
    setupActionBarWithNavController(navController, appBarConfiguration)
  }

  override fun onSupportNavigateUp(): Boolean {
    val navController = findNavController(R.id.nav_host_fragment_content_main)
    return navController.navigateUp(appBarConfiguration)
        || super.onSupportNavigateUp()
  }

  override fun onSelectionModeChanged(@StringRes toolbarTitle: Int, @ColorInt toolbarColor: Int) {
    binding.toolbar.run {
      setTitle(toolbarTitle)
      setBackgroundColor(toolbarColor)
    }
  }
}