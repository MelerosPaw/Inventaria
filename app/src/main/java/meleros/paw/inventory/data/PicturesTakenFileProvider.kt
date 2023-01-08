package meleros.paw.inventory.data

import android.R.attr.data
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.content.FileProvider
import meleros.paw.inventory.BuildConfig
import meleros.paw.inventory.R
import java.io.File
import java.io.FileInputStream


class PicturesTakenFileProvider: FileProvider(R.xml.provider_paths) {

  companion object {
    // Must be the same as in the manifest's <provider>.
    private const val authority: String = "${BuildConfig.APPLICATION_ID}.fileprovider"
    // Must be the same path specified in provider_paths.xml. So whats the point on specifying the name?
    private const val picturesDir: String = "pictures_taken"
    // Must be the same name specified in provider_paths.xml. What's the point then on specifying it if you cannot access to it.
    private const val picturesDirId: String = "/pictures"
    // Just the content:// scheme
    private const val scheme: String = "content"

    fun getUriForNewPicture(context: Context): Uri {
      // Creates the folder if it doesn't exist and the file as empty to overwrite it with the picture once taken
      val photosDir = File(context.filesDir, picturesDir).also { it.takeIf { !it.exists() }?.mkdir() }
      val photoFile = File(photosDir, getNameForNewPicture()).also { it.createNewFile() }
      return getUriForFile(context, authority, photoFile)
    }

    fun getUriForPicture(path: CharSequence, context: Context): Uri? {
      val stringPath = path.toString()

      val uri = Uri.Builder()
          .scheme(scheme)
          .authority(authority)
          .path(stringPath)
          .build()
          .takeIf { it.exists(context) }

      return uri
    }

    fun getBitmapFromUri(context: Context, imagePath: CharSequence) {
//      val fileByteArray = ByteArray(uri.toString().length)
//
//      try {
//        val fileInputStream: FileInputStream = context.contentResolver
//          .openInputStream(uri) as FileInputStream
//        fileInputStream.read(fileByteArray)
//      } catch (e: java.lang.Exception) {
//        e.printStackTrace()
//      }
//      Uri.parse(Uri.decode(imagePath.toString()))
    }

    fun isFromCamera(path: CharSequence) = path.startsWith(picturesDirId)

    /** If any exception arises while trying to query the ContentResolver with the specified Uri,
     * the content cannot be retrieved, so it will be considered invalid.
     *
     * @param context A context to obtain the content resolver.
     *
     * @return Returns true only if the uri points to a valid file.
     */
    private fun Uri.exists(context: Context): Boolean {
      val cursor: Cursor? = try {
        context.contentResolver.query(this, null, null, null, null)
      } catch (exception: Exception) {
        null
      }

      val exists = cursor != null
      cursor?.close()
      return exists
    }

    private fun getNameForNewPicture() : String = "picture_${System.currentTimeMillis()}.jpg"
  }
}