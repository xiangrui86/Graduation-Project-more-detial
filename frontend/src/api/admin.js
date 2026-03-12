import request from "./request";

export function getReport() {
  return request.get("/admin/report/dashboard");
}

export function getUsers(params) {
  return request.get("/admin/user", { params });
}

export function createUser(data) {
  return request.post("/admin/user", data);
}

export function updateUser(id, data) {
  return request.put(`/admin/user/${id}`, data);
}

export function deleteUser(id) {
  return request.delete(`/admin/user/${id}`);
}

export function getMerchants() {
  return request.get("/admin/merchant");
}

export function createMerchant(data) {
  return request.post("/admin/merchant", data);
}

export function updateMerchant(id, data) {
  return request.put(`/admin/merchant/${id}`, data);
}

export function deleteMerchant(id) {
  return request.delete(`/admin/merchant/${id}`);
}

export function getCategories() {
  return request.get("/admin/category");
}

export function createCategory(data) {
  return request.post("/admin/category", data);
}

export function updateCategory(id, data) {
  return request.put(`/admin/category/${id}`, data);
}

export function deleteCategory(id) {
  return request.delete(`/admin/category/${id}`);
}

export function getProducts(params) {
  return request.get("/admin/product", { params });
}

export function updateProduct(id, data) {
  return request.put(`/admin/product/${id}`, data);
}

export function deleteProduct(id) {
  return request.delete(`/admin/product/${id}`);
}

export function getOrders(params) {
  return request.get("/admin/order", { params });
}

export function getOrderDetail(id) {
  return request.get(`/admin/order/${id}`);
}

export function getNews() {
  return request.get("/admin/news");
}

export function createNews(data) {
  return request.post("/admin/news", data);
}

export function updateNews(id, data) {
  return request.put(`/admin/news/${id}`, data);
}

export function deleteNews(id) {
  return request.delete(`/admin/news/${id}`);
}
