package com.example.mybooks.ui.bookslist

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mybooks.other.ListElementAdapter
import com.example.mybooks.R
import com.example.mybooks.data.db.BooksDatabase
import com.example.mybooks.data.repositories.BooksRepository
import kotlinx.android.synthetic.main.activity_list.*

class ListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        val database = BooksDatabase(this)
        val repository = BooksRepository(database)
        val factory = BooksViewModelFactory(repository)

        val viewModel = ViewModelProviders.of(this, factory).get(BooksViewModel::class.java)

        val adapter = ListElementAdapter(listOf(), viewModel)

        rvListOfBooks.adapter = adapter
        rvListOfBooks.layoutManager = LinearLayoutManager(this)

        viewModel.getAllListElements().observe(this, Observer {
            adapter.books = it
            adapter.notifyDataSetChanged()
        })

        fabGoToAdder.setOnClickListener {
            Intent(this, AdderActivity::class.java).also {
                startActivity(it)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.app_bar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.miAbout -> Toast.makeText(this, "About is work in progress", Toast.LENGTH_SHORT).show()
            R.id.miSettings -> Toast.makeText(this, "Settings are work in progress", Toast.LENGTH_SHORT).show()
        }
        return true
    }
}