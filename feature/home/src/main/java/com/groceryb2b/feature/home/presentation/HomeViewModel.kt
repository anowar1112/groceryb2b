package com.groceryb2b.feature.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.groceryb2b.core.database.catalog.CategoryEntity
import com.groceryb2b.core.database.catalog.ProductEntity
import com.groceryb2b.feature.home.data.CatalogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(val query: String = "", val selectedCategoryId: String? = null, val categories: List<CategoryEntity> = emptyList(), val products: List<ProductEntity> = emptyList(), val quantities: Map<Long, Int> = emptyMap())

@HiltViewModel
class HomeViewModel @Inject constructor(repository: CatalogRepository) : ViewModel() {
    private val query = MutableStateFlow("")
    private val selectedCategoryId = MutableStateFlow<String?>(null)
    private val quantities = MutableStateFlow<Map<Long, Int>>(emptyMap())
    val uiState: StateFlow<HomeUiState> = combine(repository.categories(), repository.products(), query, selectedCategoryId, quantities) { categories, products, search, category, qty ->
        val normalized = search.trim().lowercase()
        HomeUiState(search, category, categories, products.filter { product -> (category == null || product.categoryId == category) && (normalized.isBlank() || product.nameBn.lowercase().contains(normalized) || product.nameEn.lowercase().contains(normalized) || product.brand.lowercase().contains(normalized)) }, qty)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())
    init { viewModelScope.launch { repository.ensureSeedData() } }
    fun updateQuery(value: String) { query.value = value }
    fun selectCategory(id: String?) { selectedCategoryId.value = id }
    fun changeQuantity(id: Long, change: Int) { quantities.value = quantities.value.toMutableMap().apply { val next = (get(id) ?: 0) + change; if (next <= 0) remove(id) else put(id, next) } }
}
