<template>
  <star-page>
    <el-tabs v-model="currentTabName" type="border-card">
      <el-tab-pane v-for="(item, index) in tabList" :label="item.label" :key="index" :name="item.name">
        <div :is="item.com"/>
      </el-tab-pane>
    </el-tabs>
  </star-page>
</template>

<script>
import infoSetting from './tpls/info-setting'
import wechatSetting from './tpls/wechat-setting'
import fileSetting from './tpls/file-setting'
export default {
  name: 'Index',
  components: { infoSetting, wechatSetting, fileSetting },
  data() {
    return {
      currentTabName: '1',
      tabList: [{ label: '基础信息设置', name: '1', com: 'infoSetting' }, { label: '微信设置', name: '2', com: 'wechatSetting' }, { label: '素材管理', name: '3', com: 'fileSetting' }]
    }
  }
}
</script>

<style scoped>

</style>
