package fastcampus.aop.part2.mycarrot.home

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import fastcampus.aop.part2.mycarrot.DBKey.Companion.DB_ARTICLES
import fastcampus.aop.part2.mycarrot.R
import fastcampus.aop.part2.mycarrot.databinding.ActivityAddArticleBinding

class AddArticleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddArticleBinding
    private var selectedUri: Uri? = null

    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }
    private val storage: FirebaseStorage by lazy {
        Firebase.storage
    }
    private val articleDB: DatabaseReference by lazy {
        Firebase.database.reference.child(DB_ARTICLES)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imageAddButton.setOnClickListener {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    startContentProvider()
                }
                shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                    showPermissionContextPopup()
                }
                else -> {
                    requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1010)
                }
            }
        }

        binding.submitButton.setOnClickListener {
            val title = binding.titleEditText.text.toString()
            val price = binding.priceEditText.text.toString()
            val sellerId = auth.currentUser?.uid.orEmpty()

            val model = ArticleModel(sellerId, title, System.currentTimeMillis(), "$price 원", "")
            articleDB.push().setValue(model)

            finish()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
                1010 -> {
                    if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        startContentProvider()
                    } else {
                        Toast.makeText(this, getString(R.string.reject_permission), Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun startContentProvider() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, 2020)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
            return
        }

        when (requestCode) {
            2020 -> {
                val uri = data?.data
                if (uri != null) {
                    binding.photoImageView.setImageURI(uri)
                    selectedUri = uri
                } else {
                    Toast.makeText(this, getString(R.string.fail_load_image), Toast.LENGTH_SHORT).show()
                }
            }
            else -> {
                Toast.makeText(this, getString(R.string.fail_load_image), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showPermissionContextPopup() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.need_permission))
            .setMessage(getString(R.string.need_permission_message))
            .setPositiveButton(getString(R.string.agree)) { _, _ ->
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1010)
            }
            .create()
            .show()
    }
}