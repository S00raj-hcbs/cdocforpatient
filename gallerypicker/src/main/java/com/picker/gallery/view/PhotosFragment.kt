package com.picker.gallery.view

import android.Manifest
import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.picker.gallery.R
import com.picker.gallery.databinding.FragmentMediaBinding
import com.picker.gallery.model.GalleryAlbums
import com.picker.gallery.model.GalleryData
import com.picker.gallery.presenter.PhotosPresenterImpl
import com.picker.gallery.utils.MLog
import com.picker.gallery.utils.RunOnUiThread
import com.picker.gallery.utils.font.FontsConstants
import com.picker.gallery.utils.font.FontsManager
import com.picker.gallery.utils.keypad.HideKeypad
import com.picker.gallery.utils.scroll.FastScrollRecyclerView
import com.picker.gallery.view.adapters.AlbumAdapter
import com.picker.gallery.view.adapters.ImageGridAdapter
//import kotlinx.android.synthetic.main.fragment_media.*
//import org.jetbrains.anko.doAsync
import java.io.File
import java.util.*

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/*
class PhotosFragment : Fragment(), ImagePickerContract {

    var photoList: ArrayList<GalleryData> = ArrayList()
    var albumList: ArrayList<GalleryAlbums> = ArrayList()
    lateinit var glm: GridLayoutManager
    var photoids: ArrayList<Int> = ArrayList()
    val imagePickerPresenter: PhotosPresenterImpl = PhotosPresenterImpl(this)
    lateinit var listener: OnPhoneImagesObtained
    private val PERMISSIONS_READ_WRITE = 123
    //var imageGrid: FastScrollRecyclerView

    lateinit var ctx: Context
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        ctx = inflater.context
        return inflater.inflate(R.layout.fragment_media, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        allowAccessButton.outlineProvider = ViewOutlineProvider.BACKGROUND

        initViews()

        allowAccessButton.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                if (isReadWritePermitted13()) initGalleryViews() else checkReadWritePermission13()
            }else{
                if (isReadWritePermitted()) initGalleryViews() else checkReadWritePermission()
            }
        }

        if (activity != null) HideKeypad().hideKeyboard(requireActivity())
        backFrame.setOnClickListener { activity?.finish() }

        imageGrid.setPopUpTypeface(FontsManager(ctx).getTypeface(FontsConstants.MULI_SEMIBOLD))
        galleryIllusTitle.typeface = FontsManager(ctx).getTypeface(FontsConstants.MULI_SEMIBOLD)
        galleryIllusContent.typeface = FontsManager(ctx).getTypeface(FontsConstants.MULI_REGULAR)
        allowAccessButton.typeface = FontsManager(ctx).getTypeface(FontsConstants.MULI_SEMIBOLD)
    }

    fun initViews() {
        photoList.clear()
        albumList.clear()
        photoids.clear()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            if (isReadWritePermitted13()) initGalleryViews() else allowAccessFrame.visibility = View.VISIBLE
        }else{
            if (isReadWritePermitted()) initGalleryViews() else allowAccessFrame.visibility = View.VISIBLE
        }
    }

    fun initGalleryViews() {
        allowAccessFrame.visibility = View.GONE
        glm = GridLayoutManager(ctx, 4)
        imageGrid.itemAnimator = null
        val bundle = this.arguments
        if (bundle != null) photoids = if (bundle.containsKey("photoids")) bundle.getIntegerArrayList("photoids")!! else ArrayList()
        galleryOperation()
    }

    override fun galleryOperation() {
        doAsync {
            albumList = ArrayList()
            listener = object : OnPhoneImagesObtained {
                @RequiresApi(Build.VERSION_CODES.HONEYCOMB_MR1)
                override fun onComplete(albums: ArrayList<GalleryAlbums>) {
                   // albums.sortWith(compareBy { it.name })
                    for (album in albums) {
                        albumList.add(album)
                    }
                    albumList.add(0, GalleryAlbums(0, "All Photos", albumPhotos = photoList))
                  //  photoList.sortWith(compareByDescending { File(it.photoUri).lastModified() })
                    photoList.reverse();

                    for (id in photoids) {
                        for (image in photoList) {
                            if (id == image.id) image.isSelected = true
                        }
                    }

                    RunOnUiThread(ctx).safely {
                        imageGrid.layoutManager = glm
                        initRecyclerViews()
                        done.setOnClickListener {
                            val newList: ArrayList<GalleryData> = ArrayList()
                            photoList.filterTo(newList) { it.isSelected && it.isEnabled }
                            val i = Intent()
                            i.putParcelableArrayListExtra("MEDIA", newList)
                            (ctx as PickerActivity).setResult((ctx as PickerActivity).REQUEST_RESULT_CODE, i)
                            (ctx as PickerActivity).finish()
                        }
                        albumselection.setOnClickListener {
                            toggleDropdown()
                        }
                        dropdownframe.setOnClickListener {
                            toggleDropdown()
                        }
                    }
                }

                override fun onError() {
                    MLog.e("CURSOR", "FAILED")
                }
            }

            doAsync {
                getPhoneAlbums(ctx, listener)
            }
        }
    }

    override fun initRecyclerViews() {
        albumsrecyclerview.layoutManager = LinearLayoutManager(ctx)
        albumsrecyclerview.adapter = AlbumAdapter(ArrayList(), this)
        imageGrid.adapter = ImageGridAdapter(imageList = photoList, threshold = (ctx as PickerActivity).IMAGES_THRESHOLD)
    }

    @RequiresApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    override fun toggleDropdown() {
        dropdown.animate().rotationBy(0f).setDuration(300).setInterpolator(LinearInterpolator()).start()
        if ((albumsrecyclerview.adapter as AlbumAdapter).malbumList.size == 0) {
            albumsrecyclerview.adapter = AlbumAdapter(albumList, this)
            dropdown.setImageResource(R.drawable.ic_gallery_up)
            try {
                done.isEnabled = false
                val animation = AnimationUtils.loadAnimation(ctx, R.anim.scale_down)
                done.startAnimation(animation)
            } catch (e: Exception) {
            }
            done.visibility = View.GONE
        } else {
            albumsrecyclerview.adapter = AlbumAdapter(ArrayList(), this)
            dropdown.setImageResource(R.drawable.ic_gallery_down)
            done.isEnabled = true
            done.visibility = View.VISIBLE
        }
    }

    override fun getPhoneAlbums(context: Context, listener: OnPhoneImagesObtained) {
        imagePickerPresenter.getPhoneAlbums()
    }

    override fun updateTitle(galleryAlbums: GalleryAlbums) {
        albumselection.text = galleryAlbums.name
    }

    override fun updateSelectedPhotos(selectedlist: ArrayList<GalleryData>) {
        for (selected in selectedlist) {
            for (photo in photoList) {
                photo.isSelected = selected.id == photo.id
                photo.isEnabled = selected.id == photo.id
            }
        }
    }

    @TargetApi(android.os.Build.VERSION_CODES.JELLY_BEAN)
    fun checkReadWritePermission(): Boolean {
        requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSIONS_READ_WRITE)
        return true
    }


    fun checkReadWritePermission13(): Boolean {
        requestPermissions(arrayOf(Manifest.permission.READ_MEDIA_IMAGES), PERMISSIONS_READ_WRITE)
        return true
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_READ_WRITE -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) initGalleryViews()
            else allowAccessFrame.visibility = View.VISIBLE
        }
    }

    private fun isReadWritePermitted(): Boolean = (context?.checkCallingOrSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && context?.checkCallingOrSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)

    private fun isReadWritePermitted13(): Boolean = (context?.checkCallingOrSelfPermission(android.Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED )

}*/
class PhotosFragment : Fragment(), ImagePickerContract {

    private var _binding: FragmentMediaBinding? = null
    private val binding get() = _binding!!

    private lateinit var glm: GridLayoutManager
    internal lateinit var ctx: Context
    private val imagePickerPresenter: PhotosPresenterImpl = PhotosPresenterImpl(this)
    internal lateinit var listener: OnPhoneImagesObtained
    private val PERMISSIONS_READ_WRITE = 123
    private var photoids: ArrayList<Int> = ArrayList()
    internal var photoList: ArrayList<GalleryData> = ArrayList()
    private var albumList: ArrayList<GalleryAlbums> = ArrayList()
    lateinit var imageGrid: RecyclerView
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentMediaBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ctx = requireContext()

        binding.allowAccessButton.outlineProvider = ViewOutlineProvider.BACKGROUND

        initViews()

        binding.allowAccessButton.setOnClickListener {
           /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (isReadWritePermitted13()) initGalleryViews() else checkReadWritePermission13()
            } else {*/
                if (isReadWritePermitted()) initGalleryViews() else checkReadWritePermission()
           // }
           // if (isReadWritePermitted()) initGalleryViews() else checkReadWritePermission()
        }

        HideKeypad().hideKeyboard(requireActivity())

        binding.backFrame.setOnClickListener { requireActivity().finish() }

        binding.imageGrid.setPopUpTypeface(FontsManager(ctx).getTypeface(FontsConstants.MULI_SEMIBOLD))
        binding.galleryIllusTitle.typeface = FontsManager(ctx).getTypeface(FontsConstants.MULI_SEMIBOLD)
        binding.galleryIllusContent.typeface = FontsManager(ctx).getTypeface(FontsConstants.MULI_REGULAR)
        binding.allowAccessButton.typeface = FontsManager(ctx).getTypeface(FontsConstants.MULI_SEMIBOLD)
    }

    internal fun initViews() {
        imageGrid = binding.imageGrid // Replace with your actual RecyclerView ID

        photoList.clear()
        albumList.clear()
        photoids.clear()
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (isReadWritePermitted13()) initGalleryViews() else binding.allowAccessFrame.visibility = View.VISIBLE
        } else {*/
            if (isReadWritePermitted()) initGalleryViews() else binding.allowAccessFrame.visibility = View.VISIBLE
       // }
        //if (isReadWritePermitted()) initGalleryViews() else binding.allowAccessFrame.visibility = View.VISIBLE
    }

    private fun initGalleryViews() {
        binding.allowAccessFrame.visibility = View.GONE
        glm = GridLayoutManager(ctx, 4)
        binding.imageGrid.itemAnimator = null
        val bundle = arguments
        photoids = bundle?.getIntegerArrayList("photoids") ?: ArrayList()
        galleryOperation()
    }

    /*override fun galleryOperation() {
        doAsync {
            albumList = ArrayList()
            listener = object : OnPhoneImagesObtained {
                @RequiresApi(Build.VERSION_CODES.HONEYCOMB_MR1)
                override fun onComplete(albums: ArrayList<GalleryAlbums>) {
                    // albums.sortWith(compareBy { it.name })
                    albumList.addAll(albums)
                    albumList.add(0, GalleryAlbums(0, "All Photos", albumPhotos = photoList))
                    // photoList.sortWith(compareByDescending { File(it.photoUri).lastModified() })
                    photoList.reverse()

                    photoids.forEach { id ->
                        photoList.forEach { image ->
                            if (id == image.id) image.isSelected = true
                        }
                    }

                    RunOnUiThread(ctx).safely {
                        binding.imageGrid.layoutManager = glm
                        initRecyclerViews()
                        binding.done.setOnClickListener {
                            val newList = photoList.filter { it.isSelected && it.isEnabled } as ArrayList
                            val i = Intent().apply {
                                putParcelableArrayListExtra("MEDIA", newList)
                            }
                            (ctx as PickerActivity).setResult((ctx as PickerActivity).REQUEST_RESULT_CODE, i)
                            (ctx as PickerActivity).finish()
                        }
                        binding.albumselection.setOnClickListener { toggleDropdown() }
                        binding.dropdownframe.setOnClickListener { toggleDropdown() }
                    }
                }

                override fun onError() {
                    MLog.e("CURSOR", "FAILED")
                }
            }

            doAsync {
                getPhoneAlbums(ctx, listener)
            }
        }
    }*/


    override fun galleryOperation() {
        CoroutineScope(Dispatchers.IO).launch {
            albumList = ArrayList()
            listener = object : OnPhoneImagesObtained {
                @RequiresApi(Build.VERSION_CODES.HONEYCOMB_MR1)
                override fun onComplete(albums: ArrayList<GalleryAlbums>) {
                    albumList.addAll(albums)
                    albumList.add(0, GalleryAlbums(0, "All Photos", albumPhotos = photoList))
                    photoList.reverse()

                    photoids.forEach { id ->
                        photoList.forEach { image ->
                            if (id == image.id) image.isSelected = true
                        }
                    }

                    CoroutineScope(Dispatchers.Main).launch {
                        binding.imageGrid.layoutManager = glm
                        initRecyclerViews()
                        binding.done.setOnClickListener {
                            val newList = photoList.filter { it.isSelected && it.isEnabled } as ArrayList
                            val i = Intent().apply {
                                putParcelableArrayListExtra("MEDIA", newList)
                            }
                            (ctx as PickerActivity).setResult((ctx as PickerActivity).REQUEST_RESULT_CODE, i)
                            (ctx as PickerActivity).finish()
                        }
                        binding.albumselection.setOnClickListener { toggleDropdown() }
                        binding.dropdownframe.setOnClickListener { toggleDropdown() }
                    }
                }

                override fun onError() {
                    MLog.e("CURSOR", "FAILED")
                }
            }

            withContext(Dispatchers.IO) {
                getPhoneAlbums(ctx, listener)
            }
        }
    }


    override fun initRecyclerViews() {
        binding.albumsrecyclerview.layoutManager = LinearLayoutManager(ctx)
        binding.albumsrecyclerview.adapter = AlbumAdapter(ArrayList(), this)
        binding.imageGrid.adapter = ImageGridAdapter(imageList = photoList, threshold = (ctx as PickerActivity).IMAGES_THRESHOLD)
    }

    @RequiresApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    override fun toggleDropdown() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            binding.dropdown.animate().rotationBy(0f).setDuration(300).setInterpolator(LinearInterpolator()).start()
        }
       /* val albumsAdapter = binding.albumsrecyclerview.adapter as AlbumAdapter
        if (albumsAdapter.malbumList.isEmpty()) {
            albumsAdapter.malbumList.addAll(albumList)
            binding.dropdown.setImageResource(R.drawable.ic_gallery_up)
            try {
                binding.done.isEnabled = false
                val animation = AnimationUtils.loadAnimation(ctx, R.anim.scale_down)
                binding.done.startAnimation(animation)
            } catch (e: Exception) {
            }
            binding.done.visibility = View.GONE
        } else {
            albumsAdapter.malbumList.clear()
            binding.dropdown.setImageResource(R.drawable.ic_gallery_down)
            binding.done.isEnabled = true
            binding.done.visibility = View.VISIBLE
        }*/

        val albumsAdapter = binding.albumsrecyclerview.adapter as? AlbumAdapter
        if (albumsAdapter != null) {
            if (albumsAdapter.itemCount == 0) { // Check if adapter is empty
                albumsAdapter.setData(albumList)
                binding.dropdown.setImageResource(R.drawable.ic_gallery_up)
                try {
                    binding.done.isEnabled = false
                    val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_down)
                    binding.done.startAnimation(animation)
                } catch (e: Exception) {
                    // Handle animation exception if needed
                }
                binding.done.visibility = View.GONE
            } else {
                albumsAdapter.setData(emptyList()) // Clear adapter data
                binding.dropdown.setImageResource(R.drawable.ic_gallery_down)
                binding.done.isEnabled = true
                binding.done.visibility = View.VISIBLE
            }
        } else {
            // Create and set adapter if not already set
            binding.albumsrecyclerview.adapter = AlbumAdapter(albumList, this)
        }
    }

    override fun getPhoneAlbums(context: Context, listener: OnPhoneImagesObtained) {
        imagePickerPresenter.getPhoneAlbums()
    }

    override fun updateTitle(galleryAlbums: GalleryAlbums) {
        binding.albumselection.text = galleryAlbums.name
    }

    override fun updateSelectedPhotos(selectedlist: ArrayList<GalleryData>) {
        photoList.forEach { photo ->
            photo.isSelected = selectedlist.any { it.id == photo.id }
            photo.isEnabled = selectedlist.any { it.id == photo.id }
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun checkReadWritePermission(): Boolean {
        requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSIONS_READ_WRITE)
        return true
    }

    /*private fun checkReadWritePermission13(): Boolean {
        requestPermissions(arrayOf(Manifest.permission.READ_MEDIA_IMAGES), PERMISSIONS_READ_WRITE)
        return true
    }*/

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_READ_WRITE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initGalleryViews()
                } else {
                    binding.allowAccessFrame.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun isReadWritePermitted(): Boolean = (context?.checkCallingOrSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
            context?.checkCallingOrSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)

/*
    private fun isReadWritePermitted13(): Boolean = (context?.checkCallingOrSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED)
*/

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}