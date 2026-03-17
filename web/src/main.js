import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap/dist/js/bootstrap.bundle.min.js'
import $ from 'jquery'

// 将 jQuery 挂载到 window 对象，供全局使用
window.$ = $
window.jQuery = $

createApp(App).use(store).use(router).mount('#app')