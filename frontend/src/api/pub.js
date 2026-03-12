import request from './request'

export function getProducts(params) {
  return request.get('/pub/products', { params })
}

export function getProduct(id) {
  return request.get(`/pub/products/${id}`)
}

export function getNewArrivals(size = 10) {
  return request.get('/pub/products/new', { params: { size } })
}

export function getSalesRank(size = 10) {
  return request.get('/pub/products/rank', { params: { size } })
}

export function getRecommend(userId, size = 20) {
  return request.get('/pub/products/recommend', { params: { userId, size } })
}

export function getCategories(parentId) {
  return request.get('/pub/categories', { params: parentId != null ? { parentId } : {} })
}

export function getNews(params) {
  return request.get('/pub/news', { params: params || {} })
}

export function getAnnouncements(size = 5) {
  return request.get('/pub/news/announcements', { params: { size } })
}

export function getReviews(productId, page = 0, size = 10) {
  return request.get('/pub/reviews', { params: { productId, page, size } })
}
