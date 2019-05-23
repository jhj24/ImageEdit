package com.jhj.imageedit.weight

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.jhj.imageedit.IMGMode
import com.jhj.imageedit.IMGTextEditDialog
import com.jhj.imageedit.R
import kotlinx.android.synthetic.main.image_edit_activity.view.*
import kotlinx.android.synthetic.main.image_edit_clip_layout.view.*
import kotlinx.android.synthetic.main.image_edit_opt_layout.view.*
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class ImageEditView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {


    private var mTextDialog: IMGTextEditDialog? = null
    private var view: View = LayoutInflater.from(context).inflate(R.layout.image_edit_activity, this, false)


    fun setImagePath(path: String) {
        view.image_canvas.setImageBitmap(getBitmap(path))
        view.layout_operate.visibility = View.GONE
        addView(view)
        onViewClick()
    }

    private fun onViewClick() {

        view.rb_doodle.setOnClickListener {
            //涂鸦
            onModeClick(IMGMode.DOODLE)
        }
        view.btn_text.setOnClickListener {
            //文字
            onTextModeClick()
        }
        view.rb_mosaic.setOnClickListener {
            //马赛克
            onModeClick(IMGMode.MOSAIC)
        }
        view.btn_clip.setOnClickListener {
            //剪切
            onModeClick(IMGMode.CLIP)
        }
        view.btn_undo.setOnClickListener {
            //返回
            onUndoClick()
        }
        view.ib_clip_cancel.setOnClickListener {
            //取消剪切
            view.image_canvas.cancelClip()
            setOpDisplay(if (view.image_canvas.mode == IMGMode.CLIP) OP_CLIP else OP_NORMAL)
        }
        view.ib_clip_done.setOnClickListener {
            //剪切成功
            view.image_canvas.doClip()
            setOpDisplay(if (view.image_canvas.mode == IMGMode.CLIP) OP_CLIP else OP_NORMAL)
        }
        view.tv_clip_reset.setOnClickListener {
            //还原
            view.image_canvas.resetClip()
        }
        view.ib_clip_rotate.setOnClickListener {
            //选择
            view.image_canvas.doRotate()
        }
        view.cg_colors.setOnCheckedChangeListener { group, checkedId ->
            //颜色
            view.image_canvas.setPenColor(view.cg_colors.checkColor)
        }
    }

    private fun getBitmap(path: String): Bitmap? {
        val options = BitmapFactory.Options()
        options.inSampleSize = 1
        options.inJustDecodeBounds = true

        var bitmap: Bitmap? = null
        if (File(path).exists()) {
            bitmap = BitmapFactory.decodeFile(path)
        }
        return bitmap
    }


    private fun updateModeUI() {
        when (view.image_canvas.mode) {
            IMGMode.DOODLE -> {
                view.rg_modes.check(R.id.rb_doodle)
                setOpSubDisplay(OP_SUB_DOODLE)
            }
            IMGMode.MOSAIC -> {
                view.rg_modes.check(R.id.rb_mosaic)
                setOpSubDisplay(OP_SUB_MOSAIC)
            }
            IMGMode.NONE -> {
                view.rg_modes.clearCheck()
                setOpSubDisplay(OP_HIDE)
            }
            else -> {
            }
        }
    }

    private fun setOpDisplay(op: Int) {
        if (op >= 0) {
            view.vs_op.displayedChild = op
        }
    }

    private fun setOpSubDisplay(opSub: Int) {
        if (opSub < 0) {
            view.layout_op_sub.visibility = View.GONE
        } else {
            view.vs_op_sub.displayedChild = opSub
            view.layout_op_sub.visibility = View.VISIBLE
        }
    }


    private fun onModeClick(mode: IMGMode) {
        var imgMode = mode
        val cm = view.image_canvas.mode
        if (cm == imgMode) {
            imgMode = IMGMode.NONE
        }
        view.image_canvas.mode = imgMode
        updateModeUI()

        if (imgMode == IMGMode.CLIP) {
            setOpDisplay(OP_CLIP)
        }
    }

    private fun onTextModeClick() {
        if (mTextDialog == null) {
            mTextDialog = IMGTextEditDialog(context, IMGTextEditDialog.Callback { text -> view.image_canvas.addStickerText(text) })
            mTextDialog?.setOnShowListener { view.vs_op.visibility = View.GONE }
            mTextDialog?.setOnDismissListener { view.vs_op.visibility = View.VISIBLE }
        }
        mTextDialog?.show()
    }

    private fun onUndoClick() {
        val mode = view.image_canvas.mode
        if (mode == IMGMode.DOODLE) {
            view.image_canvas.undoDoodle()
        } else if (mode == IMGMode.MOSAIC) {
            view.image_canvas.undoMosaic()
        }
    }


    fun onSaveBitmap(targetPath: String, body: (Boolean) -> Unit) {
        if (!TextUtils.isEmpty(targetPath)) {
            val bitmap = view.image_canvas.saveBitmap()
            if (bitmap != null) {
                var fout: FileOutputStream? = null
                try {
                    fout = FileOutputStream(targetPath)
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fout)
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                } finally {
                    if (fout != null) {
                        try {
                            fout.close()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }
                body(true)
                return
            }
        }
        body(false)
    }


    companion object {

        const val OP_HIDE = -1

        const val OP_NORMAL = 0

        const val OP_CLIP = 1

        const val OP_SUB_DOODLE = 0

        const val OP_SUB_MOSAIC = 1
    }
}
