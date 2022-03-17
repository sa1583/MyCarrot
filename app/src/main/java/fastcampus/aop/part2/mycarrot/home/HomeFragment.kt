package fastcampus.aop.part2.mycarrot.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import fastcampus.aop.part2.mycarrot.DBKey.Companion.CHILD_CHAT
import fastcampus.aop.part2.mycarrot.DBKey.Companion.DB_ARTICLES
import fastcampus.aop.part2.mycarrot.DBKey.Companion.DB_USERS
import fastcampus.aop.part2.mycarrot.R
import fastcampus.aop.part2.mycarrot.chatlist.ChatListItem
import fastcampus.aop.part2.mycarrot.databinding.FragmentHomeBinding

class HomeFragment: Fragment(R.layout.fragment_home) {
    private lateinit var articleDB: DatabaseReference
    private lateinit var userDB: DatabaseReference
    private lateinit var articleAdapter: ArticleAdapter

    private val articleList = mutableListOf<fastcampus.aop.part2.mycarrot.home.ArticleModel>()
    private val listener = object: ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            val articleModel = snapshot.getValue(ArticleModel::class.java)
            articleModel ?: return

            articleList.add(articleModel)
            articleAdapter.submitList(articleList)
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

        override fun onChildRemoved(snapshot: DataSnapshot) {}

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

        override fun onCancelled(error: DatabaseError) {}
    }


    private var binding: FragmentHomeBinding? = null
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentHomeBinding = FragmentHomeBinding.bind(view)
        binding = fragmentHomeBinding

        articleList.clear()
        articleDB = Firebase.database.reference.child(DB_ARTICLES)
        userDB = Firebase.database.reference.child(DB_USERS)
        articleAdapter = ArticleAdapter(onItemClicked = { articleModel ->
            if (auth.currentUser != null) {
                if (auth.currentUser!!.uid != articleModel.sellerId) {
                    val chatRoom = ChatListItem(
                        buyerId = auth.currentUser!!.uid,
                        sellerId = articleModel.sellerId,
                        itemTitle = articleModel.title,
                        key = System.currentTimeMillis()
                    )
                    userDB.child(auth.currentUser!!.uid)
                        .child(CHILD_CHAT)
                        .push()
                        .setValue(chatRoom)

                    userDB.child(articleModel.sellerId)
                        .child(CHILD_CHAT)
                        .push()
                        .setValue(chatRoom)

                    Snackbar.make(view, getString(R.string.chat_room_success), Snackbar.LENGTH_SHORT).show()

                } else {
                    Snackbar.make(view, getString(R.string.my_item), Snackbar.LENGTH_SHORT).show()
                }
            } else {
                Snackbar.make(view, getString(R.string.need_login), Snackbar.LENGTH_SHORT).show()
            }

        })

        fragmentHomeBinding.articleRecyclerVIew.layoutManager = LinearLayoutManager(context)
        fragmentHomeBinding.articleRecyclerVIew.adapter = articleAdapter

        fragmentHomeBinding.addFloatingButton.setOnClickListener {
            if (auth.currentUser != null) {
                val intent = Intent(requireContext(), AddArticleActivity::class.java)
                startActivity(intent)
            } else {
                Snackbar.make(view, getString(R.string.need_login), Snackbar.LENGTH_SHORT).show()
            }
        }
        articleDB.addChildEventListener(listener)
    }

    override fun onResume() {
        super.onResume()
        articleAdapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        articleDB.removeEventListener(listener)
    }


}