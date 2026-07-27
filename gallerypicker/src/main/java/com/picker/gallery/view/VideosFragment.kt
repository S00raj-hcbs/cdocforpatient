package com.picker.gallery.view

import android.Manifest
import android.annotation.SuppressLint
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
import com.picker.gallery.presenter.VideosPresenterImpl
import com.picker.gallery.utils.MLog
import com.picker.gallery.utils.RunOnUiThread
import com.picker.gallery.utils.font.FontsConstants
import com.picker.gallery.utils.font.FontsManager
import com.picker.gallery.utils.keypad.HideKeypad
import com.picker.gallery.view.adapters.AlbumAdapter
import com.picker.gallery.view.adapters.VideoGridAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
//import kotlinx.android.synthetic.main.fragment_media.*
//import org.jetbrains.anko.doAsync
import java.io.File
import java.util.*

/*
class VideosFragment : Fragment(), ImagePickerContract {

    var photoList: ArrayList<GalleryData> = ArrayList()
    var albumList: ArrayList<GalleryAlbums> = ArrayList()
    lateinit var glm: GridLayoutManager
    var photoids: ArrayList<Int> = ArrayList()

    val imagePickerPresenter: VideosPresenterImpl = VideosPresenterImpl(this)

    lateinit var listener: OnPhoneImagesObtained

    private val PERMISSIONS_READ_WRITE = 123

    lateinit var ctx: Context
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        ctx = inflater.context
        return inflater.inflate(R.layout.fragment_media, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        allowAccessButton.outlineProvider = ViewOutlineProvider.BACKGROUND

        imageGrid.setPopUpTypeface(FontsManager(ctx).getTypeface(FontsConstants.MULI_SEMIBOLD))

        albumselection.text = "All Videos"


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            if (isReadWritePermitted13()) initGalleryViews() else allowAccessFrame.visibility = View.VISIBLE
        }else{
            if (isReadWritePermitted()) initGalleryViews() else allowAccessFrame.visibility = View.VISIBLE
        }

        allowAccessButton.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                if (isReadWritePermitted13()) initGalleryViews() else checkReadWritePermission13()
            }else{
                if (isReadWritePermitted()) initGalleryViews() else checkReadWritePermission()
            }
            //if (isReadWritePermitted()) initGalleryViews() else checkReadWritePermission()
        }

        if (activity != null) HideKeypad().hideKeyboard(activity!!)
        backFrame.setOnClickListener { activity?.onBackPressed() }

        galleryIllusTitle.typeface = FontsManager(ctx).getTypeface(FontsConstants.MULI_SEMIBOLD)
        galleryIllusContent.typeface = FontsManager(ctx).getTypeface(FontsConstants.MULI_REGULAR)
        allowAccessButton.typeface = FontsManager(ctx).getTypeface(FontsConstants.MULI_SEMIBOLD)
    }

    fun initGalleryViews() {
        allowAccessFrame.visibility = View.GONE
        glm = GridLayoutManager(ctx, 3)
        imageGrid.itemAnimator = null
        val bundle = this.arguments
        if (bundle != null) photoids = if (bundle.containsKey("photoids")) bundle.getIntegerArrayList("photoids")!! else ArrayList()
        galleryOperation()
    }

    override fun galleryOperation() {
        doAsync {
            albumList = ArrayList()
            listener = object : OnPhoneImagesObtained {
                override fun onComplete(albums: ArrayList<GalleryAlbums>) {
                    albums.sortWith(compareBy { it.name })
                    for (album in albums) {
                        albumList.add(album)
                    }
                    albumList.add(0, GalleryAlbums(0, "All Videos", albumPhotos = photoList))
                    photoList.sortWith(compareByDescending { File(it.photoUri).lastModified() })

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
                            (ctx as PickerActivity).onBackPressed()
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
        imageGrid.adapter = VideoGridAdapter(imageList = photoList, threshold = (ctx as PickerActivity).VIDEOS_THRESHOLD)
    }

    override fun toggleDropdown() {
        dropdown.animate().rotationBy(0f).setDuration(300).setInterpolator(LinearInterpolator()).start()
        if ((albumsrecyclerview.adapter as AlbumAdapter).malbumList.size == 0) {
            albumsrecyclerview.adapter = AlbumAdapter(albumList, this)
            dropdown.setImageResource(R.drawable.ic_dropdown_rotate)
            try {
                done.isEnabled = false
                val animation = AnimationUtils.loadAnimation(ctx, R.anim.scale_down)
                done.startAnimation(animation)
            } catch (e: Exception) {
            }
            done.visibility = View.GONE
        } else {
            albumsrecyclerview.adapter = AlbumAdapter(ArrayList(), this)
            dropdown.setImageResource(R.drawable.ic_dropdown)
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
        requestPermissions(arrayOf(Manifest.permission.READ_MEDIA_VIDEO), PERMISSIONS_READ_WRITE)
        return true
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_READ_WRITE -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) initGalleryViews()
            else allowAccessFrame.visibility = View.VISIBLE
        }
    }

    private fun isReadWritePermitted(): Boolean = (context?.checkCallingOrSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && context?.checkCallingOrSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
    private fun isReadWritePermitted13(): Boolean = (context?.checkCallingOrSelfPermission(android.Manifest.permission.READ_MEDIA_VIDEO) == PackageManager.PERMISSION_GRANTED && context?.checkCallingOrSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)

}*/


class VideosFragment : Fragment(), ImagePickerContract {

    private var _binding: FragmentMediaBinding? = null
    private val binding get() = _binding!!

    internal var photoList: ArrayList<GalleryData> = ArrayList()
    private var albumList: ArrayList<GalleryAlbums> = ArrayList()
    private lateinit var glm: GridLayoutManager
    private var photoids: ArrayList<Int> = ArrayList()

    private val imagePickerPresenter: VideosPresenterImpl = VideosPresenterImpl(this)

    internal lateinit var listener: OnPhoneImagesObtained

    private val PERMISSIONS_READ_WRITE = 123

    internal lateinit var ctx: Context
    lateinit var imageGrid: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentMediaBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ctx = requireContext()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            binding.allowAccessButton.outlineProvider = ViewOutlineProvider.BACKGROUND
        }
       imageGrid= binding.imageGrid

        binding.imageGrid.setPopUpTypeface(FontsManager(ctx).getTypeface(FontsConstants.MULI_SEMIBOLD))
        binding.albumselection.text = "All Videos"

       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (isReadWritePermitted13()) initGalleryViews() else binding.allowAccessFrame.visibility = View.VISIBLE
        } else {*/
            if (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    isReadWritePermitted()
                } else {
                    TODO("VERSION.SDK_INT < M")
                }
            ) initGalleryViews() else binding.allowAccessFrame.visibility = View.VISIBLE
//}

        binding.allowAccessButton.setOnClickListener {
           /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (isReadWritePermitted13()) initGalleryViews() else checkReadWritePermission13()
            } else {*/
                if (isReadWritePermitted()) initGalleryViews() else checkReadWritePermission()
            //}
        }

        if (activity != null) HideKeypad().hideKeyboard(requireActivity())
        binding.backFrame.setOnClickListener { requireActivity().onBackPressed() }

        binding.galleryIllusTitle.typeface = FontsManager(ctx).getTypeface(FontsConstants.MULI_SEMIBOLD)
        binding.galleryIllusContent.typeface = FontsManager(ctx).getTypeface(FontsConstants.MULI_REGULAR)
        binding.allowAccessButton.typeface = FontsManager(ctx).getTypeface(FontsConstants.MULI_SEMIBOLD)
    }

    private fun initGalleryViews() {
        binding.allowAccessFrame.visibility = View.GONE
        glm = GridLayoutManager(ctx, 3)
        binding.imageGrid.itemAnimator = null
        val bundle = this.arguments
        if (bundle != null) photoids = if (bundle.containsKey("photoids")) bundle.getIntegerArrayList("photoids")!! else ArrayList()
        galleryOperation()
    }

/*    override fun galleryOperation() {
        doAsync {
            albumList = ArrayList()
            listener = object : OnPhoneImagesObtained {
                @RequiresApi(Build.VERSION_CODES.HONEYCOMB_MR1)
                override fun onComplete(albums: ArrayList<GalleryAlbums>) {
                    albums.sortWith(compareBy { it.name })
                    for (album in albums) {
                        albumList.add(album)
                    }
                    albumList.add(0, GalleryAlbums(0, "All Videos", albumPhotos = photoList))
                    photoList.sortWith(compareByDescending { File(it.photoUri).lastModified() })

                    for (id in photoids) {
                        for (image in photoList) {
                            if (id == image.id) image.isSelected = true
                        }
                    }

                    RunOnUiThread(ctx).safely {
                        binding.imageGrid.layoutManager = glm
                        initRecyclerViews()
                        binding.done.setOnClickListener {
                            val newList: ArrayList<GalleryData> = ArrayList()
                            photoList.filterTo(newList) { it.isSelected && it.isEnabled }
                        val i = Intent().apply {
                            putParcelableArrayListExtra("MEDIA", newList)
                        }
                            i.putParcelableArrayListExtra("MEDIA", newList)
                            (requireActivity() as PickerActivity).setResult((requireActivity() as PickerActivity).REQUEST_RESULT_CODE, i)
                            requireActivity().onBackPressed()
                        }
                        binding.albumselection.setOnClickListener {
                            toggleDropdown()
                        }
                        binding.dropdownframe.setOnClickListener {
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
    }*/

    override fun galleryOperation() {
        CoroutineScope(Dispatchers.IO).launch {
            albumList = ArrayList()

            listener = object : OnPhoneImagesObtained {
                @RequiresApi(Build.VERSION_CODES.HONEYCOMB_MR1)
                override fun onComplete(albums: ArrayList<GalleryAlbums>) {
                    albums.sortBy { it.name }
                    albumList.addAll(albums)
                    albumList.add(0, GalleryAlbums(0, "All Videos", albumPhotos = photoList))

                    photoList.sortByDescending { File(it.photoUri).lastModified() }

                    photoids.forEach { id ->
                        photoList.find { it.id == id }?.isSelected = true
                    }

                    // Switch to the main thread for UI updates
                    CoroutineScope(Dispatchers.Main).launch {
                        binding.imageGrid.layoutManager = glm
                        initRecyclerViews()

                        binding.done.setOnClickListener {
                            val selectedList = ArrayList(photoList.filter { it.isSelected && it.isEnabled })
                            val intent = Intent().apply {
                                putParcelableArrayListExtra("MEDIA", selectedList)
                            }
                            (requireActivity() as PickerActivity).apply {
                                setResult(REQUEST_RESULT_CODE, intent)
                                onBackPressed()
                            }
                        }

                        binding.albumselection.setOnClickListener { toggleDropdown() }
                        binding.dropdownframe.setOnClickListener { toggleDropdown() }
                    }
                }

                override fun onError() {
                    MLog.e("CURSOR", "FAILED")
                }
            }

            getPhoneAlbums(ctx, listener)
        }
    }
    override fun initRecyclerViews() {
        binding.albumsrecyclerview.layoutManager = LinearLayoutManager(ctx)
        binding.albumsrecyclerview.adapter = AlbumAdapter(ArrayList(), this)
        binding.imageGrid.adapter = VideoGridAdapter(imageList = photoList, threshold = (ctx as PickerActivity).VIDEOS_THRESHOLD)
    }

    @RequiresApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    override fun toggleDropdown() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                binding.dropdown.animate().rotationBy(0f).setDuration(300).setInterpolator(LinearInterpolator()).start()
            }

        if ((binding.albumsrecyclerview.adapter as AlbumAdapter).itemCount == 0) {
            binding.albumsrecyclerview.adapter = AlbumAdapter(albumList, this)
            binding.dropdown.setImageResource(R.drawable.ic_dropdown_rotate)
            try {
                binding.done.isEnabled = false
                val animation = AnimationUtils.loadAnimation(ctx, R.anim.scale_down)
                binding.done.startAnimation(animation)
            } catch (e: Exception) {
            }
            binding.done.visibility = View.GONE
        } else {
            binding.albumsrecyclerview.adapter = AlbumAdapter(ArrayList(), this)
            binding.dropdown.setImageResource(R.drawable.ic_dropdown)
            binding.done.isEnabled = true
            binding.done.visibility = View.VISIBLE
        }
    }

    override fun getPhoneAlbums(context: Context, listener: OnPhoneImagesObtained) {
        imagePickerPresenter.getPhoneAlbums()
    }

    override fun updateTitle(galleryAlbums: GalleryAlbums) {
        binding.albumselection.text = galleryAlbums.name
    }

    override fun updateSelectedPhotos(selectedlist: ArrayList<GalleryData>) {
        for (selected in selectedlist) {
            for (photo in photoList) {
                photo.isSelected = selected.id == photo.id
                photo.isEnabled = selected.id == photo.id
            }
        }
    }

    @SuppressLint("NewApi")
    fun checkReadWritePermission(): Boolean {
        requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSIONS_READ_WRITE)
        return true
    }

    fun checkReadWritePermission13(): Boolean {
        requestPermissions(arrayOf(Manifest.permission.READ_MEDIA_VIDEO), PERMISSIONS_READ_WRITE)
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_READ_WRITE -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) initGalleryViews()
            else binding.allowAccessFrame.visibility = View.VISIBLE
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun isReadWritePermitted(): Boolean = (ctx.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ctx.checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)

    /*@RequiresApi(Build.VERSION_CODES.M)
    private fun isReadWritePermitted13(): Boolean = (ctx.checkSelfPermission(android.Manifest.permission.READ_MEDIA_VIDEO) == PackageManager.PERMISSION_GRANTED && ctx.checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
*/
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}