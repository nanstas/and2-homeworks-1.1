package ru.netology.nmedia.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentEditPostBinding
import ru.netology.nmedia.utils.StringArg
import ru.netology.nmedia.utils.Utils
import ru.netology.nmedia.viewmodel.PostViewModel
import java.io.File

@AndroidEntryPoint
class EditPostFragment : Fragment() {
    private val photoRequestCode = 1
    private val cameraRequestCode = 2

    companion object {
        var Bundle.textArg: String? by StringArg
    }

    @ExperimentalCoroutinesApi
    private val viewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)

    private var fragmentBinding: FragmentEditPostBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_edit_post, menu)
    }

    @ExperimentalCoroutinesApi
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.save -> {
                fragmentBinding?.let {
                    val id = arguments?.getLong("postId") ?: 0L
                    if (id != 0L) {
                        viewModel.changeContent(id, it.contentEditText.text.toString(), false)
                    } else {
                        viewModel.changeContent(0, it.contentEditText.text.toString(), true)
                    }
                    viewModel.save()
                    Utils.hideKeyboard(requireView())
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    @ExperimentalCoroutinesApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = FragmentEditPostBinding.inflate(
            inflater,
            container,
            false
        )
        fragmentBinding = binding


        val content = arguments?.getString("content") ?: ""

        binding.contentEditText.setText(content)

        binding.contentEditText.requestFocus()

        binding.pickPhoto.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(2048)
                .galleryOnly()
                .galleryMimeTypes(arrayOf(
                    "image/png",
                    "image/jpeg",
                ))
                .start(photoRequestCode)
        }

        binding.takePhoto.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(2048)
                .cameraOnly()
                .start(cameraRequestCode)
        }

        binding.removePhoto.setOnClickListener {
            viewModel.changePhoto(null, null)
        }

        viewModel.photo.observe(viewLifecycleOwner) {
            if (it.uri == null) {
                binding.photoContainer.visibility = View.GONE
                return@observe
            }

            binding.photoContainer.visibility = View.VISIBLE
            binding.photo.setImageURI(it.uri)
        }

        viewModel.postCreated.observe(viewLifecycleOwner) {
            viewModel.loadPosts()
            findNavController().navigateUp()
        }

        return binding.root
    }

    @ExperimentalCoroutinesApi
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == ImagePicker.RESULT_ERROR) {
            fragmentBinding?.let {
                Snackbar.make(it.root, ImagePicker.getError(data), Snackbar.LENGTH_LONG).show()
            }
            return
        }
        if (resultCode == Activity.RESULT_OK && requestCode == photoRequestCode) {
            val uri: Uri? = data?.data
            val file: File? = ImagePicker.getFile(data)
            viewModel.changePhoto(uri, file)
            return
        }
        if (resultCode == Activity.RESULT_OK && requestCode == cameraRequestCode) {
            val uri: Uri? = data?.data
            val file: File? = ImagePicker.getFile(data)
            viewModel.changePhoto(uri, file)
            return
        }
    }

    override fun onDestroyView() {
        fragmentBinding = null
        super.onDestroyView()
    }
}