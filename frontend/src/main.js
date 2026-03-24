import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import './styles/theme.css'
import './styles/element-overrides.css'

// 注册全局 UI 组件库
Vue.use(ElementUI)
Vue.config.productionTip = false

// 挂载应用根实例，注入路由与状态管理
new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
