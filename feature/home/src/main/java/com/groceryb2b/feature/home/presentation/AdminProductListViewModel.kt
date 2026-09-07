package com.groceryb2b.feature.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.groceryb2b.core.database.catalog.CategoryDao
import com.groceryb2b.core.database.catalog.CategoryEntity
import com.groceryb2b.core.database.catalog.ProductDao
import com.groceryb2b.core.database.catalog.ProductEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminProductListUiState(
    val products: List<ProductEntity> = emptyList(),
    val categories: List<CategoryEntity> = emptyList(),
    val isLoading: Boolean = true,
    val searchQuery: String = "",
    val selectedCategoryId: String? = null
)

@HiltViewModel
class AdminProductListViewModel @Inject constructor(
    private val productDao: ProductDao,
    private val categoryDao: CategoryDao
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _selectedCategoryId = MutableStateFlow<String?>(null)

    val uiState: StateFlow<AdminProductListUiState> = combine(
        productDao.observeAll(),
        categoryDao.observeAll(),
        _searchQuery,
        _selectedCategoryId
    ) { products, categories, query, categoryId ->
        val filteredProducts = products.filter { product ->
            val matchesQuery = query.isBlank() || 
                product.nameBn.contains(query, ignoreCase = true) || 
                product.nameEn.contains(query, ignoreCase = true) ||
                product.brand.contains(query, ignoreCase = true)
            
            val matchesCategory = categoryId == null || product.categoryId == categoryId
            
            matchesQuery && matchesCategory
        }

        AdminProductListUiState(
            products = filteredProducts,
            categories = categories,
            isLoading = false,
            searchQuery = query,
            selectedCategoryId = categoryId
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AdminProductListUiState())

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateCategoryFilter(categoryId: String?) {
        _selectedCategoryId.value = categoryId
    }

    fun deleteProduct(id: Long) = viewModelScope.launch {
        productDao.deleteById(id)
    }
}
