package fastcampus.aop.part2.mycarrot

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import fastcampus.aop.part2.mycarrot.charlist.ChatListFragment
import fastcampus.aop.part2.mycarrot.databinding.ActivityMainBinding
import fastcampus.aop.part2.mycarrot.home.HomeFragment
import fastcampus.aop.part2.mycarrot.mypage.MyPageFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val homeFragment = HomeFragment()
        val chatListFragment = ChatListFragment()
        val myPageFragment = MyPageFragment()

        replaceFragment(homeFragment)

        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.home -> replaceFragment(homeFragment)
                R.id.chatList -> replaceFragment(chatListFragment)
                R.id.myPage -> replaceFragment(myPageFragment)
            }
            true
        }
        Log.d(TAG, "onCreate");
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart");
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop");
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume");
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy");
    }


    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .apply {
                replace(R.id.fragmentContainer, fragment)
                commit()
            }
    }

    companion object {
        private const val TAG = "LifeCycle: MainActivity"
    }
}