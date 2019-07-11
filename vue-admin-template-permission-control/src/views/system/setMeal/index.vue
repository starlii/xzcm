<template>
  <star-page>
    <div style="text-align: right" class="mb15">
      <el-button type="primary" icon="el-icon-plus" @click="$router.push({path: 'addPackage'})">新增套餐</el-button>
    </div>
    <el-table :data="packageInfo.list" border>
      <el-table-column label="ID" prop="id" align="center" width="80"/>
      <el-table-column label="套餐名称" prop="name" header-align="center"/>
      <el-table-column label="套餐描述" prop="description" header-align="center" min-width="250"/>
      <el-table-column label="状态" prop="status" align="center" width="80" sortable>
        <template slot-scope="scope">
          <span v-if="!scope.row.status" class="el-icon-check success pointer" @click="updateStatus(scope.row.id, 1)"/>
          <span v-else class="el-icon-close red pointer" @click="updateStatus(scope.row.id, 0)"/>
        </template>
      </el-table-column>
      <el-table-column label="管理操作" align="center">
        <template slot-scope="scope">
          <el-button @click="$router.push({path: 'editPackage', query:{id: scope.row.id}})">修改</el-button>
          <el-button @click="deletePackage(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      :current-page="page"
      :page-sizes="[50, 100, 150]"
      :page-size="size"
      :total="packageInfo.total"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"/>
  </star-page>
</template>

<script>
export default {
  name: 'Index',
  data() {
    return {
      packageInfo: {
        total: 0,
        list: []
      },
      page: 1,
      size: 50
    }
  },
  created() {
    this.getList()
  },
  methods: {
    handleSizeChange(val) {
      this.size = val
      this.getList()
    },
    handleCurrentChange(val) {
      this.page = val
      this.getList()
    },
    async getList() {
      const { error, data, msg } = await this.$api.userPackage.getUserPackage({
        page: this.page,
        size: this.size
      })
      if (error) return this.$message.error(msg)
      this.packageInfo = data
    },
    async deletePackage(packageId) {
      const { error, msg } = await this.$api.userPackage.deleteUserPackage({ packageId })
      if (error) return this.$message.error(msg)
      this.$message.success(msg)
      this.getList()
    },
    async updateStatus(id, status) {
      const { error, msg } = await this.$api.userPackage.updateUserPackage({ id, status })
      if (error) return this.$message.error(msg)
      this.$message.success(msg)
      this.getList()
    }
  }
}
</script>

<style scoped>

</style>
