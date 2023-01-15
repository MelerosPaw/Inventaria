package meleros.paw.inventory.ui.viewmodel

import android.content.Context
import android.net.Uri
import meleros.paw.inventory.data.PicturesTakenFileProvider
import meleros.paw.inventory.ui.widget.FramedPhotoViewerView

class ImageManager {

  fun getUriFromString(path: CharSequence, context: Context): Uri? = when (getPictureOrigin(path)) {
    FramedPhotoViewerView.Origin.CAMERA -> createValidUri(path, context)
    FramedPhotoViewerView.Origin.FILE_SYSTEM -> Uri.parse(path.toString())
  }

  fun getPictureOrigin(imagePath: CharSequence): FramedPhotoViewerView.Origin =
    if (PicturesTakenFileProvider.isFromCamera(imagePath)) {
      FramedPhotoViewerView.Origin.CAMERA
    } else {
      FramedPhotoViewerView.Origin.FILE_SYSTEM
    }

  fun createValidUri(imagePath: CharSequence, context: Context): Uri? =
    PicturesTakenFileProvider.getUriForPicture(imagePath, context)
}