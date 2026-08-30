package com.groceryb2b.feature.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.groceryb2b.core.database.catalog.CategoryEntity
import com.groceryb2b.core.database.catalog.ProductEntity
import com.groceryb2b.core.database.cart.CartDao
import com.groceryb2b.core.database.cart.CartItemEntity
import com.groceryb2b.core.database.shop.ShopDao
import com.groceryb2b.core.database.shop.ShopEntity
import com.groceryb2b.core.network.SessionManager
import com.groceryb2b.feature.home.data.CatalogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val query: String = "",
    val selectedCategoryId: String? = null,
    val categories: List<CategoryEntity> = emptyList(),
    val products: List<ProductEntity> = emptyList(),
    val quantities: Map<Long, Int> = emptyMap(),
    val shop: ShopEntity? = null,
    val isAdmin: Boolean = false
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: CatalogRepository,
    private val cartDao: CartDao,
    private val sessionManager: SessionManager,
    private val shopDao: ShopDao
) : ViewModel() {
    private val shopId = sessionManager.shopId ?: sessionManager.mobileNumber.orEmpty()
    private val mobileNumber = sessionManager.mobileNumber.orEmpty()
    private val query = MutableStateFlow("")
    private val selectedCategoryId = MutableStateFlow<String?>(null)
    private val quantities = cartDao.observeByShop(shopId)
        .map { items -> items.associate { it.productId to it.quantity } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyMap())
    
    private val shop = shopDao.observeByMobileNumber(mobileNumber)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val uiState: StateFlow<HomeUiState> = combine(
        combine(repository.categories(), repository.products(), ::Pair),
        combine(query, selectedCategoryId, quantities, ::Triple),
        shop
    ) { (categories, products), (search, category, qty), shopInfo ->
        val normalized = search.trim().lowercase()
        HomeUiState(
            query = search,
            selectedCategoryId = category,
            categories = categories,
            products = products.filter { product ->
                (category == null || product.categoryId == category) &&
                        (normalized.isBlank() ||
                                product.nameBn.lowercase().contains(normalized) ||
                                product.nameEn.lowercase().contains(normalized) ||
                                product.brand.lowercase().contains(normalized))
            },
            quantities = qty,
            shop = shopInfo,
            isAdmin = sessionManager.isAdmin
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())

    init {
        viewModelScope.launch { repository.ensureSeedData() }
    }

    fun updateQuery(value: String) {
        query.value = value
    }

    fun selectCategory(id: String?) {
        selectedCategoryId.value = id
    }

    fun changeQuantity(id: Long, change: Int) = viewModelScope.launch {
        val next = (quantities.value[id] ?: 0) + change
        if (next <= 0) cartDao.delete(shopId, id) else cartDao.upsert(CartItemEntity(shopId, id, next))
    }
    
    fun logout() {
        sessionManager.clear()
    }
}
