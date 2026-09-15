package com.peopleentertained.app

import android.app.Activity
import android.os.Bundle
import android.widget.*
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class MainActivity : Activity() {
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseFirestore.getInstance() }
    private val storage by lazy { FirebaseStorage.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showLogin()
    }

    private fun showLogin() {
        val box = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(40,60,40,40) }
        val title = TextView(this).apply { text = "People Entertained"; textSize = 28f }
        val email = EditText(this).apply { hint = "Email" }
        val pass = EditText(this).apply { hint = "Password"; inputType = 0x81 }
        val login = Button(this).apply { text = "Login" }
        val signup = Button(this).apply { text = "Create account" }
        box.addView(title); box.addView(email); box.addView(pass); box.addView(login); box.addView(signup)
        login.setOnClickListener {
            auth.signInWithEmailAndPassword(email.text.toString(), pass.text.toString())
                .addOnSuccessListener { showHome() }
                .addOnFailureListener { toast(it.message ?: "Login failed") }
        }
        signup.setOnClickListener {
            auth.createUserWithEmailAndPassword(email.text.toString(), pass.text.toString())
                .addOnSuccessListener { user ->
                    db.collection("users").document(user.user!!.uid).set(mapOf("email" to email.text.toString(), "role" to "user"))
                    showHome()
                }.addOnFailureListener { toast(it.message ?: "Signup failed") }
        }
        setContentView(box)
    }

    private fun showHome() {
        val box = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(24,40,24,24) }
        val title = TextView(this).apply { text = "People Entertained"; textSize = 26f }
        val upload = Button(this).apply { text = "Upload video" }
        val logout = Button(this).apply { text = "Logout" }
        val list = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        box.addView(title); box.addView(upload); box.addView(logout); box.addView(list)
        db.collection("videos").whereEqualTo("status","approved").get().addOnSuccessListener { snap ->
            for (doc in snap.documents) {
                val t = TextView(this).apply {
                    text = "${doc.getString("title") ?: "Video"}\n${doc.getString("videoUrl") ?: ""}"
                    textSize = 17f; setPadding(0,18,0,18)
                }
                list.addView(t)
            }
        }
        upload.setOnClickListener { pickAndUpload() }
        logout.setOnClickListener { auth.signOut(); showLogin() }
        setContentView(ScrollView(this).apply { addView(box) })
    }

    private fun pickAndUpload() {
        val i = android.content.Intent(android.content.Intent.ACTION_GET_CONTENT).apply { type = "video/*" }
        startActivityForResult(i, 77)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: android.content.Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != 77 || resultCode != RESULT_OK || data?.data == null) return
        val uri = data.data!!
        val uid = auth.currentUser?.uid ?: return
        val ref = storage.reference.child("videos/$uid/${System.currentTimeMillis()}.mp4")
        ref.putFile(uri).continueWithTask { ref.downloadUrl }.addOnSuccessListener { url ->
            db.collection("videos").add(mapOf(
                "title" to "New video",
                "videoUrl" to url.toString(),
                "ownerId" to uid,
                "status" to "pending",
                "createdAt" to System.currentTimeMillis()
            )).addOnSuccessListener { toast("Uploaded. Waiting for admin approval."); showHome() }
        }.addOnFailureListener { toast(it.message ?: "Upload failed") }
    }

    private fun toast(s: String) = Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
}
