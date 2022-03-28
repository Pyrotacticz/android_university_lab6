package com.codepath.nytimes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.codepath.nytimes.ui.books.BestSellerBooksFragment
import com.codepath.nytimes.ui.home.HomeFragment
import com.codepath.nytimes.ui.search.ArticleResultFragment
import com.codepath.nytimes.ui.settings.SettingsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private var homeFragment = HomeFragment()
    private var articleResultFragment = ArticleResultFragment.newInstance()
    private var bestSellerBooksFragment = BestSellerBooksFragment.newInstance()
    private var settingsFragment = SettingsFragment()
    lateinit var bottomNavigationView: BottomNavigationView
    var SELECTED_ITEM_ID_KEY: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fragmentManager: FragmentManager = supportFragmentManager

        bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            var fragment: Fragment? = null
            when (item.itemId) {
                R.id.action_home -> {
                    fragment = homeFragment
                    SELECTED_ITEM_ID_KEY = HOME_TAG
                }
                R.id.action_bsb -> {
                    fragment = bestSellerBooksFragment
                    SELECTED_ITEM_ID_KEY = BOOKS_TAG
                }
                R.id.action_search -> {
                    fragment = articleResultFragment
                    SELECTED_ITEM_ID_KEY = ARTICLES_TAG
                }
                R.id.action_settings -> {
                    fragment = settingsFragment
                    SELECTED_ITEM_ID_KEY = SETTINGS_TAG
                }
            }
            if (fragment != null) {
                fragmentManager.beginTransaction().replace(R.id.flContainer,
                    fragment, SELECTED_ITEM_ID_KEY).commit()
            }
            // return true to say that we handled the interaction
            true
        }

        if (savedInstanceState != null) {
            SELECTED_ITEM_ID_KEY = savedInstanceState.getString(CHOSEN_TAG)
            Log.i("MAIN", SELECTED_ITEM_ID_KEY.toString())
            val selectedBottomId = savedInstanceState.getInt(SELECTED_ITEM_ID_KEY!!)
            when (selectedBottomId) {
                R.id.action_home -> {
                    homeFragment = fragmentManager.findFragmentByTag(HOME_TAG) as HomeFragment
                }
                R.id.action_bsb -> {
                    bestSellerBooksFragment =
                        fragmentManager.findFragmentByTag(BOOKS_TAG) as BestSellerBooksFragment
                }
                R.id.action_search -> {
                    articleResultFragment =
                        fragmentManager.findFragmentByTag(ARTICLES_TAG) as ArticleResultFragment
                }
                R.id.action_settings -> {
                    settingsFragment =
                        fragmentManager.findFragmentByTag(SETTINGS_TAG) as SettingsFragment
                }
            }
            bottomNavigationView.selectedItemId = selectedBottomId
        } else {
            bottomNavigationView.selectedItemId = R.id.action_home
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        Log.i("saving", SELECTED_ITEM_ID_KEY.toString() + bottomNavigationView.selectedItemId.toString())
        outState.putInt(SELECTED_ITEM_ID_KEY, bottomNavigationView.selectedItemId)
        outState.putString(CHOSEN_TAG, SELECTED_ITEM_ID_KEY)
        Log.i("saving", outState.getInt(SELECTED_ITEM_ID_KEY).toString())
        Log.i("saving", outState.getString(CHOSEN_TAG).toString())
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState)
        SELECTED_ITEM_ID_KEY = savedInstanceState.getString(CHOSEN_TAG)
        Log.i("RESTORE", SELECTED_ITEM_ID_KEY!!)
    }



    companion object {
        const val HOME_TAG = "home"
        const val ARTICLES_TAG = "articles"
        const val BOOKS_TAG = "books"
        const val SETTINGS_TAG = "settings"
        const val CHOSEN_TAG = "restore"
    }
}