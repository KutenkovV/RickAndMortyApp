package com.dmp.simplemorty.network

import com.dmp.simplemorty.domain.models.Character

object SimpleMortyCache {

    val characterMap = mutableMapOf<Int, Character>()
    var charr = mutableMapOf<Int, Character>()
}