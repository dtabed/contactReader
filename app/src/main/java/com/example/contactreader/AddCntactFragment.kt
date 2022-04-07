package com.example.contactreader

import android.view.View
import android.widget.*
import androidx.fragment.app.DialogFragment
import android.os.Bundle;
import android.widget.Button;


class AddCntactFragment: DialogFragment(R.layout.fragement_add_dialog) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    val btn: Button = view.findViewById(R.id.insertBT)
     btn.setOnClickListener {
         val m1:MainActivity = getActivity() as MainActivity;
         val radioGroup:RadioGroup = view.findViewById(R.id.radioGroup)
         val selectedOption: Int = radioGroup.checkedRadioButtonId
         val nameET : EditText = view.findViewById(R.id.nameTE)
         val numberET : EditText = view.findViewById(R.id.phoneTE)
         val radioButton = view.findViewById<RadioButton>(selectedOption)
         m1.feedback(nameET.text.toString(),numberET.text.toString(),radioButton.text.toString())
         dismiss()
     }
        val canclBT: Button = view.findViewById(R.id.cancelBT)
        canclBT.setOnClickListener { dismiss() }
    }
}


