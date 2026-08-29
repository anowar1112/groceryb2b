package com.groceryb2b.feature.home.data

import com.groceryb2b.core.database.catalog.CategoryDao
import com.groceryb2b.core.database.catalog.CategoryEntity
import com.groceryb2b.core.database.catalog.ProductDao
import com.groceryb2b.core.database.catalog.ProductEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CatalogRepository @Inject constructor(private val categoryDao: CategoryDao, private val productDao: ProductDao) {
    fun categories(): Flow<List<CategoryEntity>> = categoryDao.observeAll()
    fun products(): Flow<List<ProductEntity>> = productDao.observeAll()
    suspend fun ensureSeedData() {
        categoryDao.insertAll(seedCategories)
        // New demo products can be safely added in later app versions without
        // duplicating catalog entries already saved on the device.
        seedProducts.forEach { product ->
            if (!productDao.existsByNameEn(product.nameEn)) productDao.insert(product)
        }
    }
}

private val seedCategories = listOf(
    CategoryEntity("rice", "চাল", "Rice", 1), CategoryEntity("dal", "ডাল", "Dal", 2), CategoryEntity("oil", "তেল", "Oil", 3), CategoryEntity("salt_sugar", "লবণ ও চিনি", "Salt & Sugar", 4), CategoryEntity("biscuits", "বিস্কুট", "Biscuits", 5), CategoryEntity("noodles", "নুডলস", "Noodles", 6), CategoryEntity("drinks", "পানীয়", "Drinks", 7), CategoryEntity("soap", "সাবান", "Soap", 8), CategoryEntity("shampoo", "শ্যাম্পু", "Shampoo", 9), CategoryEntity("detergent", "ডিটারজেন্ট", "Detergent", 10), CategoryEntity("tissue", "টিস্যু", "Tissue", 11), CategoryEntity("spices", "মসলা", "Spices", 12), CategoryEntity("other", "অন্যান্য", "Other grocery products", 13)
)
private val seedProducts = listOf(
    ProductEntity(categoryId="rice", nameBn="মিনিকেট চাল", nameEn="Miniket Rice", brand="লোকাল", unit="25 কেজি", price=1900, stock=45),
    ProductEntity(categoryId="rice", nameBn="নাজিরশাইল চাল", nameEn="Nazirshail Rice", brand="লোকাল", unit="25 কেজি", price=2100, stock=30),
    ProductEntity(categoryId="dal", nameBn="মসুর ডাল", nameEn="Masoor Dal", brand="প্রাণ", unit="1 কেজি", price=145, stock=80),
    ProductEntity(categoryId="oil", nameBn="সয়াবিন তেল", nameEn="Soybean Oil", brand="ফ্রেশ", unit="5 লিটার", price=870, discountPercent=3, stock=55),
    ProductEntity(categoryId="salt_sugar", nameBn="আয়োডিন লবণ", nameEn="Iodized Salt", brand="মুন্নু", unit="1 কেজি", price=42, stock=120),
    ProductEntity(categoryId="salt_sugar", nameBn="চিনি", nameEn="Sugar", brand="লোকাল", unit="1 কেজি", price=135, stock=100),
    ProductEntity(categoryId="biscuits", nameBn="টোস্ট বিস্কুট", nameEn="Toast Biscuit", brand="অলিম্পিক", unit="300 গ্রাম", price=95, stock=70),
    ProductEntity(categoryId="noodles", nameBn="ইনস্ট্যান্ট নুডলস", nameEn="Instant Noodles", brand="ম্যাগি", unit="8 প্যাক", price=280, stock=60),
    ProductEntity(categoryId="drinks", nameBn="আমের জুস", nameEn="Mango Juice", brand="ফ্রুটিকা", unit="1 লিটার", price=105, stock=48),
    ProductEntity(categoryId="soap", nameBn="বিউটি সোপ", nameEn="Beauty Soap", brand="লাক্স", unit="100 গ্রাম", price=68, stock=90),
    ProductEntity(categoryId="shampoo", nameBn="শ্যাম্পু স্যাশে", nameEn="Shampoo Sachet", brand="সানসিল্ক", unit="12 পিস", price=90, stock=75),
    ProductEntity(categoryId="detergent", nameBn="ডিটারজেন্ট পাউডার", nameEn="Detergent Powder", brand="হুইল", unit="1 কেজি", price=125, stock=42),
    ProductEntity(categoryId="tissue", nameBn="ফেসিয়াল টিস্যু", nameEn="Facial Tissue", brand="বসুন্ধরা", unit="100 শিট", price=75, stock=50),
    ProductEntity(categoryId="spices", nameBn="হলুদের গুঁড়া", nameEn="Turmeric Powder", brand="রাধুনী", unit="200 গ্রাম", price=85, stock=35),
    ProductEntity(categoryId="other", nameBn="ম্যাচবক্স", nameEn="Matchbox", brand="লোকাল", unit="10 প্যাক", price=120, stock=65)
)
