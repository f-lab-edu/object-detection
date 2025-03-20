package com.example.objectdetection.activity

import android.os.Bundle
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.objectdetection.ImageListAdapter
import com.example.objectdetection.MainViewModel
import com.example.objectdetection.R
import com.example.objectdetection.databinding.ActivityMainBinding
import com.example.objectdetection.fragment.DetailViewPagerFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ImageListAdapter

    private val linearLayoutManager = LinearLayoutManager(this)
    private val gridLayoutManager = GridLayoutManager(this, 2)

    private val viewModel: MainViewModel by viewModels()

    private var isUpdateDialog = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        initSearchView()

        isUpdateDialog = intent.getBooleanExtra("isUpdateDialog", false)

        if (isUpdateDialog) {
            showUpdateDialog()
        }

        adapter = ImageListAdapter(emptyList()) { photoList, position ->
            val fragment = DetailViewPagerFragment.newInstance(photoList, position)
            setFragment(fragment)
        }
        binding.rvImage.adapter = adapter
        binding.rvImage.layoutManager = linearLayoutManager
        binding.toolbar.ivList.setOnClickListener {
            binding.toolbar.ivList.isSelected = !binding.toolbar.ivList.isSelected

            binding.rvImage.layoutManager = if (binding.toolbar.ivList.isSelected) {
                gridLayoutManager
            } else {
                linearLayoutManager
            }
        }

        viewModel.photos.observe(this) { photos ->
            photos?.let {
                adapter.updateData(photos)
            }
        }

        viewModel.apiError.observe(this) { exceptionMessage ->
            Toast.makeText(this@MainActivity, "$exceptionMessage", Toast.LENGTH_LONG).show()
        }
    }

    private fun initSearchView() {
        binding.sv.isSubmitButtonEnabled = true
        binding.sv.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    viewModel.searchPhotos(query)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }

    private fun setFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.slide_out
            )
            .replace(R.id.fv, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun showUpdateDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.version_update_dialog_title)
        builder.setMessage(R.string.version_update_dialog_sub_message)

        builder.setPositiveButton(R.string.version_update_dialog_positive) { dialog, _ ->
            dialog.dismiss()
        }

        builder.setNegativeButton(R.string.version_update_dialog_negative) { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }


    override fun onResume() {
        super.onResume()
        binding.sv.clearFocus()
    }
}
