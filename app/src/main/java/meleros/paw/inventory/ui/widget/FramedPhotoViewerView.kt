package meleros.paw.inventory.ui.widget

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import meleros.paw.inventory.R
import meleros.paw.inventory.data.PicturesTakenFileProvider
import meleros.paw.inventory.databinding.FramedImageViewerViewBinding

class FramedPhotoViewerView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
) : ConstraintLayout(context, attrs) {

  private val binding: FramedImageViewerViewBinding
  var manager: Manager? = null

  init {
    binding = FramedImageViewerViewBinding.inflate(LayoutInflater.from(context), this)
    binding.framedImageViewerLabelTakePicture.setOnClickListener { takePicture() }
    binding.framedImageViewerLabelSelectImage.setOnClickListener { pickImageFromGallery() }

    attrs?.let {
      val typedArray = context.obtainStyledAttributes(it, R.styleable.FramedPhotoViewerView)
      displayControls = typedArray.getBoolean(R.styleable.FramedPhotoViewerView_displayControls, true)
      typedArray.recycle()
    }
  }

  var displayControls: Boolean = true
    set(value) {
      field = value
      binding.framedImageViewerLabelTakePicture.isVisible = value
      binding.framedImageViewerLabelSelectImage.isVisible = value
    }

  fun setImageURI(uri: Uri) {
    binding.framedImageViewerImgItemPicture.setImageURI(uri)
  }

  private fun takePicture() {
    manager?.takePhoto(context)
  }

  private fun pickImageFromGallery() {
    manager?.pickImageFromGallery()
  }

  /**
   * Hay que declarar e inicializar este objeto como campo de la clase que tenga la vista para que funcione el registro
   * del fragment para obtener resultados de activity.
   */
  class Manager(
    fragment: Fragment,
    private val takePersistableUriPermission: Boolean = true,
    val onPhotoObtainedListener: (Origin, Uri) -> Unit,
  ) {

    private var picturesTakenUri: Uri? = null
    private val takePictureLauncher: ActivityResultLauncher<Uri> =
      fragment.registerForActivityResult(ActivityResultContracts.TakePicture()) { isSaved ->
        picturesTakenUri?.takeIf { isSaved }?.let { onPhotoObtainedListener(Origin.CAMERA, it) }
      }
    private val pickPhotoLauncher: ActivityResultLauncher<PickVisualMediaRequest> =
      fragment.registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { destinationUri ->
        destinationUri?.let {
          // TODO Melero 9/1/23: Guardar esto en el manual de Android Pure
          if (takePersistableUriPermission) {
            fragment.context?.contentResolver?.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
          }
          onPhotoObtainedListener(Origin.FILE_SYSTEM, it)
        }
      }

    fun takePhoto(context: Context) {
      picturesTakenUri = PicturesTakenFileProvider.getUriForNewPicture(context)
      takePictureLauncher.launch(picturesTakenUri)
    }

    fun pickImageFromGallery() {
      val mediaType = PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
      pickPhotoLauncher.launch(mediaType)
    }
  }

  enum class Origin {
    CAMERA,
    FILE_SYSTEM,
  }
}