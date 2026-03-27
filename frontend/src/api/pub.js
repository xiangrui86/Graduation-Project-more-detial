import request from './request'

/**
 * 公共接口集合（无需登录或弱依赖登录）
 * 主要用于商城前台展示数据读取。
 */

/** 查询商品列表 */
export function getProducts(params) {
  return request.get('/pub/products', { params })
}

/** 查询商品详情 */
export function getProduct(id) {
  return request.get(`/pub/products/${id}`)
}

/** 获取新品列表 */
export function getNewArrivals(size = 10) {
  return request.get('/pub/products/new', { params: { size } })
}

/** 获取销量排行榜 */
export function getSalesRank(size = 10) {
  return request.get('/pub/products/rank', { params: { size } })
}

/** 获取个性化推荐 */
export function getRecommend(userId, size = 20) {
  return request.get('/pub/products/recommend', { params: { userId, size } })
}

/** 获取分类列表（可按父级过滤） */
export function getCategories(parentId) {
  return request.get('/pub/categories', { params: parentId != null ? { parentId } : {} })
}

/** 获取全部分类（树构建场景） */
export function getAllCategories() {
  return request.get('/pub/categories', { params: { all: true } })
}

/** 查询资讯/公告列表 */
export function getNews(params) {
  return request.get('/pub/news', { params: params || {} })
}

/** 获取公告列表 */
export function getAnnouncements(size = 5) {
  return request.get('/pub/news/announcements', { params: { size } })
}

/** 查询商品评价 */
export function getReviews(productId, page = 0, size = 10) {
  return request.get('/pub/reviews', { params: { productId, page, size } })
}

/** 查询商品规格列表 */
export function getProductSpecs(productId) {
  return request.get(`/api/product-specs/product/${productId}`)
}

/** 查询单个规格详情 */
export function getProductSpec(specId) {
  return request.get(`/api/product-specs/${specId}`)
}
