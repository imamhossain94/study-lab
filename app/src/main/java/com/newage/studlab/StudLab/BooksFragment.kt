package com.newage.studlab.StudLab

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.newage.studlab.Adapter.BookRecyclerViewAdapter
import com.newage.studlab.Application.StudLab
import com.newage.studlab.Application.StudLab.Companion.appContext
import com.newage.studlab.Application.StudLab.Companion.currentUser
import com.newage.studlab.Model.StudLibrary.Book
import com.newage.studlab.Model.TokenModel.DeviceToken
import com.newage.studlab.R
import com.newage.studlab.Services.FcmNotificationService.*
import es.dmoral.toasty.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class BooksFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_books, container, false)
    }

    private lateinit var apiService: APIService

    val booksList = ArrayList<Book>()
    var filteredList = ArrayList<Book>()

    lateinit var recyclerView:RecyclerView
    lateinit var adapter: BookRecyclerViewAdapter

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        apiService = Client.getClient("https://fcm.googleapis.com/")!!.create(APIService::class.java)

        activity!!.let {

            recyclerView = it.findViewById(R.id.book_recycler_view)

        }
        recyclerView.layoutManager = LinearLayoutManager(context)

        if(booksList.isNotEmpty()){
            filteredList.clear()
            filteredList = booksList.filter { it.proCode == StudLabActivity.program && it.semName == StudLabActivity.semesterName && it.courCode == StudLabActivity.coursCode}  as ArrayList<Book>
            adapter = BookRecyclerViewAdapter(StudLab.appContext, filteredList,
                downloadClick = {
                    downloadBook(it)
                }, reactClick = {
                    reactBook(it)
                })
        }else{
            adapter = BookRecyclerViewAdapter(StudLab.appContext, booksList,
                downloadClick = {
                    downloadBook(it)
                }, reactClick = {
                    reactBook(it)
                })
        }

        recyclerView.adapter = adapter
        loadBook()
    }



    private fun loadBook(){
        val ref = FirebaseDatabase.getInstance().getReference("/Library/Book")
        ref.keepSynced(true)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                booksList.clear()
                var books: Book? = null
                for (productSnapshot in dataSnapshot.children) {
                    books = productSnapshot.getValue(Book::class.java)
                    booksList.add(books!!)
                }

                if(booksList.isNotEmpty()){
                    filteredList.clear()
                    filteredList = booksList.filter { it.proCode == StudLabActivity.program && it.semName == StudLabActivity.semesterName && it.courCode == StudLabActivity.coursCode}  as ArrayList<Book>
                    adapter.filterBookList(filteredList)
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                throw databaseError.toException()
            }
        })
    }

    @SuppressLint("DefaultLocale")
    private fun downloadBook(book:Book){
        val uri: Uri = Uri.parse(book.bookPdfFile)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)

        val updBook = Book(book.proCode,book.semName,book.courCode,book.bookTitleEdition,
            book.bookRating,book.bookWriterName,book.bookDescription,
            (book.bookTotalDownload.toInt() + 1 ).toString(), (book.bookTotalLoved.toInt() + 1 ).toString(), book.bookLovedBY + " " + currentUser!!.user_id,
            book.bookCoverImage,book.bookPdfFile,book.bookPdfSize)

        val filename = "${book.proCode} ${book.semName} ${book.courCode} ${book.bookTitleEdition}".toLowerCase()
        val ref = FirebaseDatabase.getInstance().getReference("/Library/Book/$filename")

        ref.setValue(updBook)
            .addOnSuccessListener {
                Toasty.success(appContext, "Downloading...", Toast.LENGTH_SHORT, true).show()
            }
            .addOnFailureListener{
                Toasty.error(appContext, "Failed", Toast.LENGTH_SHORT, true).show()
            }
    }

    @SuppressLint("DefaultLocale")
    private fun reactBook(book:Book){

        val updBook: Book?

        if(!book.bookLovedBY.contains(currentUser!!.user_id)){
                updBook = Book(book.proCode,book.semName,book.courCode,book.bookTitleEdition,
                book.bookRating,book.bookWriterName,book.bookDescription,
                book.bookTotalDownload, (book.bookTotalLoved.toInt() + 1).toString(), book.bookLovedBY + " " + currentUser!!.user_id,
                book.bookCoverImage,book.bookPdfFile,book.bookPdfSize)
        }else{
             updBook = Book(book.proCode,book.semName,book.courCode,book.bookTitleEdition,
                book.bookRating,book.bookWriterName,book.bookDescription,
                book.bookTotalDownload, (book.bookTotalLoved.toInt() - 1).toString(), book.bookLovedBY.replace(" " +currentUser!!.user_id,"") ,
                book.bookCoverImage,book.bookPdfFile,book.bookPdfSize)
            //Toasty.error(appContext, "You already reacted on it.", Toast.LENGTH_SHORT, true).show()
        }

        val filename = "${book.proCode} ${book.semName} ${book.courCode} ${book.bookTitleEdition}".toLowerCase()
        val ref = FirebaseDatabase.getInstance().getReference("/Library/Book/$filename")

        ref.setValue(updBook)
            .addOnSuccessListener {

                createNotification("Library: ${updBook.courCode} ❤","${updBook.bookLovedBY} reacted for the book ${updBook.bookTitleEdition} by ${updBook.bookWriterName} which was uploaded by you", book.bookDescription)

            }
            .addOnFailureListener{

                Toasty.error(appContext, "Failed", Toast.LENGTH_SHORT, true).show()

            }
    }


    //Send notification ----------------------------------------------------------------------------
    private fun createNotification(title:String, message: String, uid:String){

        val ref = FirebaseDatabase.getInstance().getReference("/Tokens")
        ref.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists()){

                    var tokens: DeviceToken?
                    for (productSnapshot in dataSnapshot.children) {

                        tokens = productSnapshot.getValue(DeviceToken::class.java)

                        if(uid.contains(tokens!!.userId)){
                            sendNotifications(tokens.tokenId,title,message)
                        }


                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                throw (databaseError.toException() as Throwable?)!!
            }
        })
    }


    private fun sendNotifications(userToken: String, title: String, message: String) {
        val data = Data(title, message)
        val sender = NotificationSender(data, userToken)

        apiService.sendNotifcation(sender).enqueue(object : Callback<MyResponse> {
            override fun onResponse(call: Call<MyResponse>, response: Response<MyResponse>) {
                if (response.code() == 200) {
                    if (response.body()!!.success != 1) {
                        Toast.makeText(requireContext(), "Failed ", Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<MyResponse>, t: Throwable) {
            }
        })
    }




}