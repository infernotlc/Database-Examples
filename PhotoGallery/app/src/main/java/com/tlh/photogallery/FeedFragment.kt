package com.tlh.photogallery

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.tlh.photogallery.adapter.FeedRecyclerAdapter
import com.tlh.photogallery.databinding.FragmentFeedBinding
import com.tlh.photogallery.databinding.FragmentRegisterBinding
import com.tlh.photogallery.model.Post


class FeedFragment : Fragment(), PopupMenu.OnMenuItemClickListener {

    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore
    var adapter : FeedRecyclerAdapter? = null
    val postArrayList : ArrayList<Post> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        db = Firebase.firestore
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.floatingActionButton.setOnClickListener { addButtonClicked(it) }
        binding.recyclerview.layoutManager = LinearLayoutManager(requireContext())
        getDataFromFirestore()

        adapter = FeedRecyclerAdapter(postArrayList)
        binding.recyclerview.adapter = adapter

    }

    fun addButtonClicked(view: View){
        val popup = PopupMenu(requireContext(), binding.floatingActionButton)
        val inflater  = popup.menuInflater
        inflater.inflate(R.menu.my_pop_menu,popup.menu)
        popup.setOnMenuItemClickListener(this)
        popup.show()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun getDataFromFirestore() {

        db.collection("Posts").orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Toast.makeText(requireContext(), exception.localizedMessage, Toast.LENGTH_LONG)
                        .show()
                } else {

                    if (snapshot != null) {
                        if (!snapshot.isEmpty) {

                            postArrayList.clear()

                            val documents = snapshot.documents
                            for (document in documents) {
                                val comment = document.get("comment") as String
                                val useremail = document.get("userEmail") as String
                                val downloadUrl = document.get("downloadUrl") as String
                                //val timestamp = document.get("date") as Timestamp
                                //val date = timestamp.toDate()

                                val post = Post(useremail, comment, downloadUrl)
                                postArrayList.add(post)
                            }
                            adapter!!.notifyDataSetChanged()

                        }
                    }

                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        if (item.itemId == R.id.uploadItem) {
            val action = FeedFragmentDirections.actionFeedFragmentToUploadFragment()
            Navigation.findNavController(requireView()).navigate(action)

        } else if (item.itemId == R.id.exitItem) {
            auth.signOut()
            val action = FeedFragmentDirections.actionFeedFragmentToRegisterFragment()
            Navigation.findNavController(requireView()).navigate(action)

        }
        return true
    }
}