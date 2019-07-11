import Vue from 'vue'

import 'normalize.css/normalize.css' // A modern alternative to CSS resets

import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'

import '@/styles/index.scss' // global css

import App from './App'
import router from './router'
import store from './store'
import StarPage from '@/components/page'
import VueLazyLoad from 'vue-lazyload'
import '@/icons' // icon
import '@/permission' // permission control
import api from '@/api'
import filters from './utils/filters'
import directive from './utils/directive'

import VueQuillEditor from 'vue-quill-editor'

// require styles
import 'quill/dist/quill.core.css'
import 'quill/dist/quill.snow.css'
import 'quill/dist/quill.bubble.css'

Vue.use(VueQuillEditor, /* { default global options } */)
Vue.use(ElementUI, { size: 'mini' })
Vue.use(StarPage)
Vue.use(api)
Vue.use(filters)
Vue.use(directive)
Vue.use(VueLazyLoad, {
  error: '/static/lazy.png',
  loading: '/static/lazy.png'
})
Vue.config.productionTip = false

new Vue({
  el: '#app',
  router,
  store,
  render: h => h(App)
})
