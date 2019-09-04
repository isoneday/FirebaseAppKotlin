package com.imastudio.firebaseapp.authentication

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.imastudio.firebaseapp.R
import kotlinx.android.synthetic.main.activity_login.*
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

class LoginActivity : AppCompatActivity() {


    var auth: FirebaseAuth? = null
    var database = FirebaseDatabase.getInstance()
    var reference = database.reference
    val reqcode = 1
    val reqcodeGalery = 2
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        ivImagePerson.setOnClickListener {
            checkStoragePermission()

        }
    }

    private fun checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) !=
                PackageManager.PERMISSION_GRANTED
            ) {

                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), reqcode)
                return
            }
        }
        loadImage()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            reqcode -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) loadImage()
                else Toast.makeText(this, "tidak dapat mengakses galery", Toast.LENGTH_SHORT).show()
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        }
    }

    private fun loadImage() {
        var intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, reqcodeGalery)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == reqcodeGalery && data != null && resultCode == Activity.RESULT_OK) {
            val selectedImage = data.data
            val filePathColum = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = contentResolver.query(selectedImage!!, filePathColum, null, null, null)
            cursor!!.moveToFirst()
            val coulomIndex = cursor!!.getColumnIndex(filePathColum[0])
            val picturePath = cursor!!.getString(coulomIndex)
            cursor!!.close()
            ivImagePerson.setImageBitmap(BitmapFactory.decodeFile(picturePath))
        }
    }


    fun onRegister(view: View) {

        RegisterToFirebase(etEmail.text.toString(), etPassword.text.toString())
    }

    fun onLogin(view: View) {

        LoginToFirebase(etEmail.text.toString(), etPassword.text.toString())
    }

    private fun LoginToFirebase(email: String, password: String) {
        auth?.signInWithEmailAndPassword(email, password)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "berhasil login", Toast.LENGTH_SHORT).show()
                saveImageToStorage()
            } else {
                Toast.makeText(this, "gagal login" + task.exception?.localizedMessage, Toast.LENGTH_SHORT).show()

            }
        }
    }

    private fun RegisterToFirebase(email: String, password: String) {
        auth?.createUserWithEmailAndPassword(email, password)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "berhasil register", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "gagal register" + task.exception?.localizedMessage, Toast.LENGTH_SHORT).show()

            }
        }


    }

    private fun saveImageToStorage() {
        var user = auth?.currentUser
        val email = user?.email.toString()
        val storage = FirebaseStorage.getInstance()
        val referenceStorage = storage.getReferenceFromUrl("gs://steadfast-fold-162314.appspot.com")
        //nama file
        val tgl =SimpleDateFormat("ddMMyyHHmmss")
        val obj = Date()
        val imagePath = SplitStringEmail(email)+"."+tgl.format(obj)+".jpg"
        val imageRef = referenceStorage.child("images_user/"+imagePath)
        //compress file image
        val drawable=ivImagePerson.drawable as BitmapDrawable
        val bitmap=drawable.bitmap
        val baos= ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos)
        val data= baos.toByteArray()
        val uploadTask=imageRef.putBytes(data)
        uploadTask.addOnFailureListener {
        task->
            Toast.makeText(this,"gagal upload image"+task.localizedMessage,Toast.LENGTH_SHORT).show()
        }.addOnSuccessListener {
            task ->

            var downloadUrl = referenceStorage.downloadUrl.toString()
            user?.uid?.let { reference.child("users").child(it).child("email").setValue(user?.email) }

            user?.uid?.let { reference.child("users").child(it).child("imageProfile").setValue(downloadUrl) }
            Toast.makeText(this,"berhasil upload image",Toast.LENGTH_SHORT).show()
            cekHalaman()
        }


    }

    private fun cekHalaman() {
        var user =auth?.currentUser
        if (user!= null){
                var intent = Intent(this,MainActivity::class.java)
                startActivity(intent)
            finish()
            }
    }

    override fun onStart() {
        super.onStart()
        cekHalaman()
    }

    private fun SplitStringEmail(email: String): String {
        val newEmail = email.split("@")
        return newEmail[0]
    }

}