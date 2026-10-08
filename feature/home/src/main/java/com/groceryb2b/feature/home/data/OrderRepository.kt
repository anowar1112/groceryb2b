package com.groceryb2b.feature.home.data

import com.groceryb2b.core.database.order.OrderDao
import com.groceryb2b.core.database.order.OrderEntity
import com.groceryb2b.core.database.order.OrderItemEntity
import com.groceryb2b.core.database.order.OrderWithShop
import com.groceryb2b.core.database.catalog.ProductDao
import com.groceryb2b.core.network.SessionManager
import com.groceryb2b.core.network.SupabaseOrderApi
import com.groceryb2b.core.network.SupabaseShopApi
import com.groceryb2b.core.network.CreateRemoteOrderDto
import com.groceryb2b.core.network.CreateRemoteOrderItemDto
import com.groceryb2b.core.network.UpdateRemoteOrderStatusDto
import com.groceryb2b.core.network.SupabaseSessionRefresher
import kotlinx.coroutines.flow.Flow
import java.util.UUID
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
    private val orderDao: OrderDao,
    private val productDao: ProductDao,
    private val sessionManager: SessionManager,
    private val sessionRefresher: SupabaseSessionRefresher,
    private val shopApi: SupabaseShopApi,
    private val remoteOrderApi: SupabaseOrderApi
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
            clientSyncId = UUID.randomUUID().toString(),
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
        runCatching { uploadOrder(orderId) }
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
        val order = orderDao.getOrderById(orderId) ?: return
        sessionRefresher.ensureFreshSession()
        order.remoteId?.let { remoteId ->
            remoteOrderApi.updateOrderStatus("eq.$remoteId", UpdateRemoteOrderStatusDto(status))
        }
        orderDao.updateOrderStatus(orderId, status)
    }

    suspend fun deleteOrder(orderId: Long) {
        orderDao.deleteOrderById(orderId)
    }

    suspend fun syncRemoteOrders() {
        sessionRefresher.ensureFreshSession()
        val remoteShop = currentRemoteShop() ?: return
        orderDao.getOrdersPendingUpload().forEach { localOrder ->
            uploadOrder(localOrder.id)
        }
        remoteOrderApi.ordersForShop("eq.${remoteShop.id}").forEach { remoteOrder ->
            val clientId = remoteOrder.clientId
            val existingOrder = orderDao.getOrderByRemoteId(remoteOrder.id)
                ?: if (clientId == null) null else orderDao.getOrderByClientSyncId(clientId)
            if (existingOrder != null) {
                if (existingOrder.remoteId == null) {
                    orderDao.updateRemoteId(existingOrder.id, remoteOrder.id)
                }
                if (existingOrder.status != remoteOrder.status) {
                    orderDao.updateOrderStatus(existingOrder.id, remoteOrder.status)
                }
                return@forEach
            }
            val localOrderId = orderDao.insertOrder(
                OrderEntity(
                    remoteId = remoteOrder.id,
                    clientSyncId = remoteOrder.clientId,
                    shopId = sessionManager.shopId ?: return@forEach,
                    totalPrice = remoteOrder.totalPrice,
                    status = remoteOrder.status
                )
            )
            orderDao.insertOrderItems(remoteOrder.items.mapIndexed { index, item ->
                val localProductId = productDao.getByNameEn(item.productNameEn)?.id
                    ?: -((item.id.hashCode().toLong() and Long.MAX_VALUE) + index + 1)
                OrderItemEntity(
                    orderId = localOrderId,
                    productId = localProductId,
                    productNameBn = item.productNameBn,
                    productNameEn = item.productNameEn,
                    quantity = item.quantity,
                    pricePerUnit = item.pricePerUnit,
                    totalPrice = item.totalPrice
                )
            })
        }
    }

    private suspend fun uploadOrder(orderId: Long) {
        val localOrder = orderDao.getOrderById(orderId) ?: return
        if (localOrder.remoteId != null) return
        sessionRefresher.ensureFreshSession()
        val remoteShop = currentRemoteShop() ?: return
        val clientSyncId = localOrder.clientSyncId ?: UUID.randomUUID().toString().also {
            orderDao.updateClientSyncId(orderId, it)
        }
        val remoteOrder = remoteOrderApi.upsertOrder(
            order = CreateRemoteOrderDto(remoteShop.id, clientSyncId, localOrder.totalPrice, localOrder.status)
        ).firstOrNull() ?: return
        val items = orderDao.getOrderItems(orderId).map { item ->
            CreateRemoteOrderItemDto(
                orderId = remoteOrder.id,
                clientId = "$clientSyncId:${item.productId}",
                productNameBn = item.productNameBn,
                productNameEn = item.productNameEn,
                quantity = item.quantity,
                pricePerUnit = item.pricePerUnit,
                totalPrice = item.totalPrice
            )
        }
        if (items.isNotEmpty()) remoteOrderApi.upsertOrderItems(items = items)
        orderDao.updateRemoteId(orderId, remoteOrder.id)
    }

    private suspend fun currentRemoteShop() = sessionManager.mobileNumber
        ?.let { mobileNumber -> shopApi.findByMobileNumber("eq.$mobileNumber").firstOrNull() }
}
