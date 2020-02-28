package graphql.input

case class ProductInput(name: String, SKU: String, stock: Int, categoryId: String, imageUrl: String, productDetailInput: List[ProductDetailInput])
