import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'

import Antd from 'ant-design-vue';
import 'ant-design-vue/dist/reset.css';
import './css/tailwindcss.css';

import '@/access' // 引入权限控制

// region https://github.com/aesoper101/vue3-colorpicker/blob/main/README.ZH-cn.md
import Vue3ColorPicker from "vue3-colorpicker";
import "vue3-colorpicker/style.css";
// endregion

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(Antd)
app.use(Vue3ColorPicker)

app.mount('#app')
