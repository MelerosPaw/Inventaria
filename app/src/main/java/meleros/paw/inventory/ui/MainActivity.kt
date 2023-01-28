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
import meleros.paw.inventory.extension.hide
import meleros.paw.inventory.extension.show
import meleros.paw.inventory.ui.viewmodel.BaseViewModel

class MainActivity : AppCompatActivity(), SelectionModeListener, TitleHolder, OverallLoader {

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

  private fun startLoading(message: CharSequence? = null) {
    binding.loaderOverall.apply {
      text = message
      show()
    }
  }

  private fun stopLoading() {
    binding.loaderOverall.apply {
      hide()
      text = null
    }
  }

  //region SelectionModeListener
  override fun onSelectionModeChanged(@StringRes toolbarTitle: Int, @ColorInt toolbarColor: Int) {
    binding.toolbar.run {
      setTitle(toolbarTitle)
      setBackgroundColor(toolbarColor)
    }
  }
  //endregion

  //region TitleHolder
  override fun setTitleInToolbar(title: CharSequence) {
    binding.toolbar.run {
      setTitle(title)
    }
  }
  //endregion

  //region OverallLoader
  override fun updateState(state: BaseViewModel.LoadingState) {
    when (state) {
      is BaseViewModel.LoadingState.Loading -> startLoading(state.message)
      is BaseViewModel.LoadingState.NotLoading -> stopLoading()
    }
  }
  //endregion
}