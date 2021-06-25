package com.raidplan

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.ui.AppBarConfiguration
import com.google.android.material.navigation.NavigationView
import com.raidplan.api.ApiData
import com.raidplan.data.Character
import com.raidplan.data.Classes
import com.raidplan.data.OauthStrings
import com.raidplan.data.User
import com.raidplan.databinding.ActivityMainBinding
import com.raidplan.ui.AuthFragmentMvrx
import com.raidplan.ui.CharPickerFragment
import com.raidplan.ui.GuildFragmentMvrx
import com.raidplan.ui.RaidPosMvrx
import io.realm.Realm
import io.realm.RealmConfiguration

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Realm.init(this)
        val realmConfig = RealmConfiguration.Builder().apply {
            allowWritesOnUiThread(true)
            allowQueriesOnUiThread(true)
        }.build()
        Realm.setDefaultConfiguration(realmConfig)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        drawerLayout = binding.drawerLayout
        navView = binding.navView

        Realm.getDefaultInstance().use { r ->
            r.executeTransaction { bgRealm ->
                val oauth = bgRealm.where(OauthStrings::class.java).findFirst()
                if (oauth == null) {
                    bgRealm.createObject(OauthStrings::class.java)
                } else {
                    ApiData.AUTH_CODE = oauth.authCode
                    ApiData.AUTH_TOKEN = oauth.authToken
                    ApiData.AUTH_EXPIRATON = oauth.authExpiration
                }
                bgRealm.where(User::class.java).findFirst()?.let { u ->
                    val unmanaged = bgRealm.copyFromRealm(u)
                    user = unmanaged
                    unmanaged.mainChar?.let { c ->
                        setupNavHeaderCharacter(c)
                    }
                }
            }
        }
        if (user == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.host_fragment, AuthFragmentMvrx(), "auth").commit()
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

        } else if (user?.mainChar != null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.host_fragment, GuildFragmentMvrx(), "guild").commit()
        } else {
            supportFragmentManager.beginTransaction()
                .replace(R.id.host_fragment, CharPickerFragment(), "chars").commit()
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onResume() {
        super.onResume()
        val uri = intent.data
        uri?.let {
            if (it.toString().startsWith(ApiData.REDIRECT_URI)) {
                val code = it.getQueryParameter("code")
                if (code != null) {
                    Realm.getDefaultInstance().use { realm ->
                        realm.executeTransactionAsync { bgRealm ->
                            bgRealm.where(OauthStrings::class.java).findFirst()?.let { strings ->
                                strings.authCode = code
                            }
                        }
                    }
                    ApiData.AUTH_CODE = code
                    ApiData.getAuthToken(this)
                }
            }
        }
    }

    override fun onBackPressed() {
        drawerLayout.openDrawer(GravityCompat.START)
    }

    fun authorize(v: View) {
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(ApiData.AUTHORIZE_URI + "oauth/authorize?client_id=" + ApiData.CLIENT_ID + "&scope=wow.profile&state=123&redirect_uri=" + ApiData.REDIRECT_URI + "&response_type=code")
        )
        startActivity(intent)
    }


    fun reAuthorize(code: Int) {

    }

    fun showMainFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.host_fragment, GuildFragmentMvrx(), "guild").commit()
        updateUserVar()
    }

    fun showCharPicker() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.host_fragment, CharPickerFragment(), "chars").commit()
    }

    fun openRaidPositioner(item: MenuItem) {
        item.isChecked = !item.isChecked
        supportFragmentManager.beginTransaction()
            .replace(R.id.host_fragment, RaidPosMvrx.newInstance("${item.title}"), "raidPos")
            .commit()
        drawerLayout.closeDrawer(GravityCompat.START)
    }

    fun setCharacter(v: View) {
        drawerLayout.closeDrawer(GravityCompat.START)
        showCharPicker()
    }

    fun setupNavHeaderCharacter(char: Character) {
        val header = navView.getHeaderView(0)
        header.findViewById<TextView>(R.id.playerName).text = char.name
        header.findViewById<TextView>(R.id.playerGuild).text = "<${char.guild}>"
        header.findViewById<TextView>(R.id.playerRealm).text = "${char.server}"
        header.findViewById<TextView>(R.id.playerDetail).text =
            "${Classes.getClassById("${char.playerClass}")}"
    }

    private fun updateUserVar() {
        Realm.getDefaultInstance().use { r ->
            r.executeTransaction { bgRealm ->
                bgRealm.where(User::class.java).findFirst()?.let { u ->
                    val unmanaged = bgRealm.copyFromRealm(u)
                    user = unmanaged
                    unmanaged.mainChar?.let { c ->
                        setupNavHeaderCharacter(c)
                    }
                }
            }
        }
        if (user?.mainChar == null) {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        } else {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)

        }
    }

}