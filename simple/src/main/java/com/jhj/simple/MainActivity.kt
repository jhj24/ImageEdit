package com.jhj.simple

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.jhj.imageedit.IMGEditActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : Activity() {
    val REQ_IMAGE_EDIT = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val a = "/storage/emulated/0/DCIM/Camera/IMG_20171004_160402.jpg"
        val b = "/storage/emulated/0/DCIM/Camera/22.jpg"
        btn_image.setOnClickListener {
            startActivityForResult(
                    Intent(this, IMGEditActivity::class.java)
                            .putExtra(IMGEditActivity.EXTRA_IMAGE_URI, a)
                            .putExtra(IMGEditActivity.EXTRA_IMAGE_SAVE_PATH, b),
                    REQ_IMAGE_EDIT
            )
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQ_IMAGE_EDIT -> {
                if (resultCode == Activity.RESULT_OK) {
                    //onImageEditDone()
                }
            }
        }
    }
}
