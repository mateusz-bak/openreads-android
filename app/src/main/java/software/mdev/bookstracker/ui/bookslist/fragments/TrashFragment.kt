package software.mdev.bookstracker.ui.bookslist.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_trash.*
import software.mdev.bookstracker.R
import software.mdev.bookstracker.data.db.BooksDatabase
import software.mdev.bookstracker.data.repositories.BooksRepository
import software.mdev.bookstracker.ui.bookslist.ListActivity
import software.mdev.bookstracker.ui.bookslist.viewmodel.BooksViewModel
import software.mdev.bookstracker.ui.bookslist.viewmodel.BooksViewModelProviderFactory
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper
import software.mdev.bookstracker.adapters.DeletedBookAdapter
import software.mdev.bookstracker.data.db.LanguageDatabase
import software.mdev.bookstracker.data.db.YearDatabase
import software.mdev.bookstracker.data.repositories.LanguageRepository
import software.mdev.bookstracker.data.repositories.OpenLibraryRepository
import software.mdev.bookstracker.data.repositories.YearRepository
import software.mdev.bookstracker.other.Constants
import java.text.SimpleDateFormat
import java.util.*


class TrashFragment : Fragment(R.layout.fragment_trash) {

    lateinit var viewModel: BooksViewModel
    val currentFragment = Constants.BOOK_STATUS_IN_PROGRESS

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as ListActivity).booksViewModel

        var sharedPreferencesName = (activity as ListActivity).getString(R.string.shared_preferences_name)
        val sharedPref = (activity as ListActivity).getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()

        view.hideKeyboard()

        val database = BooksDatabase(view.context)
        val yearDatabase = YearDatabase(view.context)
        val languageDatabase = LanguageDatabase(view.context)

        val booksRepository = BooksRepository(database)
        val yearRepository = YearRepository(yearDatabase)
        val openLibraryRepository = OpenLibraryRepository()
        val languageRepository = LanguageRepository(languageDatabase)

        val booksViewModelProviderFactory = BooksViewModelProviderFactory(
            booksRepository,
            yearRepository,
            openLibraryRepository,
            languageRepository
        )

        viewModel = ViewModelProvider(this, booksViewModelProviderFactory).get(
            BooksViewModel::class.java
        )

        val viewModel = ViewModelProviders.of(this, booksViewModelProviderFactory).get(BooksViewModel::class.java)

        val deletedBookAdapter = DeletedBookAdapter(view.context, whichFragment = currentFragment, viewModel)

        rvBooks.adapter = deletedBookAdapter
        rvBooks.layoutManager = LinearLayoutManager(view.context)

        // bounce effect on the recyclerview
        OverScrollDecoratorHelper.setUpOverScroll(
            rvBooks,
            OverScrollDecoratorHelper.ORIENTATION_VERTICAL
        )

        this.getBooks(deletedBookAdapter)
    }

    fun View.hideKeyboard() {
        val inputManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }

    fun View.showKeyboard() {
        val inputManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.toggleSoftInputFromWindow(windowToken, 0, 0)
    }

    fun getBooks(deletedBookAdapter: DeletedBookAdapter) {
        viewModel.getDeletedBooks().observe(
            viewLifecycleOwner,
            Observer { some_books -> deletedBookAdapter.differ.submitList(some_books) })
    }
}