package com.groceryb2b.feature.home.presentation

import androidx.lifecycle.ViewModel
import com.groceryb2b.core.database.catalog.ProductDao
import com.groceryb2b.core.database.catalog.ProductEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope
import javax.inject.Inject

data class AdminProductFormUiState(
    val categoryId: String = "",
    val nameBn: String = "",
    val nameEn: String = "",
    val brand: String = "",
    val unit: String = "",
    val price: String = "",
    val stock: String = "",
    val existingId: Long? = null,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val isSaved: Boolean = false
)

@HiltViewModel
class AdminProductFormViewModel @Inject constructor(
    private val productDao: ProductDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminProductFormUiState())
    val uiState: StateFlow<AdminProductFormUiState> = _uiState

    fun updateField(field: String, value: String) {
        _uiState.update { state ->
            when (field) {
                "categoryId" -> state.copy(categoryId = value)
                "nameBn" -> state.copy(nameBn = value)
                "nameEn" -> state.copy(nameEn = value)
                "brand" -> state.copy(brand = value)
                "unit" -> state.copy(unit = value)
                "price" -> state.copy(price = value)
                "stock" -> state.copy(stock = value)
                else -> state
            }
        }
    }

    fun loadProduct(productId: Long?) = viewModelScope.launch {
        if (productId == null) {
            _uiState.value = AdminProductFormUiState()
            return@launch
        }

        val product = productDao.getById(productId) ?: return@launch
        _uiState.value = AdminProductFormUiState(
            categoryId = product.categoryId,
            nameBn = product.nameBn,
            nameEn = product.nameEn,
            brand = product.brand,
            unit = product.unit,
            price = product.price.toString(),
            stock = product.stock.toString(),
            existingId = product.id
        )
    }

    fun saveProduct() = viewModelScope.launch {
        val state = _uiState.value
        if (state.categoryId.isBlank() || state.nameBn.isBlank() || state.nameEn.isBlank() || state.brand.isBlank() || state.unit.isBlank()) {
            _uiState.update { it.copy(errorMessage = "সব ফিল্ড পূরণ করুন") }
            return@launch
        }

        val price = state.price.toIntOrNull()
        val stock = state.stock.toIntOrNull()
        if (price == null || stock == null) {
            _uiState.update { it.copy(errorMessage = "Price এবং Stock অবশ্যই সংখ্যা হতে হবে") }
            return@launch
        }

        _uiState.update { it.copy(isSaving = true, errorMessage = null) }

        val product = ProductEntity(
            id = state.existingId ?: 0L,
            categoryId = state.categoryId,
            nameBn = state.nameBn,
            nameEn = state.nameEn,
            brand = state.brand,
            unit = state.unit,
            price = price,
            stock = stock,
            minimumOrderQuantity = 1
        )

        if (state.existingId != null) {
            productDao.update(product)
        } else {
            productDao.insert(product)
        }

        _uiState.update { it.copy(isSaving = false, isSaved = true, errorMessage = null) }
    }
}
