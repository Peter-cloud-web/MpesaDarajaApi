package com.example.mpesadarajaapi

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment

class ProgressDialogFragment:DialogFragment() {

    private lateinit var title: String
    private lateinit var message: String

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val tvTitle = view?.findViewById<TextView>(R.id.tvProgress_title)
        val tvMessage = view?.findViewById<TextView>(R.id.tvProgress_message)

        title = arguments?.getString(ARG_TITLE) ?: ""
        message = arguments?.getString(ARG_MESSAGE) ?: ""

        if (tvTitle != null) {
            tvTitle.text = title
        }
        if (tvMessage != null) {
            tvMessage.text = message
        }

        return view
    }

    companion object {
        private const val ARG_TITLE = "title"
        private const val ARG_MESSAGE = "message"

        fun newInstance(title: String, message: String): ProgressDialogFragment {
            val fragment = ProgressDialogFragment()
            val args = Bundle()
            args.putString(ARG_TITLE, title)
            args.putString(ARG_MESSAGE, message)
            fragment.arguments = args
            return fragment
        }
    }
}