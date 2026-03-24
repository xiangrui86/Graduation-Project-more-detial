import request from './request'

/**
 * 认证相关接口
 * - 登录
 * - 注册
 */

/**
 * 用户登录
 * @param {Object} data 登录参数（如 username、password）
 * @returns {Promise} 登录结果（通常包含 token 与用户信息）
 */
export function login(data) {
  return request.post('/auth/login', data)
}

/**
 * 用户注册
 * @param {Object} data 注册参数（如 username、password、nickname）
 * @returns {Promise} 注册结果
 */
export function register(data) {
  return request.post('/auth/register', data)
}
