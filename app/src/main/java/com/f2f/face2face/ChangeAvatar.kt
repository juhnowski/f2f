package com.f2f.face2face

import android.app.ActionBar
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.annotation.RequiresApi
import android.support.design.widget.Snackbar
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity;
import android.util.Base64
import android.util.Log
import android.view.*
import android.widget.*
import com.f2f.face2face.json.*

import kotlinx.android.synthetic.main.activity_change_avatar.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class ChangeAvatar : AppCompatActivity(), ResponseProcessable {

    val TAG = "ChangeAvatar"
    val REQUEST_IMAGE_CAPTURE = 1
    val REQUEST_IMAGE_GALLERY = 2
    val REQUEST_TAKE_PHOTO = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_avatar)
        Log.d(TAG,"onCreate")
        FullScreencall()

        val imageView = findViewById<ImageView>(R.id.imageView_avatar)
        val base64ava = getAvatar(this@ChangeAvatar)
        if (base64ava.length>4) {
            Log.d(TAG,"base64ava.length=${base64ava.length}")
            val imageAsBytes = Base64.decode(base64ava, 0)
            imageView.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.size))
        } else {
            Log.d(TAG,"load from resource")
            imageView.setImageResource(R.mipmap.face);
        }
        val clickListener = View.OnClickListener { view ->
            when (view.id) {
                R.id.imageView_avatar -> {
                    //showPopup(view)
                }
            }
        }

        imageView_avatar.setOnClickListener(clickListener)

        val pb = findViewById<ProgressBar>(R.id.pb_change_avatar)
        pb.isIndeterminate = true
        pb.visibility= View.INVISIBLE
    }

    fun change(V:View?){
        dispatchTakePictureIntent();
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_change_avatar, menu)
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.xml.left_to_right, R.xml.right_to_left)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            android.R.id.home -> {
                super.onBackPressed()
                overridePendingTransition(R.xml.left_to_right, R.xml.right_to_left)
                return true
            }
            R.id.save_new_avatar -> {
                val pb = findViewById<ProgressBar>(R.id.pb_change_avatar)
                pb.visibility= View.VISIBLE
                val iv1 = findViewById<ImageView>(R.id.imageView_avatar)
                iv1.buildDrawingCache()
                val bmp = iv1.getDrawingCache()

                val stream = ByteArrayOutputStream()
                bmp.compress(Bitmap.CompressFormat.PNG, 90, stream)
                val image = stream.toByteArray()
                Log.d(TAG,"byte array:$image")

                val img_str = Base64.encodeToString(image, 0)

                ServerTask(prepareJson(img_str),this@ChangeAvatar,this).execute()

                return true
            }
            R.id.popup_change_avatar_instagram -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/?hl=ru"))
                startActivity(intent)
            }
            R.id.popup_change_avatar_camera -> {
                dispatchTakePictureIntent();
            }
            R.id.popup_change_avatar_from_gallery -> {
                intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent, REQUEST_IMAGE_GALLERY)
            }
            R.id.popup_change_avatar_icloud -> {
                Toast.makeText(this@ChangeAvatar, item.title, Toast.LENGTH_SHORT).show();
            }
            R.id.popup_change_avatar_dropbox -> {
                Toast.makeText(this@ChangeAvatar, item.title, Toast.LENGTH_SHORT).show();
            }
            R.id.popup_change_avatar_delete -> {
                Toast.makeText(this@ChangeAvatar, item.title, Toast.LENGTH_SHORT).show();
            }
            R.id.popup_change_avatar_cancel -> {
                Toast.makeText(this@ChangeAvatar, item.title, Toast.LENGTH_SHORT).show();
            }

        }
        return super.onOptionsItemSelected(item)
    }

    var mCurrentPhotoPath: String = ""

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            mCurrentPhotoPath = absolutePath
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d(TAG,"onActivityResult requestCode=$requestCode resultCode=$resultCode data=$data")
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            Log.d(TAG,"REQUEST_IMAGE_CAPTURE")
            setPic()
            return
        }

        if((requestCode == REQUEST_IMAGE_GALLERY)&& (resultCode == RESULT_OK)){
            Log.d(TAG,"REQUEST_IMAGE_GALLERY")
            manageImageFromUri(data?.data!!);
            return
        }
    }

    private fun manageImageFromUri(imageUri: Uri) {
        var bitmap: Bitmap? = null

        try {
            bitmap = MediaStore.Images.Media.getBitmap(
                this.contentResolver, imageUri
            )

        } catch (e: Exception) {
            Log.e(TAG,e.message)
        }

        if (bitmap != null) {
            imageView_avatar.setImageBitmap(bitmap)
        }
    }

    fun FullScreencall() {
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            val v = this.window.decorView
            v.systemUiVisibility = View.GONE
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            val decorView = window.decorView
            val uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            decorView.systemUiVisibility = uiOptions
        }
    }

//    private fun showPopup(view: View) {
//        var popup: PopupMenu? = null;
//        popup = PopupMenu(this.imageView_avatar.context, view, Gravity.RIGHT)
//
//        popup.inflate(R.menu.menu_popup_change_avatar)
//
//        popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item: MenuItem? ->
//
//            when (item!!.itemId) {
//                R.id.popup_change_avatar_instagram -> {
//                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/?hl=ru"))
//                    startActivity(intent)
//                }
//                R.id.popup_change_avatar_camera -> {
//                    dispatchTakePictureIntent();
//                }
//                R.id.popup_change_avatar_from_gallery -> {
//                    intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//                    startActivityForResult(intent, REQUEST_IMAGE_GALLERY)
//                }
//                R.id.popup_change_avatar_icloud -> {
//                    Toast.makeText(this@ChangeAvatar, item.title, Toast.LENGTH_SHORT).show();
//                }
//                R.id.popup_change_avatar_dropbox -> {
//                    Toast.makeText(this@ChangeAvatar, item.title, Toast.LENGTH_SHORT).show();
//                }
//                R.id.popup_change_avatar_delete -> {
//                    Toast.makeText(this@ChangeAvatar, item.title, Toast.LENGTH_SHORT).show();
//                }
//                R.id.popup_change_avatar_cancel -> {
//                    Toast.makeText(this@ChangeAvatar, item.title, Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            true
//        })
//
//        popup.show()
//    }



    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    Log.e(TAG, ex.message)
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "com.f2f.face2face.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
                }
            }
        }
    }

    private fun galleryAddPic() {
        Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
            val f = File(mCurrentPhotoPath)
            mediaScanIntent.data = Uri.fromFile(f)
            sendBroadcast(mediaScanIntent)
        }
    }

    private fun setPic() {
        // Get the dimensions of the View
        val targetW: Int = imageView_avatar.width
        val targetH: Int = imageView_avatar.height

        val bmOptions = BitmapFactory.Options().apply {
            // Get the dimensions of the bitmap
            inJustDecodeBounds = true
            BitmapFactory.decodeFile(mCurrentPhotoPath, this)
            val photoW: Int = outWidth
            val photoH: Int = outHeight

            // Determine how much to scale down the image
            val scaleFactor: Int = Math.min(photoW / targetW, photoH / targetH)

            // Decode the image file into a Bitmap sized to fill the View
            inJustDecodeBounds = false
            inSampleSize = scaleFactor
            inPurgeable = true
        }
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions)?.also { bitmap ->
            imageView_avatar.setImageBitmap(bitmap)
        }
    }

    override fun handleResponse(resp: Response) {
        Log.d(TAG,"handleResponse")
        val pb = findViewById<ProgressBar>(R.id.pb_change_avatar)

        if (resp?.status.equals("0")) {
            Log.d(TAG,"status = 0")
//            super.onBackPressed()
            Toast.makeText(this@ChangeAvatar, "Аватар сохранен", Toast.LENGTH_SHORT).show()
        } else {
            if(resp?.status.equals("4")){
                setLoggedIn(this@ChangeAvatar,false)
                gotoLogin(this@ChangeAvatar)
            }
            Log.d(TAG,"${resp?.message}")
            Toast.makeText(this@ChangeAvatar, resp?.error, Toast.LENGTH_SHORT).show()
        }
        Log.d(TAG,"handleResponse done")

        pb.visibility= View.INVISIBLE
        super.onBackPressed()
        overridePendingTransition(R.xml.left_to_right, R.xml.right_to_left)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun prepareJson(ava:String):String{
        val name = this.findViewById<EditText>(R.id.change_account_name)
        val email = this.findViewById<EditText>(R.id.change_account_email)
        val surname =  this.findViewById<EditText>(R.id.change_account_surname)
        val result = ServerRequest(
            getInfo(this@ChangeAvatar),
            NAME_UPDATE_PROFILE,
            UpdateProfile_RequestData(name="",email= getEmail(this@ChangeAvatar),surname="",avatar=ava,promocode="")
        ).json()
        Log.d(TAG,"message=$result")
        return result

    }

}
