package edu.example.dev_3_5_cc.dto.product

import com.fasterxml.jackson.annotation.JsonProperty
import edu.example.dev_3_5_cc.entity.Product

data class ProductListDTO(
    val productId: Long? = null,

    @JsonProperty("pName")
    val pName: String? = null,

    val price: Long? = null,

    val pimage: String? = null,

    val stock: Int = 0
) {
    constructor(product: Product): this (
        productId= product.productId,
        pName = product.pName,
        price = product.price,
        stock = product.stock,
        pimage = product.images.first().filename
    )
}