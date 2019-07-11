import { signOut, getCurrentUser, userLogin, switchUser } from '@/api/login'
import { getToken, setToken, removeToken } from '@/utils/auth'

const user = {
  state: {
    token: getToken(),
    name: '',
    avatar: '',
    roles: []
  },

  mutations: {
    SET_TOKEN: (state, token) => {
      state.token = token
    },
    SET_NAME: (state, name) => {
      state.name = name
    },
    SET_AVATAR: (state, avatar) => {
      state.avatar = avatar
    },
    SET_ROLES: (state, roles) => {
      state.roles = roles
    }
  },

  actions: {
    // 登录
    Login({ commit }, userInfo) {
      return new Promise(async(resolve, reject) => {
        const { error, data, msg } = await userLogin(userInfo)
        if (error) return reject(msg)
        setToken(data.token)
        commit('SET_TOKEN', data.token)
        resolve(data)
      })
    },

    // 获取用户信息
    GetInfo({ commit }) {
      return new Promise(async(resolve, reject) => {
        const { error, data, msg } = await getCurrentUser()
        if (error) return reject(msg)
        if (data.role) { // 验证返回的roles是否是一个非空数组
          commit('SET_ROLES', [data.role])
        } else {
          reject('getInfo: roles null !')
        }
        commit('SET_NAME', data.username)
        commit('SET_AVATAR', data.avatar)
        return resolve(data)
      })
    },

    // 登出
    LogOut({ commit, state }) {
      return new Promise(async(resolve, reject) => {
        const { error, msg } = await signOut()
        if (error) return reject(msg)
        commit('SET_TOKEN', '')
        commit('SET_ROLES', [])
        removeToken()
        resolve()
      })
    },
    SwitchUser({ commit }, userInfo) {
      return new Promise(async(resolve, reject) => {
        const { error, msg, data } = await switchUser(userInfo)
        if (error) return reject(msg)
        console.log('switch user', data)
        setToken(data.token)
        commit('SET_TOKEN', data.token)
        commit('SET_ROLES', [])
        resolve(data)
      })
    },

    // 前端 登出
    FedLogOut({ commit }) {
      return new Promise(resolve => {
        commit('SET_TOKEN', '')
        removeToken()
        resolve()
      })
    }
  }
}

export default user
