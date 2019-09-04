package com.imastudio.firebaseapp

import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.imastudio.firebaseapp.model.Berita

import kotlinx.android.synthetic.main.activity_utama.*

class UtamaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_utama)
        setSupportActionBar(toolbar)
        val berita = FirebaseDatabase.getInstance().getReference("berita")
        berita.addValueEventListener(
            object: ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    for( data in p0.children){
                        var post = data.getValue(Berita::class.java)


                    }
                }
            }
        )
        fab.setOnClickListener { view ->
          startActivity(Intent(this,InsertBeritaActivity::class.java))
            finish()
        }
    }

}
