package com.picker.gallery.view.adapters

import android.content.Context
import android.os.Build
import android.provider.MediaStore

import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.picker.gallery.R
import com.picker.gallery.databinding.AlbumItemBinding
import com.picker.gallery.model.GalleryAlbums
import com.picker.gallery.model.GalleryData
import com.picker.gallery.utils.font.FontsConstants
import com.picker.gallery.utils.font.FontsManager
import com.picker.gallery.view.PhotosFragment
import com.picker.gallery.view.VideosFragment
//import kotlinx.android.synthetic.main.album_item.view.*
//import kotlinx.android.synthetic.main.fragment_media.*
//import org.jetbrains.anko.doAsync
//import org.jetbrains.anko.uiThread
import java.util.ArrayList

/*
class AlbumAdapter() : RecyclerView.Adapter<AlbumAdapter.MyViewHolder>() {
    var malbumList: ArrayList<GalleryAlbums> = ArrayList()
    lateinit var currentFragment: Fragment

    constructor(albumList: ArrayList<GalleryAlbums> = ArrayList(), currentFragment: Fragment) : this() {
        malbumList = albumList
        this.currentFragment = currentFragment
    }

    lateinit var ctx: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        ctx = parent.context
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.album_item, parent, false))
    }

    @RequiresApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val count = malbumList[holder.adapterPosition].albumPhotos.count { it.isSelected }
        if (count > 0 && malbumList[holder.adapterPosition].id != 0) {
            holder.selectedcount.visibility = View.VISIBLE
            holder.selectedcount.text = count.toString()
        } else holder.selectedcount.visibility = View.GONE

        holder.albumtitle.text = malbumList[holder.adapterPosition].name
        holder.photoscount.text = malbumList[holder.adapterPosition].albumPhotos.size.toString()

        doAsync {
            uiThread {
                Glide.with(currentFragment).load(malbumList[holder.adapterPosition].coverUri).apply(RequestOptions().centerCrop().placeholder(R.drawable.ic_link_cont_default_img_1_5x)).into(holder.albumthumbnail)
            }
        }

        holder.albumFrame.setOnClickListener {
            when (currentFragment) {
                is PhotosFragment -> {
                    (currentFragment as PhotosFragment).updateTitle(malbumList[holder.adapterPosition])
                    (currentFragment as PhotosFragment).imageGrid.adapter = ImageGridAdapter((currentFragment as PhotosFragment).photoList, malbumList[holder.adapterPosition].id)
                    (currentFragment as PhotosFragment).toggleDropdown()
                }
                is VideosFragment -> {
                    (currentFragment as VideosFragment).updateTitle(malbumList[holder.adapterPosition])
                    (currentFragment as VideosFragment).imageGrid.adapter = VideoGridAdapter((currentFragment as VideosFragment).photoList, malbumList[holder.adapterPosition].id)
                    (currentFragment as VideosFragment).toggleDropdown()
                }
            }
        }

        holder.albumtitle.typeface = FontsManager(ctx).getTypeface(FontsConstants.MULI_REGULAR)
        holder.photoscount.typeface = FontsManager(ctx).getTypeface(FontsConstants.MULI_REGULAR)
    }

    override fun getItemCount(): Int = malbumList.size

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var albumthumbnail: ImageView = view.albumthumbnail
        var albumtitle: TextView = view.albumtitle
        var photoscount: TextView = view.photoscount
        var selectedcount: TextView = view.selectedcount
        var albumFrame: FrameLayout = view.albumFrame
    }
}*/
class AlbumAdapter(
    private var albumList: ArrayList<GalleryAlbums> = ArrayList(),
    private val currentFragment: Fragment
) : RecyclerView.Adapter<AlbumAdapter.MyViewHolder>() {

    lateinit var ctx: Context

    // Update method to set new data
    fun setData(newAlbumList: List<GalleryAlbums>) {
        albumList.clear()
        albumList.addAll(newAlbumList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        ctx = parent.context
        val binding = AlbumItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val album = albumList[position]

        val count = album.albumPhotos.count { it.isSelected }
        if (count > 0 && album.id != 0) {
            holder.binding.selectedcount.visibility = View.VISIBLE
            holder.binding.selectedcount.text = count.toString()
        } else {
            holder.binding.selectedcount.visibility = View.GONE
        }

        holder.binding.albumtitle.text = album.name
        holder.binding.photoscount.text = album.albumPhotos.size.toString()

        Glide.with(currentFragment)
            .load(album.coverUri)
            .apply(RequestOptions().centerCrop().placeholder(R.drawable.ic_link_cont_default_img_1_5x))
            .into(holder.binding.albumthumbnail)

        holder.binding.albumFrame.setOnClickListener {
            when (currentFragment) {
                is PhotosFragment -> {
                   /* if(album.name == "All Photos"){
                        currentFragment.updateTitle(album)
                        *//*val albumPhotos = loadAllPhotos(ctx)
                        (currentFragment as PhotosFragment).photoList=albumPhotos*//*
                        currentFragment.imageGrid.adapter = ImageGridAdapter((currentFragment as PhotosFragment).photoList, album.id)
                        currentFragment.toggleDropdown()
                    }else{*/
                        currentFragment.updateTitle(album)
                       /* val albumPhotos = getAlbumPhotos(album)
                        (currentFragment as PhotosFragment).photoList=albumPhotos*/
                        currentFragment.imageGrid.adapter = ImageGridAdapter( (currentFragment as PhotosFragment).photoList/*(currentFragment as PhotosFragment).photoList*/, album.id)
                        currentFragment.toggleDropdown()
                   // }
                }
                is VideosFragment -> {
                    currentFragment.updateTitle(album)
                    currentFragment.imageGrid.adapter = VideoGridAdapter((currentFragment as VideosFragment).photoList, album.id)
                    currentFragment .toggleDropdown()
                }
            }
        }

        holder.binding.albumtitle.typeface = FontsManager(ctx).getTypeface(FontsConstants.MULI_REGULAR)
        holder.binding.photoscount.typeface = FontsManager(ctx).getTypeface(FontsConstants.MULI_REGULAR)
    }

    fun loadAllPhotos(context: Context): ArrayList<GalleryData> {
        val photos = ArrayList<GalleryData>()
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DATE_ADDED
        )

        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        val cursor = context.contentResolver.query(uri, projection, null, null, sortOrder)
        cursor?.use {
            val idCol = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val albumNameCol = it.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
            val albumIdCol = it.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID)
            val dataCol = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            val dateCol = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)

            while (it.moveToNext()) {
                val id = it.getInt(idCol)
                val albumName = it.getString(albumNameCol) ?: "Unknown"
                val albumId = it.getInt(albumIdCol)
                val uriString = it.getString(dataCol)
                val dateAdded = it.getString(dateCol)

                photos.add(
                    GalleryData(
                        id = id,
                        albumName = albumName,
                        albumId = albumId,
                        photoUri = uriString,
                        dateAdded = dateAdded
                    )
                )
            }
        }

        return photos
    }


    fun getAlbumPhotos(album: GalleryAlbums): ArrayList<GalleryData> {
        val photos : ArrayList<GalleryData> = ArrayList()

        // Query MediaStore to get photos belonging to the selected album/folder
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME // To identify album/folder
        )

        // Use the album name or folder name to filter photos from that specific album/folder
        val selection = "${MediaStore.Images.Media.BUCKET_DISPLAY_NAME} = ?"
        val selectionArgs = arrayOf(album.name) // album.name is the name of the selected album/folder
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC" // Sort by the latest photo

        val cursor = ctx.contentResolver?.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )

        cursor?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val dataColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            val bucketColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)

            while (it.moveToNext()) {
                val id = it.getLong(idColumn)
                val name = it.getString(nameColumn)
                val data = it.getString(dataColumn)

                // Add photos from the selected album/folder to the list
                photos.add(GalleryData(
                    id = id.toInt(),
                    albumName = album.name,
                    photoUri = "content://media/external/images/media/$id",
                    albumId = album.id,
                    isSelected = false,
                    isEnabled = true,
                    mediaType = MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE,
                    dateAdded = System.currentTimeMillis().toString(),
                    thumbnail = data // Optionally provide a thumbnail
                ))
            }
        }

        return photos
    }

    override fun getItemCount(): Int = albumList.size

    class MyViewHolder(val binding: AlbumItemBinding) : RecyclerView.ViewHolder(binding.root)
}