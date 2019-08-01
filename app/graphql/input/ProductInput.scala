package graphql.input

case class ProductInput(name: String, SKU: String, stock: Int, categoryId: String, productDetailInput: List[ProductDetailInput])
