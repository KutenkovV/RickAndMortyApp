package com.dmp.simplemorty.characters.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmp.simplemorty.characters.CharactersRepository
import com.dmp.simplemorty.domain.models.Character
import com.dmp.simplemorty.network.SimpleMortyCache
import kotlinx.coroutines.launch

class CharacterDetailViewModel: ViewModel() {

    //Тут получаем данные с сервера и кэшируем их
    private val repository = CharactersRepository()

    private val _characterByIdLiveData = MutableLiveData<Character?>()
    val characterByIdLiveData: LiveData<Character?> = _characterByIdLiveData //т.е. здесь лежит полученный персонаж

    fun fetchCharacter(characterId: Int) = viewModelScope.launch {
        val character = repository.getCharacterById(characterId) //Получаем из класса репозиторий нашего персонажа
        _characterByIdLiveData.postValue(character) //добавляем его в модель
    }
}