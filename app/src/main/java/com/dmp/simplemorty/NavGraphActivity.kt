package com.dmp.simplemorty

import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import androidx.navigation.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.dmp.simplemorty.characters.CharactersRepository
import com.dmp.simplemorty.characters.detail.CharacterDetailFragmentArgs
import com.dmp.simplemorty.domain.models.Character
import com.dmp.simplemorty.network.SimpleMortyCache
import com.google.android.material.navigation.NavigationView
import org.json.JSONObject
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.Writer

class NavGraphActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nav_graph)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)

        appBarConfiguration = AppBarConfiguration(
            topLevelDestinationIds = setOf(
                R.id.characterListFragment,
                R.id.characterSearchFragment,
                R.id.episodeListFragment
            ),
            drawerLayout = drawerLayout
        )
        setupActionBarWithNavController(
            navController = navController,
            configuration = appBarConfiguration
        )

        findViewById<NavigationView>(R.id.nav_view).apply {
            setupWithNavController(navController)
            setCheckedItem(navController.graph.startDestination)
        }
    }

    private val safeArgs: CharacterDetailFragmentArgs by navArgs()

      fun action(view: View) {
        //var character = CharacterDetailViewModel().characterByIdLiveData
        //var character = CharacterDetailFragment().characterr
          parseJson()
    }

    private fun parseJson() {
        var character = SimpleMortyCache.charr
//        var character = SimpleMortyCache.characterMap()
        var json = JSONObject()
        json.put("Character", addCharacter(character))

       saveJson(json.toString())
    }

    private fun addCharacter(character: MutableMap<Int, Character>): JSONObject {
        return JSONObject()
            .put("image", character.values)
    }

    private fun saveJson(jsonStrings: String) {
        Toast.makeText(this, "Сообщение: $jsonStrings", Toast.LENGTH_SHORT).show()
        val output: Writer
        val file = createFile()
        output = BufferedWriter(FileWriter(file))
        output.write(jsonStrings)
        output.close()
    }

    private fun createFile(): File {

        val fileName = "myJson"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        if (storageDir != null) {
            if(!storageDir.exists()) {
                storageDir.mkdir()
            }
        }
        return File.createTempFile(
            fileName,
            ".json",
            storageDir
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(this, R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}