package com.example.contactreader

import android.content.ContentProviderOperation
import android.content.ContentValues
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.ListView
import android.widget.SearchView
import android.widget.SimpleCursorAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat

import androidx.core.content.ContextCompat
import android.content.ContentUris

import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemLongClickListener


class MainActivity : AppCompatActivity() {
    companion object {
        private const val READ_CONTACT_PERMISSION_CODE = 111
        private const val WRITE_CONTACT_PERMISSION_CODE = 112
    }
    var cName:String = ""
    var cNumber:String =""
    var cType:String =""


    public fun feedback(_Name:String, _Number:String, _Type: String){
        cName = _Name
        cNumber=_Number
        cType =_Type

        insertContactPhoneNumber(
            _Number ,
            _Name,_Type
        )

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkPermission(
            android.Manifest.permission.READ_CONTACTS,
            READ_CONTACT_PERMISSION_CODE)
        checkPermission(
            android.Manifest.permission.WRITE_CONTACTS,
            WRITE_CONTACT_PERMISSION_CODE)
        val lstVw: ListView = findViewById(R.id.listViewContacts)
        lstVw.onItemLongClickListener =
            OnItemLongClickListener { parent, view, position, id -> // TODO Auto-generated method stub

                Toast.makeText(this@MainActivity, "Long clicked ", Toast.LENGTH_SHORT).show()
                return@OnItemLongClickListener true
            }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu to use in the action bar
        val inflater = menuInflater
        inflater.inflate(R.menu.contacts_menu, menu)
        return true;
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var dialog_var = AddCntactFragment()
        dialog_var.show(supportFragmentManager, "Add Contact Dialog")

        return true;
    }
    // Function to check and request permission.
    private fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(this@MainActivity, permission) == PackageManager.PERMISSION_DENIED) {
            // Requesting the permission
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(permission), requestCode)
        } else {
            Toast.makeText(this@MainActivity, "Permission already granted", Toast.LENGTH_SHORT).show()
            readcontacts()
        }
    }
    // This function is called when the user accepts or decline the permission.
// Request Code is used to check which permission called this function.
// This request code is provided when the user is prompt for permission.
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        var Permissions_counter: Int = 0
        if (requestCode == READ_CONTACT_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this@MainActivity, "Read Contacts Permission Granted", Toast.LENGTH_SHORT).show()
                Permissions_counter += 1
            } else {
                Toast.makeText(this@MainActivity, "Read ContactsPermission Denied", Toast.LENGTH_SHORT).show()
            }
        }
        if (requestCode == WRITE_CONTACT_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this@MainActivity, "Write Contacts Permission Granted", Toast.LENGTH_SHORT).show()
                Permissions_counter += 1
            } else {
                Toast.makeText(this@MainActivity, "Write Contacts Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
        Toast.makeText(this@MainActivity, "Counter"+Permissions_counter, Toast.LENGTH_SHORT).show()
        if (Permissions_counter==2){
            readcontacts()
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)

    val cols = listOf<String>(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
        ContactsContract.CommonDataKinds.Phone.NUMBER,
        ContactsContract.CommonDataKinds.Phone._ID
    ).toTypedArray()
    private fun insertContactPhoneNumber(

        phoneNumber: String,
        displayName: String,
        phoneTypeStr: String
    ) {


        val operations : ArrayList<ContentProviderOperation> =  ArrayList<ContentProviderOperation>();
        operations.add(
            ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null).build()
        )

        if (displayName.trim { it <= ' ' } != "" && phoneNumber.trim { it <= ' ' } != "") {
            operations.add(
                ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(
                        ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE
                    )
                    .withValue(
                        ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                        displayName
                    ).build()
            )
            operations.add(
                ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(
                        ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
                    )
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phoneNumber)
                    .withValue(
                        ContactsContract.CommonDataKinds.Phone.TYPE,
                        phoneTypeStr
                    ).build()
            )
        }
        try {
            contentResolver.applyBatch(ContactsContract.AUTHORITY, operations)
            Toast.makeText(applicationContext, "Insert Success", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(applicationContext, "Insertion failed", Toast.LENGTH_SHORT).show()
        }
    }
    private fun getRawContactId(): Long {
        // Inser an empty contact.
        val contentValues = ContentValues()
        val rawContactUri =
            contentResolver.insert(ContactsContract.RawContacts.CONTENT_URI, contentValues)
        // Get the newly created contact raw id.
        return ContentUris.parseId(rawContactUri!!)
    }
    private fun readcontacts() {
        var from = listOf<String>(cols[0],cols[1]).toTypedArray()
        var to = intArrayOf(android.R.id.text1,android.R.id.text2)
        var rs = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI ,cols,
                                      null,
                                      null,
                                       cols[0]
        )
        var adapter = SimpleCursorAdapter(this,
                                           android.R.layout.simple_list_item_2,
                                           rs,
                                           from,
                                           to,
                                          0)
        Toast.makeText(this,"",Toast.LENGTH_LONG).show()
        val contractLV: ListView = findViewById(R.id.listViewContacts)
        contractLV.adapter = adapter
        val contractSV : SearchView = findViewById(R.id.searchViewContacts)

        contractSV.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(q: String): Boolean {

                var rs = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI ,
                                               cols,
                                              "${cols[0]} LIKE ?",
                                               Array(1){"%$q%"},
                                               cols[0])
                adapter.changeCursor(rs)

                //


                return false
            }

            override fun onQueryTextSubmit(q: String): Boolean {
                return false
            }
        })
    }
}

