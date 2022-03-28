package com.codepath.nytimes.ui.search

import android.content.Context
import com.codepath.nytimes.networking.NYTimesApiClient
import androidx.recyclerview.widget.RecyclerView
import androidx.core.widget.ContentLoadingProgressBar
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import com.codepath.nytimes.R
import androidx.recyclerview.widget.LinearLayoutManager
import com.codepath.nytimes.networking.CallbackResponse
import com.codepath.nytimes.models.Article
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment

/**
 * A fragment representing a list of Items.
 *
 *
 * interface.
 */
class ArticleResultFragment
/**
 * Mandatory empty constructor for the fragment manager to instantiate the
 * fragment (e.g. upon screen orientation changes).
 */
    : Fragment() {
    private val client = NYTimesApiClient()
    private var recyclerView: RecyclerView? = null
    private var progressSpinner: ContentLoadingProgressBar? = null
    private var savedQuery: String? = null
    private var scrollListener: EndlessRecyclerViewScrollListener? = null
    var adapter = MyArticleResultRecyclerViewAdapter()

    override fun onPrepareOptionsMenu(menu: Menu) {
        val item = menu.findItem(R.id.action_search).actionView as SearchView;
        item.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                item.clearFocus()
                if (query != null) {
                    loadNewArticlesByQuery(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    loadNewArticlesByQuery(newText)
                }
                return false
            }

        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_article_result_list, container, false)
        val localRecyclerView = view.findViewById<RecyclerView>(R.id.list)
        recyclerView = localRecyclerView
        progressSpinner = view.findViewById(R.id.progress)
        // Set the adapter
        val context = view.context
        val linearLayoutManager = LinearLayoutManager(context)
        localRecyclerView.layoutManager = linearLayoutManager
        localRecyclerView.adapter = adapter
        scrollListener = object : EndlessRecyclerViewScrollListener(linearLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadNextDataFromApi(page)
            }
        }
        localRecyclerView.addOnScrollListener(scrollListener!!)
        activity!!.title = getString(R.string.action_bar_search)
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
    }

    private fun loadNewArticlesByQuery(query: String) {
        Log.d("ArticleResultFragment", "load article with query $query")
        progressSpinner?.show()
        savedQuery = query
        scrollListener?.resetState()
        client.getArticlesByQuery(object : CallbackResponse<List<Article>> {
            override fun onSuccess(model: List<Article>) {
                val adapter = recyclerView?.adapter as MyArticleResultRecyclerViewAdapter
                adapter.setNewArticles(model)
                adapter.notifyDataSetChanged()
                Log.d("ArticleResultFragment", "successfully loaded articles")
                progressSpinner?.hide()
            }

            override fun onFailure(error: Throwable?) {
                Log.d("ArticleResultFragment", "failure load article " + error!!.message)
                progressSpinner?.hide()
            }
        }, query)
    }

    private fun loadNextDataFromApi(page: Int) {
        client.getArticlesByQuery(object : CallbackResponse<List<Article>> {
            override fun onSuccess(models: List<Article>) {
                val adapter = recyclerView?.adapter as MyArticleResultRecyclerViewAdapter
                adapter.addArticles(models)
                adapter.notifyDataSetChanged()
                Log.d("ArticleResultFragment", String.format("successfully loaded articles from page %d", page))
            }

            override fun onFailure(error: Throwable?) {
                Log.d("ArticleResultFragment", "failure load article " + error!!.message)
                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
            }
        }, savedQuery, page)
    }

    companion object {
        fun newInstance(): ArticleResultFragment {
            return ArticleResultFragment()
        }
    }
}