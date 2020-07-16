package com.f2f.face2face

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.annotation.RequiresApi
import android.support.v4.content.FileProvider
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Base64
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.f2f.face2face.json.*
import kotlinx.android.synthetic.main.activity_change_avatar.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AdvertNew : AppCompatActivity(), ResponseProcessable {

    val TAG = "AdvertNew"

    val REQUEST_IMAGE_GALLERY = 2
    val REQUEST_TAKE_PHOTO = 3

    private var recycler: RecyclerView? = null
    private var adapter: AdvertNewPicturesAdapter? = null
    private val pictureList = ArrayList<Picture>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_advert_new)

        val pb = findViewById<ProgressBar>(R.id.pb_advert_new)
        pb.isIndeterminate = true
        pb.visibility = View.INVISIBLE

        initPictureList()
        recycler = findViewById<View>(R.id.advert_new_recycler_view_pictures) as RecyclerView?
        adapter = AdvertNewPicturesAdapter(this, pictureList)
        recycler!!.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recycler!!.adapter = adapter

    }

    override fun onResume() {
        super.onResume()
        val languige = findViewById<TextView>(R.id.advert_new_languige)
        val categories = findViewById<TextView>(R.id.advert_new_category)
        val pay = findViewById<TextView>(R.id.advert_new_payment)
        val price = findViewById<EditText>(R.id.advertNew_editText_price)
        val cur = findViewById<TextView>(R.id.advertNew_textView_Currency)
        val keywords = findViewById<TextView>(R.id.advert_new_keyword)

        keywords.text = ""
        if (advertNew_keywordsSelected.size > 0) {
            advertNew_keywordsSelected.forEach() {
                if (keywords.text.length > 0) {
                    keywords.text = keywords.text.toString() + ","
                }
                keywords.text = keywords.text.toString() + Storage.KEYWORDS.get(it).name
                Log.d(TAG, "${Storage.KEYWORDS.get(it).name} keywords.text=${keywords.text.toString()}")
            }
        } else {
            keywords.text = "Выберите ключевые слова"
        }

        if (advertNew_mPosition_category === -1) {
            categories.text = "Выберите категорию"
        } else {
            categories.text = Storage.CATEGORY.get(advertNew_mPosition_category).name
        }

        if (advertNew_mPosition_languige === -1) {
            languige.text = "Выберите язык"
        } else {
            languige.text = Storage.LANGUIGE.get(advertNew_mPosition_languige).name
        }

        if (advertNew_mPosition_payment === -1) {
            pay.text = "Выберите способ оплаты"
        } else {
            pay.text = Storage.PAYMENTS.get(advertNew_mPosition_payment).name
            if (pay.text.contains("Barter") || pay.text.contains("Batrer")) {
                price.visibility = View.INVISIBLE
                cur.visibility = View.INVISIBLE
            } else {
                price.visibility = View.VISIBLE
                cur.visibility = View.VISIBLE
            }
        }

    }

    fun initPictureList() {
        newPicture()
        newPicture()
        newPicture()
    }

    fun newPicture() {
        val idx = pictureList.size - 1
        val img_str =
            "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"
        pictureList.add(Picture(base64pic = img_str, mime_type = "image/png", order_id = "$idx"))
    }

    fun change_languiges(view: View?) {
        val inten = Intent(this, LanguigeList::class.java)
        inten.putExtra("param", "1");
        startActivity(inten)
        overridePendingTransition(R.xml.right_to_left1, R.xml.left_to_right_1)
    }

    fun change_category(view: View?) {
        val inten = Intent(this, CategoriesList::class.java)
        inten.putExtra("param", "1");
        startActivity(inten)
        overridePendingTransition(R.xml.right_to_left1, R.xml.left_to_right_1)
    }

    fun change_payment(view: View?) {
        val inten = Intent(this, PaymentList::class.java)
        inten.putExtra("param", "1");
        startActivity(inten)
        overridePendingTransition(R.xml.right_to_left1, R.xml.left_to_right_1)
    }

    fun change_keyword(view: View?) {
        val inten = Intent(this, KeywordsList::class.java)
        inten.putExtra("param", "1");
        startActivity(inten)
        overridePendingTransition(R.xml.right_to_left1, R.xml.left_to_right_1)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.xml.left_to_right, R.xml.right_to_left)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_advert_new, menu)
        return true
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
            R.id.save_new_advert -> {
                if (check_complete()) {
                    ServerTask(prepareJson(), this@AdvertNew, this).execute()
                }
                true
            }
            R.id.advert_new_add_picture -> {
                newPicture()
                adapter?.notifyDataSetChanged()
            }
            R.id.advert_new_photo_picture -> {
                dispatchTakePictureIntent();
            }
            R.id.advert_new_gallery_picture -> {
                intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent, REQUEST_IMAGE_GALLERY)
            }
            R.id.advert_new_delete_picture -> {
                if (mPosition_picture != -1) {
                    pictureList.removeAt(mPosition_picture)
                    mPosition_picture = -1
                    adapter?.notifyDataSetChanged()
                } else {
                    Toast.makeText(this@AdvertNew, "Выберите позицию в списке", Toast.LENGTH_SHORT).show()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun check_complete(): Boolean {
        val title = findViewById<EditText>(R.id.advertNew_editText_title).text.toString()
        val description = findViewById<EditText>(R.id.advertNew_editText_description).text.toString()
        val price = findViewById<EditText>(R.id.advertNew_editText_price).text.toString()
        val keywords = advertNew_keywordsSelected.toList()

        if (title.length == 0){
            Toast.makeText(this@AdvertNew, "Заполните поле Заголовок", Toast.LENGTH_SHORT).show()
            return false
        }

        if (description.length == 0){
            Toast.makeText(this@AdvertNew, "Заполните поле Примечание", Toast.LENGTH_SHORT).show()
            return false
        }

        if ((price.length == 0)&&(advertNew_mPosition_payment!=1)){
            Toast.makeText(this@AdvertNew, "Заполните поле Цена", Toast.LENGTH_SHORT).show()
            return false
        }

        if (keywords.size == 0){
            Toast.makeText(this@AdvertNew, "Выберите Ключевые слова", Toast.LENGTH_SHORT).show()
            return false
        }

        if (advertNew_mPosition_category==-1){
            Toast.makeText(this@AdvertNew, "Выберите Категорию", Toast.LENGTH_SHORT).show()
            return false
        }

        if (advertNew_mPosition_languige==-1){
            Toast.makeText(this@AdvertNew, "Выберите Язык", Toast.LENGTH_SHORT).show()
            return false
        }

        if (advertNew_mPosition_payment==-1){
            Toast.makeText(this@AdvertNew, "Выберите Способ оплаты", Toast.LENGTH_SHORT).show()
            return false
        }

        if(pictureList.size==0){
            Toast.makeText(this@AdvertNew, "Добавьте изображение", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun prepareJson(): String {
        val title = findViewById<EditText>(R.id.advertNew_editText_title).text.toString()
        val description = findViewById<EditText>(R.id.advertNew_editText_description).text.toString()
        val price = findViewById<EditText>(R.id.advertNew_editText_price).text.toString()
        val keywords = advertNew_keywordsSelected.toList()

        val pb = findViewById<ProgressBar>(R.id.pb_advert_new)
        pb.visibility = View.VISIBLE

        for (i in 0..pictureList.size-1){
            val pic = pictureList.get(i)
            pic.order_id = ""+i
        }

        val msg = ServerRequest(
            getInfo(this@AdvertNew),
            NAME_ADD_ADVERT,
            AddAdvert_RequestData(title, description, price, advertNew_mPosition_category,advertNew_mPosition_languige,advertNew_mPosition_payment,keywords,pictureList,1,1)
        ).json()

        Log.d(TAG,"msg=$msg")
        return msg

        return ""
    }

    override fun handleResponse(resp: Response) {
        val pb = findViewById<ProgressBar>(R.id.pb_advert_new)
        pb.visibility = View.INVISIBLE
        Log.d(TAG, "handleResponse start")
        if (resp?.status.equals("0")) {
            Log.d(TAG, "status = 0")
            super.onBackPressed()
        } else {
            if (resp?.status.equals("4")) {
                setLoggedIn(this@AdvertNew, false)
                gotoLogin(this@AdvertNew)
            }

            Log.d(TAG, "handleResponse error message: ${resp?.message} status = ${resp?.status} error=${resp?.error}")
            Toast.makeText(this@AdvertNew, resp?.error, Toast.LENGTH_SHORT).show()

        }
        Log.d(TAG, "handleResponse done")
    }

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d(TAG, "onActivityResult requestCode=$requestCode resultCode=$resultCode data=$data")
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            Log.d(TAG, "REQUEST_IMAGE_CAPTURE")
            setPic()
            return
        }

        if ((requestCode == REQUEST_IMAGE_GALLERY) && (resultCode == RESULT_OK)) {
            Log.d(TAG, "REQUEST_IMAGE_GALLERY")
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
            Log.e(TAG, e.message)
        }

        if (bitmap != null) {
            val byteArrayOutputStream = ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            val byteArray = byteArrayOutputStream.toByteArray();
            val encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
            val picture = pictureList.get(mPosition_picture) //removeAt(mPosition_picture)
            picture.base64pic = encoded

            adapter?.notifyDataSetChanged()
        }
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

    private fun setPic() {
        // Get the dimensions of the View
        val targetW: Int = 100
        val targetH: Int = 100

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
            //imageView_avatar.setImageBitmap(bitmap)
            val byteArrayOutputStream = ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            val byteArray = byteArrayOutputStream.toByteArray();
            val encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
            val picture = pictureList.get(mPosition_picture) //removeAt(mPosition_picture)
            picture.base64pic = encoded

            adapter?.notifyDataSetChanged()
        }
    }
}
