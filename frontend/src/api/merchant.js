import request from "./request";

/**
 * 商家端接口集合
 * 覆盖店铺看板、商品管理、订单履约和图片上传能力。
 */

/** 获取商家看板数据 */
export function getReport() {
  return request.get("/merchant/report/dashboard");
}

/** 查询商家商品列表 */
export function getProducts(params) {
  return request.get("/merchant/product", { params });
}

/** 查询商品详情 */
export function getProduct(id) {
  return request.get(`/merchant/product/${id}`);
}

/** 新增商品 */
export function createProduct(data) {
  return request.post("/merchant/product", data);
}

/** 更新商品 */
export function updateProduct(id, data) {
  return request.put(`/merchant/product/${id}`, data);
}

/** 删除商品 */
export function deleteProduct(id) {
  return request.delete(`/merchant/product/${id}`);
}

/** 创建商品规格 */
export function createProductSpec(productId, data) {
  return request.post(`/merchant/product-specs/${productId}`, data);
}

/** 更新商品规格 */
export function updateProductSpec(specId, data) {
  return request.put(`/merchant/product-specs/${specId}`, data);
}

/** 删除商品规格 */
export function deleteProductSpec(specId) {
  return request.delete(`/merchant/product-specs/${specId}`);
}

/** 查询商品所有规格 */
export function getProductSpecs(productId) {
  return request.get(`/merchant/product-specs/product/${productId}`);
}

/** 查询订单列表 */
export function getOrders(params) {
  return request.get("/merchant/order", { params });
}

/** 查询订单详情 */
export function getOrderDetail(id) {
  return request.get(`/merchant/order/${id}`);
}

/** 库存管理相关接口 */

/** 查询库存列表 */
export function getInventory(params) {
  return request.get("/merchant/inventory", { params });
}

/** 调整商品库存 */
export function updateProductStock(productId, stock) {
  return request.put(`/merchant/inventory/${productId}/stock`, { stock });
}

/** 批量调整库存 */
export function batchUpdateStock(stockUpdates) {
  return request.put("/merchant/inventory/batch-stock", stockUpdates);
}

/** 获取库存预警 */
export function getStockWarnings(threshold = 10) {
  return request.get("/merchant/inventory/warnings", { params: { threshold } });
}

/** 评价管理相关接口 */

/** 查询评价列表 */
export function getReviews(params) {
  return request.get("/merchant/review", { params });
}

/** 查询单个商品的评价 */
export function getProductReviews(productId) {
  return request.get(`/merchant/review/product/${productId}`);
}

/** 删除单条评价 */
export function deleteReview(reviewId) {
  return request.delete(`/merchant/review/${reviewId}`);
}

/** 批量删除评价 */
export function deleteReviews(reviewIds) {
  return request.post("/merchant/review/batch-delete", reviewIds);
}

/** 订单发货 */
export function shipOrder(id) {
  return request.post(`/merchant/order/${id}/ship`);
}

/** 同意退款 */
export function acceptRefund(id) {
  return request.post(`/merchant/order/${id}/accept-refund`);
}

/** 拒绝退款 */
export function rejectRefund(id) {
  return request.post(`/merchant/order/${id}/reject-refund`);
}

/** 获取可选图片列表 */
export function listImages() {
  return request.get("/pub/images/list");
}

/** 获取商品客服聊天记录 */
export function getProductChat(productId) {
  return request.get("/merchant/chat/list", { params: { productId } });
}

/** 商家发送商品客服消息 */
export function sendProductChat(productId, userId, content) {
  return request.post("/merchant/chat/send", { productId, userId, content });
}

/** 上传商品图片 */
export function uploadImage(file) {
  const formData = new FormData();
  formData.append("file", file);
  return request.post("/pub/images/upload", formData, {
    headers: { "Content-Type": "multipart/form-data" },
  });
}
