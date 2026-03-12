import request from "./request";

export function getReport() {
  return request.get("/merchant/report/dashboard");
}

export function getProducts(params) {
  return request.get("/merchant/product", { params });
}

export function getProduct(id) {
  return request.get(`/merchant/product/${id}`);
}

export function createProduct(data) {
  return request.post("/merchant/product", data);
}

export function updateProduct(id, data) {
  return request.put(`/merchant/product/${id}`, data);
}

export function deleteProduct(id) {
  return request.delete(`/merchant/product/${id}`);
}

export function getOrders(params) {
  return request.get("/merchant/order", { params });
}

export function getOrderDetail(id) {
  return request.get(`/merchant/order/${id}`);
}

export function shipOrder(id) {
  return request.post(`/merchant/order/${id}/ship`);
}

export function getChatList(userId) {
  return request.get("/merchant/chat/list", { params: { userId } });
}

export function sendChat(data) {
  return request.post("/merchant/chat/send", data);
}

export function listImages() {
  return request.get("/pub/images/list");
}

export function uploadImage(file) {
  const formData = new FormData();
  formData.append("file", file);
  return request.post("/pub/images/upload", formData, {
    headers: { "Content-Type": "multipart/form-data" },
  });
}
