<template>
  <star-page>
    <div class="mt10 search">
      <div class="w300">
        <el-input v-model="username" placeholder="搜索用户名称" @keyup.enter.native="getUserList">
          <template slot="append" @click="getUserList">搜索</template>
        </el-input>
      </div>
      <el-dropdown class="ml10" trigger="click" @command="handleCommand"> <!--@command="handleCommand"-->
        <el-button type="primary">
          {{ userPackage.name }}<i class="el-icon-arrow-down el-icon--right"/>
        </el-button>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item :command="{id: null, name: '全部套餐'}">全部套餐</el-dropdown-item>
          <el-dropdown-item v-for="item in packageList" :key="item.id" :command="item">{{ item.name }}</el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
      <el-button class="ml20" type="primary" icon="el-icon-plus" @click="$router.push({path: 'addUser'})">添加用户</el-button>
    </div>
    <el-table :data="userData.list" border stripe class="mt10">
      <el-table-column prop="id" label="ID" width="50" align="center"/>
      <el-table-column prop="username" label="用户名称" align="center"/>
      <el-table-column prop="roleName" label="用户角色" align="center"/>
      <el-table-column prop="packageName" label="套餐名称" align="center"/>
      <el-table-column prop="remark" label="用户描述" header-align="center" width="250"/>
      <el-table-column prop="ip" label="最后登录IP" align="center"/>
      <el-table-column prop="updateTime" label="最后登录时间" align="center" width="140"/>
      <el-table-column prop="" label="状态" align="center" width="50">
        <template slot-scope="scope">
          <span v-if="!scope.row.status" class="el-icon-check success"/>
          <span v-else class="el-icon-close red"/>
        </template>
      </el-table-column>
      <el-table-column prop="" label="管理操作" align="center" width="150">
        <template slot-scope="scope">
          <el-button type="text" @click="switchUser(scope.row.password, scope.row.username)">登录为</el-button>
          <el-button type="text" @click="$router.push({path: 'editUser', query:{id: scope.row.id}})">修改</el-button>
          <el-button type="text" @click="deleteUser(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      :current-page="pageNum"
      :page-sizes="[50, 100, 150]"
      :page-size="pageSize"
      :total="userData.total"
      layout="total, sizes, prev, pager, next, jumper"
      class="mt10"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"/>
  </star-page>
</template>

<script>
export default {
  data() {
    return {
      userData: {
        total: 0,
        list: []
      },
      userPackage: { name: '全部套餐', id: null },
      currentPage: 1,
      username: '',
      pageSize: 50,
      pageNum: 1,
      packageList: []
    }
  },
  created() {
    this.getUserList()
    this.getUserPackage()
  },
  methods: {
    handleCommand(val) {
      this.userPackage = val
      this.getUserList()
    },
    async getUserPackage() {
      const { error, msg, data } = await this.$api.userPackage.getNoPage()
      if (error) return this.$message.error(msg)
      this.packageList = data
    },
    handleSizeChange(val) {
      this.pageSize = val
    },
    handleCurrentChange(val) {
      this.pageNum = val
    },
    async getUserList() {
      const { error, data, msg } = await this.$api.userSetting.getUserList({
        username: this.username,
        page: this.pageNum,
        size: this.pageSize,
        userPackage: this.userPackage.id
      })
      if (error) return this.$message.error(msg)
      this.userData = data
    },
    async deleteUser(userId) {
      const { error, msg } = await this.$api.userSetting.deleteUser({ userId })
      if (error) return this.$message.error(msg)
      this.$message.success(msg)
      this.getUserList()
    },
    switchUser(password, username) {
      this.$store.dispatch('SwitchUser', { password, username }).then(() => {
        this.$router.push({ path: this.redirect || '/' })
      }).catch(err => {
        console.log('switch error', err)
      })
    }
  }
}
</script>

<style scoped>

</style>
