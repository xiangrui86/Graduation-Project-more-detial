import request from "./request";

/**
 * 管理端接口集合
 * 用于用户、商家、分类、商品、订单、资讯等后台管理能力。
 */

/** 获取管理端看板数据 */
export function getReport() {
  return request.get("/admin/report/dashboard");
}

/** 分页查询用户列表 */
export function getUsers(params) {
  return request.get("/admin/user", { params });
}

/** 新增用户 */
export function createUser(data) {
  return request.post("/admin/user", data);
}

/** 更新用户信息 */
export function updateUser(id, data) {
  return request.put(`/admin/user/${id}`, data);
}

/** 删除用户 */
export function deleteUser(id) {
  return request.delete(`/admin/user/${id}`);
}

/** 查询商家列表 */
export function getMerchants() {
  return request.get("/admin/merchant");
}

/** 新增商家 */
export function createMerchant(data) {
  return request.post("/admin/merchant", data);
}

/** 更新商家 */
export function updateMerchant(id, data) {
  return request.put(`/admin/merchant/${id}`, data);
}

/** 删除商家 */
export function deleteMerchant(id) {
  return request.delete(`/admin/merchant/${id}`);
}

/** 绑定商家负责人账号 */
export function setMerchantOwner(id, ownerUserId) {
  return request.put(`/admin/merchant/${id}/owner`, { ownerUserId });
}

/** 获取分类列表 */
export function getCategories() {
  return request.get("/admin/category");
}

/** 新增分类 */
export function createCategory(data) {
  return request.post("/admin/category", data);
}

/** 更新分类 */
export function updateCategory(id, data) {
  return request.put(`/admin/category/${id}`, data);
}

/** 删除分类 */
export function deleteCategory(id) {
  return request.delete(`/admin/category/${id}`);
}

/** 查询订单列表 */
export function getOrders(params) {
  return request.get("/admin/order", { params });
}

/** 查询订单详情 */
export function getOrderDetail(id) {
  return request.get(`/admin/order/${id}`);
}

/** 修改订单状态 */
export function updateOrderStatus(id, status) {
  return request.post(`/admin/order/${id}/status`, { status });
}

/** 查询资讯/公告列表 */
export function getNews() {
  return request.get("/admin/news");
}

/** 新增资讯/公告 */
export function createNews(data) {
  return request.post("/admin/news", data);
}

/** 更新资讯/公告 */
export function updateNews(id, data) {
  return request.put(`/admin/news/${id}`, data);
}

/** 删除资讯/公告 */
export function deleteNews(id) {
  return request.delete(`/admin/news/${id}`);
}

/** 快速创建公告 */
export function createAnnouncement(data) {
  return createNews({ ...data, type: "ANNOUNCEMENT", published: true });
}

/** 评价管理相关接口 */

/** 查询评价列表 */
export function getReviews(params) {
  return request.get("/admin/review", { params });
}

/** 删除单条评价 */
export function deleteReview(reviewId) {
  return request.delete(`/admin/review/${reviewId}`);
}

/** 批量删除评价 */
export function deleteReviews(reviewIds) {
  return request.post("/admin/review/batch-delete", reviewIds);
}

/** 管理端商品审核列表 */
export function getProducts(params) {
  return request.get("/admin/product", { params });
}

/** 管理端创建商品 */
export function createProduct(data) {
  return request.post("/admin/product", data);
}

/** 管理端商品详情 */
export function getProduct(id) {
  return request.get(`/admin/product/${id}`);
}

/** 管理端更新商品 */
export function updateProduct(id, data) {
  return request.put(`/admin/product/${id}`, data);
}

/** 管理端删除商品 */
export function deleteProduct(id) {
  return request.delete(`/admin/product/${id}`);
}

/** 管理端审核通过 */
export function approveProduct(id) {
  return request.post(`/admin/product/${id}/approve`);
}

/** 管理端审核不通过 */
export function rejectProduct(id, reason) {
  return request.post(`/admin/product/${id}/reject`, { reason });
}
