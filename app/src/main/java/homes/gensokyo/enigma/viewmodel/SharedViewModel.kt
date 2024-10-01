package homes.gensokyo.enigma.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel(){
    val data = MutableLiveData<String>()
}