package com.example.pomopet

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.pomopet.databinding.ActivityTitleScreenBinding
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.types.Track

/* This is where the whole application starts */
class MainActivity : AppCompatActivity() {
    private lateinit var pomoDBHelper: PomoDBHelper

    private val clientId = "13d550e769df4e39bdcfcd98d003800a"
    private val redirectUri = "com.example.pomopet://callback"
    private var spotifyAppRemote: SpotifyAppRemote? = null

    private fun hideSystemBars() {
        val controller = WindowInsetsControllerCompat(
            window, window.decorView
        )

        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        // Start application by going to the Title Screen
        // ViewBind activity_title_screen.xml
        val actTitleScreenBind: ActivityTitleScreenBinding = ActivityTitleScreenBinding.inflate(layoutInflater)


        // Change the scene
        setContentView(actTitleScreenBind.root)

        // Change to Register Screen if user tapped anywhere in the screen
        actTitleScreenBind.layoutTitleScreenCl.setOnClickListener{

            pomoDBHelper = PomoDBHelper.getInstance(this@MainActivity)!!

            val hasRegistered = pomoDBHelper.getPet().isNotEmpty()

            // If new user, change to register screen. Else, go to activity main directly.
            if (!hasRegistered)
            {
                // Make intent of the next activity (Make sure to also include the intent in AndroidManifext.xml
                val registerIntent = Intent(applicationContext, RegisterActivity::class.java)
                // Go to next activity
                this.startActivity(registerIntent)
            }

            // existing user
            else {
                val mainPetScreenIntent = Intent(applicationContext, PetScreenActivity::class.java)
                this.startActivity(mainPetScreenIntent);
            }

            // Destroys this activity
            this.finish()
        }
    }

    override fun onStart() {
        super.onStart()

        Log.d("MainActivity", "Connected! Yay!")

        val connectionParams = ConnectionParams.Builder(clientId)
            .setRedirectUri(redirectUri)
            .showAuthView(true)
            .build()


        SpotifyAppRemote.connect(this, connectionParams, object : Connector.ConnectionListener {
            override fun onConnected(appRemote: SpotifyAppRemote) {
                spotifyAppRemote = appRemote
                Log.d("MainActivity", "Connected! Yay!")
                // Now you can start interacting with App Remote
                connected()
            }

            override fun onFailure(throwable: Throwable) {
                Log.e("MainActivity", throwable.message, throwable)
            }
        })
    }


    private fun connected() {
        spotifyAppRemote?.let {
            // Play a playlist
            val playlistURI = "spotify:playlist:37i9dQZF1DX2sUQwD7tbmL"
            it.playerApi.play(playlistURI)
            // Subscribe to PlayerState
            it.playerApi.subscribeToPlayerState().setEventCallback {
                val track: Track = it.track
                Log.d("MainActivity", track.name + " by " + track.artist.name)
            }
        }

    }

    override fun onResume() {
        super.onResume()
        hideSystemBars()
    }

}