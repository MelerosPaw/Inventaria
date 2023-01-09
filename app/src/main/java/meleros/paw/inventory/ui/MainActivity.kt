package meleros.paw.inventory.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import meleros.paw.inventory.R
import meleros.paw.inventory.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), SelectionModeListener, TitleHolder {

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

  override fun setTitleInToolbar(title: CharSequence) {
    binding.toolbar.run {
      setTitle(title)
    }
  }
}