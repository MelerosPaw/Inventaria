package meleros.paw.inventory.manager

import android.content.Context
import android.net.Uri
import meleros.paw.inventory.data.PicturesTakenFileProvider
import meleros.paw.inventory.extension.orNot
import meleros.paw.inventory.ui.widget.FramedPhotoViewerView

object ImageManager {

  fun getUriFromString(path: CharSequence, context: Context): Uri? = when (getPictureOrigin(path)) {
    FramedPhotoViewerView.Origin.CAMERA -> createValidUri(path, context)
    FramedPhotoViewerView.Origin.FILE_SYSTEM -> Uri.parse(path.toString())
  }

  private fun getPictureOrigin(imagePath: CharSequence): FramedPhotoViewerView.Origin =
    if (PicturesTakenFileProvider.isFromCamera(imagePath)) {
      FramedPhotoViewerView.Origin.CAMERA
    } else {
      FramedPhotoViewerView.Origin.FILE_SYSTEM
    }

  private fun createValidUri(imagePath: CharSequence, context: Context): Uri? =
    PicturesTakenFileProvider.getUriForPicture(imagePath, context)

  /**
   * @return Returns `true` if the picture was taken by the camera and could be deleted or if it wasn't taken by the app,
   * in which case we mustn't delete and consider everything is OK.
   */
  fun deletePicture(path: CharSequence, context: Context): Boolean =
    !PicturesTakenFileProvider.isFromCamera(path)
        || PicturesTakenFileProvider.getUriForPicture(path, context)?.let { uri ->
      context.contentResolver.delete(uri, null, null) == 1
    }.orNot()
}