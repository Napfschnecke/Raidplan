package com.raidplan

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
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
import com.raidplan.ui.*
import com.raidplan.util.PlanGlide
import com.raidplan.util.ScreenshotExporter
import io.realm.Realm
import io.realm.RealmConfiguration

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    var user: User? = null
    var boss: String? = null
    private var menu: Menu? = null

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
        navView.menu.getItem(0).isChecked = true

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
        when {
            user == null -> {
                binding.toolbar.title = resources.getString(R.string.authorize)
                supportFragmentManager.beginTransaction()
                    .replace(R.id.host_fragment, AuthFragmentMvrx(), "auth").commit()
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

            }
            user?.mainChar != null -> {
                updateToolbar(resources.getString(R.string.guild), "${user?.mainChar?.guild}")
                supportFragmentManager.beginTransaction()
                    .replace(R.id.host_fragment, GuildFragmentMvrx(), "guild").commit()
            }
            else -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.host_fragment, CharPickerFragment(), "chars").commit()
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menu = menu
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onResume() {
        super.onResume()
        val uri = intent.data
        if (user == null) {
            uri?.let {
                if (it.toString().startsWith(ApiData.REDIRECT_URI)) {
                    val code = it.getQueryParameter("code")
                    if (code != null) {
                        Realm.getDefaultInstance().use { realm ->
                            realm.executeTransactionAsync { bgRealm ->
                                bgRealm.where(OauthStrings::class.java).findFirst()
                                    ?.let { strings ->
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
        val currentFragment = supportFragmentManager.findFragmentById(R.id.host_fragment)
        currentFragment?.let {
            when (it::class) {
                RaidPosMvrx::class -> {
                    updateToolbar(
                        resources.getString(R.string.app_name),
                        "$boss",
                        true
                    )
                    navView.menu.getItem(2).isChecked = true
                }
                GuildFragmentMvrx::class -> {
                    updateToolbar(
                        resources.getString(R.string.guild),
                        "${user?.mainChar?.guild}"
                    )
                    navView.menu.getItem(0).isChecked = true
                }
            }
        }
    }

    override fun onBackPressed() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.host_fragment)
        currentFragment?.let {
            when (it::class) {
                RaidPosMvrx::class -> {
                    supportFragmentManager.beginTransaction().apply {
                        setCustomAnimations(
                            R.anim.enter_from_left,
                            R.anim.exit_to_right,
                            R.anim.enter_from_left,
                            R.anim.exit_to_right
                        ).replace(
                            R.id.host_fragment,
                            BossPickerFragment(),
                            "boss"
                        ).commit()
                    }

                    updateToolbar(
                        resources.getString(R.string.bosses),
                        resources.getString(R.string.pick_boss)
                    )
                }
                else -> drawerLayout.openDrawer(GravityCompat.START)
            }
        }
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

    fun openRaidPositioner(boss: String) {
        updateToolbar(
            resources.getString(R.string.app_name),
            boss,
            showExport = true,
            switchNav = true
        )
        supportFragmentManager.beginTransaction().apply {
            setCustomAnimations(
                R.anim.enter_from_right,
                R.anim.exit_to_left,
                R.anim.enter_from_left,
                R.anim.exit_to_right
            ).replace(R.id.host_fragment, RaidPosMvrx.newInstance(boss), "raidPos")
                .commit()
        }
    }

    fun openFragment(item: MenuItem) {
        when (item.title) {
            resources.getString(R.string.nav_guild) -> {
                supportFragmentManager.beginTransaction().apply {
                    setCustomAnimations(
                        R.anim.enter_from_right,
                        R.anim.exit_to_left,
                        R.anim.enter_from_left,
                        R.anim.exit_to_right
                    ).replace(R.id.host_fragment, GuildFragmentMvrx(), "guild")
                        .commit()
                }
                updateToolbar(resources.getString(R.string.guild), "${user?.mainChar?.guild}")
            }
            resources.getString(R.string.nav_roster) -> {

            }
            resources.getString(R.string.nav_raid) -> {
                supportFragmentManager.beginTransaction().apply {
                    setCustomAnimations(
                        R.anim.enter_from_right,
                        R.anim.exit_to_left,
                        R.anim.enter_from_left,
                        R.anim.exit_to_right
                    ).replace(R.id.host_fragment, BossPickerFragment(), "boss")
                        .commit()
                }
                updateToolbar(
                    resources.getString(R.string.bosses),
                    resources.getString(R.string.pick_boss)
                )
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
    }

    fun updateToolbar(
        title: String = "",
        subtitle: String = "",
        showExport: Boolean = false,
        switchNav: Boolean = false
    ) {
        runOnUiThread {
            menu?.findItem(R.id.action_export)?.isVisible = showExport
            binding.toolbar.title = title
            binding.toolbar.subtitle = subtitle
            if (switchNav) {
                binding.toolbar.navigationIcon = ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.ic_baseline_arrow_back_24,
                    theme
                )
            } else {
                binding.toolbar.navigationIcon = ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.ic_menu,
                    theme
                )
            }
        }
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
            Classes.getClassById("${char.playerClass}")
        val charView = header.findViewById<ImageView>(R.id.charAsset)

        PlanGlide.with(this)
            .load(char.rawUrl)
            .error(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.melee_blob_selected,
                    theme
                )
            )
            .into(charView)
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

    fun exportScreen(item: MenuItem) {
        val permission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        val permitted = (permission == PackageManager.PERMISSION_GRANTED)
        if (permitted) {
            /*
        ScreenshotExporter().store(
            ScreenshotExporter().getScreenShot(binding.hostFragment.findViewById(R.id.zoomcont)),
            "plan.png",
            this,
            "${this.boss}"
        )
         */
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 69)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 69) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                println("permission manually granted")
                ScreenshotExporter().store(
                    ScreenshotExporter().getScreenShot(binding.hostFragment.findViewById(R.id.zoomcont)),
                    "plan.png",
                    this,
                    "${this.boss}"
                )
            } else {
                println("unlucky")
            }
        }
    }
}