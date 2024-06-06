package com.tlh.photogallery

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tlh.photogallery.databinding.FragmentRegisterBinding


class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth : FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.signInButton.setOnClickListener { signInClicked(it) }
        binding.registerButton.setOnClickListener { signUpClicked(it) }
        val currentUser = auth.currentUser

        if (currentUser != null) {
            val action = RegisterFragmentDirections.actionRegisterFragmentToFeedFragment()
            Navigation.findNavController(requireView()).navigate(action)
        }
    }

    fun signInClicked(view: View) {
        val userEmail = binding.emailText.text.toString()
        val password = binding.passwordText.text.toString()

        if (userEmail.isNotEmpty() && password.isNotEmpty()) {
            auth.signInWithEmailAndPassword(userEmail,password).addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    //Signed In
                    Toast.makeText(requireContext(),"Welcome: ${auth.currentUser?.email.toString()}",Toast.LENGTH_LONG).show()
                    val action = RegisterFragmentDirections.actionRegisterFragmentToFeedFragment()
                    Navigation.findNavController(requireView()).navigate(action)
                }

            }.addOnFailureListener { exception ->
                Toast.makeText(requireContext(),exception.localizedMessage,Toast.LENGTH_LONG).show()
            }
        }

    }

    fun signUpClicked(view: View) {
        val userEmail = binding.emailText.text.toString()
        val password = binding.passwordText.text.toString()

        if (userEmail.isNotEmpty() && password.isNotEmpty()) {
            auth.createUserWithEmailAndPassword(userEmail,password).addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    val action = RegisterFragmentDirections.actionRegisterFragmentToFeedFragment()
                    Navigation.findNavController(requireView()).navigate(action)
                }

            }.addOnFailureListener { exception ->
                Toast.makeText(requireContext(),exception.localizedMessage,Toast.LENGTH_LONG).show()

            }
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}