package com.picker.gallery.view.adapters

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
//import kotlinx.android.synthetic.main.detailedimage.*
//import kotlinx.android.synthetic.main.grid_item.view.*
//import org.jetbrains.anko.doAsync
import kotlin.collections.ArrayList
import android.graphics.drawable.Drawable
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.picker.gallery.R
import com.picker.gallery.databinding.GridItemBinding
import com.picker.gallery.model.GalleryData
import com.picker.gallery.utils.DateUtil
import com.picker.gallery.utils.MLog
import com.picker.gallery.utils.RunOnUiThread
import com.picker.gallery.utils.scroll.FastScrollRecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/*
class VideoGridAdapter() : RecyclerView.Adapter<VideoGridAdapter.MyViewHolder>(), FastScrollRecyclerView.SectionedAdapter {

    lateinit var ctx: Context
    private var mimageList: ArrayList<GalleryData> = ArrayList()
    private var fullimagelist: ArrayList<GalleryData> = ArrayList()
    var THRESHOLD = 1

    constructor(imageList: ArrayList<GalleryData> = ArrayList(), filter: Int = 0, threshold: Int = 4) : this() {
        fullimagelist = imageList
        THRESHOLD = threshold
        if (filter == 0) mimageList = imageList
        else imageList.filter { it.albumId == filter }.forEach { mimageList.add(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        ctx = parent.context
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.grid_item, parent, false))
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        if (THRESHOLD != 0) {
            if (getSelectedCount() >= THRESHOLD) mimageList.filterNot { it.isSelected }.forEach { it.isEnabled = false }
            else mimageList.forEach { it.isEnabled = true }
        }

        doAsync {
            RunOnUiThread(ctx).safely {
                try {
                    val requestListener: RequestListener<Drawable> = object : RequestListener<Drawable> {
                        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                            MLog.e("error", "error")
                            holder.image.alpha = 0.3f
                            holder.image.isEnabled = false
                            holder.checkbox.visibility = View.INVISIBLE
                            return false
                        }

                        override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                            return false
                        }

                    }
                    Glide.with(ctx).load(mimageList[holder.adapterPosition].photoUri).apply(RequestOptions().centerCrop().override(150, 150)).transition(DrawableTransitionOptions.withCrossFade()).listener(requestListener).into(holder.image)
                } catch (e: Exception) {
                }
            }
        }

        if (mimageList[holder.adapterPosition].isEnabled) {
            holder.frame.alpha = 1.0f
            holder.image.isEnabled = true
            holder.checkbox.visibility = View.VISIBLE
        } else {
            holder.frame.alpha = 0.3f
            holder.image.isEnabled = false
            holder.checkbox.visibility = View.INVISIBLE
        }

        if (mimageList[holder.adapterPosition].mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO) {
            holder.durationFrame.visibility = View.VISIBLE
            holder.durationLabel.text = DateUtil().millisToTime(mimageList[holder.adapterPosition].duration.toLong())
        } else holder.durationFrame.visibility = View.GONE

        if (mimageList[holder.adapterPosition].isSelected) holder.checkbox.setImageResource(R.drawable.tick)
        else holder.checkbox.setImageResource(R.drawable.round)

        holder.image.setOnClickListener {
            if (THRESHOLD != 0) {
                when {
                    getSelectedCount() <= THRESHOLD -> {
                        if (mimageList[holder.adapterPosition].isSelected) {
                            mimageList[holder.adapterPosition].isSelected = false
                            holder.checkbox.setImageResource(R.drawable.round)
                            if (getSelectedCount() == (THRESHOLD - 1) && !mimageList[holder.adapterPosition].isSelected) {
                                mimageList.forEach { it.isEnabled = true }
                                for ((index, item) in mimageList.withIndex()) {
                                    if (item.isEnabled && !item.isSelected) notifyItemChanged(index)
                                }
                            }
                        } else {
                            mimageList[holder.adapterPosition].isSelected = true
                            holder.checkbox.setImageResource(R.drawable.tick)
                            if (getSelectedCount() == THRESHOLD && mimageList[holder.adapterPosition].isSelected) {
                                mimageList.filterNot { it.isSelected }.forEach { it.isEnabled = false }
                                for ((index, item) in mimageList.withIndex()) {
                                    if (!item.isEnabled) notifyItemChanged(index)
                                }
                            }
                        }
                    }
                    getSelectedCount() > THRESHOLD -> {
                        for (image in mimageList) {
                            mimageList.filter { it.isSelected && !it.isEnabled }.forEach { it.isSelected = false }
                        }
                    }
                    else -> {
                    }
                }
            } else {
                if (mimageList[holder.adapterPosition].isSelected) {
                    mimageList[holder.adapterPosition].isSelected = false
                    holder.checkbox.setImageResource(R.drawable.round)
                    notifyItemChanged(holder.adapterPosition)
                } else {
                    mimageList[holder.adapterPosition].isSelected = true
                    holder.checkbox.setImageResource(R.drawable.tick)
                    notifyItemChanged(holder.adapterPosition)
                }
            }
        }

        var dialog: Dialog? = null
        holder.image.setOnLongClickListener {
            dialog = Dialog(ctx)
            if (dialog != null) {
                dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog?.setContentView(R.layout.detailedimage)
                dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                doAsync {
                    RunOnUiThread(ctx).safely {
                        Glide.with(ctx).load(mimageList[holder.adapterPosition].photoUri).into(dialog?.bigimage!!)
                    }
                }
                dialog?.show()
            }
            true
        }

        holder.image.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP || event.action == DragEvent.ACTION_DROP) dialog?.dismiss()
            false
        }
    }

    private fun getSelectedCount(): Int = fullimagelist.count { it.isSelected }

    override fun getItemCount(): Int = mimageList.size

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var image = view.image
        var checkbox = view.checkbox
        var frame = view.frame
        val durationFrame = view.durationFrame
        val durationLabel = view.durationLabel
    }

    override fun getSectionName(position: Int): String = DateUtil().getMonthAndYearString(mimageList[position].dateAdded.toLong() * 1000)
}*/

class VideoGridAdapter(
    private var imageList: ArrayList<GalleryData> = ArrayList(),
    private val threshold: Int = 1
) : RecyclerView.Adapter<VideoGridAdapter.MyViewHolder>(), FastScrollRecyclerView.SectionedAdapter {

    private lateinit var ctx: Context
    private val fullImageList: ArrayList<GalleryData> = ArrayList()

    init {
        fullImageList.addAll(imageList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        ctx = parent.context
        val binding = GridItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.HONEYCOMB)
    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val image = imageList[position]
        val binding = holder.binding

        // Bind data to views using ViewBinding
        binding.apply {
            // Load image using Glide
            Glide.with(ctx)
                .load(image.photoUri)
                .apply(RequestOptions().centerCrop().override(150, 150))
                .transition(DrawableTransitionOptions.withCrossFade())
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.image.alpha = 0.3f
                        binding.image.isEnabled = false
                        binding.checkbox.visibility = View.INVISIBLE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }
                })
                .into(binding.image)

            // Handle selection logic
            binding.checkbox.setImageResource(
                if (image.isSelected) R.drawable.tick else R.drawable.round
            )
            binding.checkbox.visibility = if (image.isEnabled) View.VISIBLE else View.INVISIBLE
            binding.frame.alpha = if (image.isEnabled) 1.0f else 0.3f
            binding.image.isEnabled = image.isEnabled

            // Handle video duration visibility
            if (image.mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO) {
                binding.durationFrame.visibility = View.VISIBLE
                binding.durationLabel.text = DateUtil().millisToTime(image.duration.toLong())
            } else {
                binding.durationFrame.visibility = View.GONE
            }

            // Click listener for image item
            binding.image.setOnClickListener {
                handleImageSelection(position)
            }

            // Long click listener for image item
            var dialog: Dialog? = null
           /* binding.image.setOnLongClickListener {
                dialog = Dialog(ctx)
                dialog?.apply {
                    requestWindowFeature(Window.FEATURE_NO_TITLE)
                    setContentView(R.layout.detailedimage)
                    window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    doAsync {
                        RunOnUiThread(ctx).safely {
                            Glide.with(ctx).load(image.photoUri).into(findViewById(R.id.bigimage))
                        }
                    }
                    show()
                }
                true
            }*/
            binding.image.setOnLongClickListener {
            dialog = Dialog(ctx).apply {
                requestWindowFeature(Window.FEATURE_NO_TITLE)
                setContentView(R.layout.detailedimage)
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                CoroutineScope(Dispatchers.Main).launch {
                    Glide.with(ctx).load(image.photoUri).into(findViewById(R.id.bigimage))
                }

                show()
            }
            true
        }


            // Touch listener to dismiss dialog on touch release
            binding.image.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_UP || event.action == DragEvent.ACTION_DROP) {
                    dialog?.dismiss()
                }
                false
            }
        }
    }

    private fun handleImageSelection(position: Int) {
        if (threshold != 0) {
            when {
                getSelectedCount() <= threshold -> {
                    if (imageList[position].isSelected) {
                        imageList[position].isSelected = false
                    } else {
                        imageList[position].isSelected = true
                    }
                }
                else -> {
                    // Handle selection logic when threshold exceeded
                }
            }
        } else {
            if (imageList[position].isSelected) {
                imageList[position].isSelected = false
            } else {
                imageList[position].isSelected = true
            }
        }
        notifyItemChanged(position)
    }

    private fun getSelectedCount(): Int = fullImageList.count { it.isSelected }

    override fun getItemCount(): Int = imageList.size

    class MyViewHolder(val binding: GridItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun getSectionName(position: Int): String =
        DateUtil().getMonthAndYearString(imageList[position].dateAdded.toLong() * 1000)
}
