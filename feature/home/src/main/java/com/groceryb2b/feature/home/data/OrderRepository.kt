package com.groceryb2b.feature.home.data

import com.groceryb2b.core.database.order.OrderDao
import com.groceryb2b.core.database.order.OrderEntity
import com.groceryb2b.core.database.order.OrderItemEntity
import com.groceryb2b.core.database.order.OrderWithShop
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

data class OrderSummary(
    val id: Long,
    val shopId: String,
    val totalPrice: Int,
    val status: String,
    val items: List<OrderItemEntity> = emptyList(),
    val createdAtEpochMillis: Long
)

class OrderRepository @Inject constructor(
    private val orderDao: OrderDao
) {
    fun observeOrdersByShop(shopId: String): Flow<List<OrderEntity>> {
        return orderDao.observeOrdersByShop(shopId)
    }

    fun observeAllOrders(): Flow<List<OrderEntity>> {
        return orderDao.observeAll()
    }

    fun observeAllOrdersWithShop(): Flow<List<OrderWithShop>> {
        return orderDao.observeAllWithShop()
    }

    suspend fun createOrder(shopId: String, cartItems: List<CartItem>): Long {
        val totalPrice = cartItems.sumOf { it.totalPrice }
        
        val order = OrderEntity(
            shopId = shopId,
            totalPrice = totalPrice,
            status = "PENDING"
        )
        
        val orderId = orderDao.insertOrder(order)
        
        val orderItems = cartItems.map { cartItem ->
            OrderItemEntity(
                orderId = orderId,
                productId = cartItem.productId,
                productNameBn = cartItem.productNameBn,
                productNameEn = cartItem.productNameEn,
                quantity = cartItem.quantity,
                pricePerUnit = cartItem.pricePerUnit,
                totalPrice = cartItem.totalPrice
            )
        }
        
        orderDao.insertOrderItems(orderItems)
        return orderId
    }

    suspend fun getOrderById(orderId: Long): OrderSummary? {
        val order = orderDao.getOrderById(orderId) ?: return null
        val items = orderDao.getOrderItems(orderId)
        
        return OrderSummary(
            id = order.id,
            shopId = order.shopId,
            totalPrice = order.totalPrice,
            status = order.status,
            items = items,
            createdAtEpochMillis = order.createdAtEpochMillis
        )
    }

    suspend fun updateOrderStatus(orderId: Long, status: String) {
        orderDao.updateOrderStatus(orderId, status)
    }

    suspend fun deleteOrder(orderId: Long) {
        orderDao.deleteOrderById(orderId)
    }
}
