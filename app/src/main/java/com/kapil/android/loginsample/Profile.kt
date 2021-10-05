package com.kapil.android.loginsample

import android.app.Activity
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.kapil.android.loginsample.databinding.FragmentHomeBinding
import com.kapil.android.loginsample.databinding.FragmentProfileBinding
import com.kapil.android.loginsample.networkmanager.NetworkManager
import com.squareup.picasso.Picasso

class Profile : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var mAuth : FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val binding = DataBindingUtil.inflate<FragmentProfileBinding>(inflater, R.layout.fragment_profile, container, false)

        mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser
        activity?.title ="Profile"

        if (NetworkManager().checkConnectivity(activity as Context)) {

            if (user != null) {

                binding.userid.text = user?.displayName
                binding.userEmail.text = user?.email
                Picasso.get().load(user?.photoUrl).into(binding.imgProfile)

                binding.btnLogout.setOnClickListener {
                    AuthUI.getInstance().signOut(activity as Context)
                    requireActivity().finishAffinity()
                    startActivity(Intent(activity as Context, MainActivity::class.java))
                }
            }else{
                startActivity(Intent(activity as Context, MainActivity::class.java))
            }
        }else{
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet connection not found")
            dialog.setPositiveButton("ok"){ text, lister ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.setNegativeButton("Open Settings"){text, listner ->
                val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(intent)
                activity?.finish()
            }
            dialog.create()
            dialog.show()
        }
        return binding.root
    }
}