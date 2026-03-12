import request from "./request";

export function getProfile() {
  return request.get("/user/profile");
}

export function getCart() {
  return request.get("/user/cart");
}

export function addCart(data) {
  return request.post("/user/cart/add", data);
}

export function updateCartQuantity(data) {
  return request.put("/user/cart/quantity", data);
}

export function removeCart(productId) {
  return request.delete(`/user/cart/${productId}`);
}

export function getFavorites() {
  return request.get("/user/favorite");
}

export function checkFavorite(productId) {
  return request.get("/user/favorite/check", { params: { productId } });
}

export function addFavorite(data) {
  return request.post("/user/favorite/add", data);
}

export function removeFavorite(productId) {
  return request.delete(`/user/favorite/${productId}`);
}

export function createOrder(data) {
  return request.post("/user/order/create", data);
}

export function getMyOrders(params) {
  return request.get("/user/order", { params });
}

export function getOrderDetail(id) {
  return request.get(`/user/order/${id}`);
}

export function payOrder(id) {
  return request.post(`/user/order/${id}/pay`);
}

export function confirmReceive(id) {
  return request.post(`/user/order/${id}/confirm-receive`);
}

export function addReview(data) {
  return request.post("/user/review", data);
}

// 用户与客服聊天
export function getUserChatList() {
  return request.get("/user/chat/list");
}
export function sendUserChat(data) {
  return request.post("/user/chat/send", data);
}
