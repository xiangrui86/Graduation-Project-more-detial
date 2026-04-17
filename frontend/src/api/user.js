import request from "./request";

/**
 * 用户端接口集合
 * 覆盖个人信息、购物车、收藏、订单、评价与客服聊天能力。
 */

/** 获取当前登录用户资料 */
export function getProfile() {
  return request.get("/user/profile");
}

/** 更新当前登录用户资料 */
export function updateProfile(data) {
  return request.put("/user/profile", data);
}

/** 获取购物车列表 */
export function getCart() {
  return request.get("/user/cart");
}

/** 添加商品到购物车 */
export function addCart(data) {
  return request.post("/user/cart/add", data);
}

/** 修改购物车商品数量 */
export function updateCartQuantity(data) {
  return request.put("/user/cart/quantity", data);
}

/** 从购物车移除商品 */
export function removeCart(productId) {
  return request.delete(`/user/cart/${productId}`);
}

/** 获取收藏列表 */
export function getFavorites() {
  return request.get("/user/favorite");
}

/** 检查商品是否已收藏 */
export function checkFavorite(productId) {
  return request.get("/user/favorite/check", { params: { productId } });
}

/** 新增收藏 */
export function addFavorite(data) {
  return request.post("/user/favorite/add", data);
}

/** 取消收藏 */
export function removeFavorite(productId) {
  return request.delete(`/user/favorite/${productId}`);
}

/** 创建订单 */
export function createOrder(data) {
  return request.post("/user/order/create", data);
}

/** 获取我的订单列表 */
export function getMyOrders(params) {
  return request.get("/user/order", { params });
}

/** 获取订单详情 */
export function getOrderDetail(id) {
  return request.get(`/user/order/${id}`);
}

/** 支付订单 */
export function payOrder(id, data) {
  return request.post(`/user/order/${id}/pay`, data);
}

/** 取消订单（收货前） */
export function cancelOrder(id) {
  return request.post(`/user/order/${id}/cancel`);
}

/** 确认收货 */
export function confirmReceive(id) {
  return request.post(`/user/order/${id}/confirm-receive`);
}

/** 完成订单（评价后） */
export function completeOrder(id) {
  return request.post(`/user/order/${id}/complete`);
}

/** 发起退款申请 */
export function requestRefund(id, reason) {
  return request.post(`/user/order/${id}/refund-request`, { reason });
}

/** 针对单个订单项申请退款 */
export function requestItemRefund(orderId, itemId, data) {
  return request.post(
    `/user/order/${orderId}/items/${itemId}/refund-request`,
    data,
  );
}

/** 批量申请多个订单项退款 */
export function batchRefundItems(orderId, data) {
  return request.post(
    `/user/order/${orderId}/items/batch-refund-request`,
    data,
  );
}

/** 发表商品评价 */
export function addReview(data) {
  return request.post("/user/review", data);
}

/** 获取用户聊天会话消息 */
export function getUserChatList(params) {
  return request.get("/user/chat/list", { params });
}
/** 发送用户聊天消息 */
export function sendUserChat(data) {
  return request.post("/user/chat/send", data);
}

/** 获取收货地址列表 */
export function getAddresses() {
  return request.get("/user/address");
}

/** 获取收货地址详情 */
export function getAddress(id) {
  return request.get(`/user/address/${id}`);
}

/** 新增收货地址 */
export function createAddress(data) {
  return request.post("/user/address", data);
}

/** 更新收货地址 */
export function updateAddress(id, data) {
  return request.put(`/user/address/${id}`, data);
}

/** 删除收货地址 */
export function deleteAddress(id) {
  return request.delete(`/user/address/${id}`);
}

/** 设为默认收货地址 */
export function setDefaultAddress(id) {
  return request.put(`/user/address/${id}/default`);
}

/** 获取默认收货地址 */
export function getDefaultAddress() {
  return request.get("/user/address/default");
}
