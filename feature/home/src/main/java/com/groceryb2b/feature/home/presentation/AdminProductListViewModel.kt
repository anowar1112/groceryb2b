package com.groceryb2b.feature.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.groceryb2b.core.database.catalog.ProductDao
import com.groceryb2b.core.database.catalog.ProductEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminProductListViewModel @Inject constructor(
    private val productDao: ProductDao
) : ViewModel() {
    val products: StateFlow<List<ProductEntity>> = productDao.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun deleteProduct(id: Long) = viewModelScope.launch {
        productDao.deleteById(id)
    }
}
