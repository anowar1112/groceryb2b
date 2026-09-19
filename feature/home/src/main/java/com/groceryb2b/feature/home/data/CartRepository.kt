package com.groceryb2b.feature.home.data

import com.groceryb2b.core.database.cart.CartDao
import com.groceryb2b.core.database.cart.CartItemEntity
import com.groceryb2b.core.database.catalog.ProductDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

data class CartItem(
    val productId: Long,
    val productNameBn: String,
    val productNameEn: String,
    val brand: String,
    val unit: String,
    val quantity: Int,
    val pricePerUnit: Int,
    val originalPricePerUnit: Int = 0,
    val totalPrice: Int
)

data class CartSummary(
    val items: List<CartItem> = emptyList(),
    val totalItems: Int = 0,
    val totalPrice: Int = 0
)

class CartRepository @Inject constructor(
    private val cartDao: CartDao,
    private val productDao: ProductDao
) {
    fun observeCartByShop(shopId: String): Flow<CartSummary> {
        return cartDao.observeByShop(shopId).map { cartItems ->
            val items = cartItems.mapNotNull { cartItem ->
                val product = productDao.getById(cartItem.productId) ?: return@mapNotNull null
                
                // Calculate discounted price: price * (100 - discount) / 100
                val discountedPrice = if (product.discountPercent > 0) {
                    (product.price * (100 - product.discountPercent)) / 100
                } else {
                    product.price
                }
                
                CartItem(
                    productId = cartItem.productId,
                    productNameBn = product.nameBn,
                    productNameEn = product.nameEn,
                    brand = product.brand,
                    unit = product.unit,
                    quantity = cartItem.quantity,
                    pricePerUnit = discountedPrice,
                    originalPricePerUnit = product.price,
                    totalPrice = discountedPrice * cartItem.quantity
                )
            }
            CartSummary(
                items = items,
                totalItems = items.sumOf { it.quantity },
                totalPrice = items.sumOf { it.totalPrice }
            )
        }
    }

    suspend fun updateQuantity(shopId: String, productId: Long, quantity: Int) {
        if (quantity <= 0) {
            cartDao.delete(shopId, productId)
        } else {
            cartDao.upsert(CartItemEntity(shopId, productId, quantity))
        }
    }

    suspend fun clearCart(shopId: String) {
        cartDao.clear(shopId)
    }
}
