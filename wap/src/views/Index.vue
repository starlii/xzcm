<template>
  <div id="home">
    <!--<el-button @click="shareWechat">分享</el-button>-->
    <!--<Slider/>-->
    <section class="pd-10 bg-1 aui-text-center aui-font-size-12 lh-18 bg-white centerInfo">
      <div class="aui-row">
        <div class="aui-col-xs-4 bd-r-1">
          <div class="text-white"><img src="../assets/images/user.png" width="14" height="14"> 参与选手</div>
          <div class="aui-font-size-14 cl-1"><strong v-if="data.playerNum==null">0</strong>
            <strong v-else>{{ data.playerNum }}</strong></div>
        </div>
        <div class="aui-col-xs-4 bd-r-1">
          <div class="text-white"><i class="iconfont icon-toupiao"/> 累计投票</div>
          <div class="aui-font-size-14 cl-1"><strong v-if="data.votes<0">0</strong><strong v-else>{{ data.votes
            }}</strong></div>
        </div>
        <div class="aui-col-xs-4">
          <div class="text-white"><i class="iconfont icon-fangwenliuliang"/> 访问量</div>
          <div class="aui-font-size-14 cl-1"><strong v-if="data.views<0">0</strong><strong v-else>{{ data.views
            }}</strong></div>
        </div>
      </div>
    </section>
    <div class="time">
      <ul>
        <li><img src="../assets/images/date.jpg" class="aui-margin-r-5">投票日期：<span>{{ data.voteStartTime | formatDate("yyyy-MM-dd") }} 到 {{ data.voteEndTime | formatDate("yyyy-MM-dd") }}</span>
        </li>
        <li><img src="../assets/images/time.jpg" class="aui-margin-r-5">投票倒计时：
          <span v-if='this.overStatus == "true"'>
              0天0小时0分0秒
          </span>
          <span v-else>
            <conutDown :end-time="activityEndTime"/>
          </span>
        </li>
      </ul>
    </div>
    <div class="searchWrap">
      <el-input v-model="keyword" placeholder="请输入编号或姓名" class="input-with-select">
        <el-button slot="append" icon="el-icon-search" type="primary" @click="search()">搜索</el-button>
      </el-input>
    </div>
    <el-row class="aui-margin-t-10 btnList">
      <router-link :to="{path:'/prize/',query:{activityId:activityId}}" class="aui-bar-tab-item">
        <el-button type="">规则/奖品</el-button>
      </router-link>
      <router-link :to="{path:'/pink/',query:{activityId:activityId}}" class="aui-bar-tab-item">
        <el-button type="">比赛排名</el-button>
      </router-link>
      <el-button type="" @click="signBtn()">我要报名</el-button>
    </el-row>

    <el-tabs v-model="activeName" class="aui-margin-t-10" @tab-click="handleClick">
      <el-tab-pane label="人气选手" name="first">
        <div v-loading="loading">
          <ul class="hot">
            <li v-for="(item,index) in popularityPlayers">
              <router-link
                :to="{path:'/player',query:{id:item.playerId,activityId:activityId}}"
                :key="item.playerId"
                class="display-b  aui-padded-b-10  pos-r">
                <div class="pic">
                  <img :src="item.image" alt="">
                </div>
                <div class="over-h">
                  <div class="index-voteNum aui-pull-left">{{ item.num }}号</div>
                  <div class="cl-666 playerTitle aui-pull-left aui-margin-l-5">{{ item.name }}</div>
                </div>
                <p class="pos-r aui-margin-t-5">
                  <span class="red" v-if="item.votes<0">0 票</span>
                  <span class="red" v-else>{{ item.votes }}票</span>
                  <span class="index-vote bg-1">投票</span>
                </p>
              </router-link>
            </li>
          </ul>
          <p class="aui-clearfix"/>
          <pagination :total="total" :current-page='pageNum' @pagechange="handleCurrentChange"></pagination>
          <!--<el-pagination
            :current-page="pageNum"
            :page-size="pageSize"
            :total="total"
            class="aui-margin-t-10"
            background
            layout="pager"
            :pager-count="totalCount"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"/>-->
        </div>
      </el-tab-pane>
      <el-tab-pane label="最新参与" name="second">
        <div v-loading="latesLoading">
          <ul class="hot">
            <li v-for="(item,index) in data.latestPlayers">
              <router-link
                :to="{path:'/player',query:{id:item.playerId,activityId:activityId}}"
                :key="item.playerId"
                class="display-b  aui-padded-b-10  pos-r">
                <div class="pic">
                  <img :src="item.images[0]" alt="">
                </div>
                <div class="over-h">
                  <div class="index-voteNum aui-pull-left">{{ item.num }}号</div>
                  <div class="cl-666 playerTitle aui-pull-left aui-margin-l-5">{{ item.name }}</div>
                </div>
                <p class="pos-r aui-margin-t-5">
                  <span class="red" v-if="item.currentVotes<0">0 票</span>
                  <span class="red" v-else>{{ item.currentVotes }}票</span>
                  <span class="index-vote bg-1">投票</span>
                </p>
              </router-link>
            </li>
          </ul>
        </div>
      </el-tab-pane>
    </el-tabs>
    <!--<BottomNav :complaint-info="complaintInfo" ref="btm"/>-->
  </div>

</template>
<script>
  import Slider from './common/Slider.vue'
  import conutDown from './common/conutDown.vue'
  import BottomNav from './common/BottomNav.vue'
  import Pagination from './common/Pagination.vue'

  export default {
    name: 'Home',
    components: {
      Slider,
      conutDown,
      BottomNav,
      Pagination
    },
    data() {
      return {
        data: [],
        activityEndTime: '',
        userList: [],
        odd: [],
        even: [],
        page: 1, // 当前页数
        loading: false,
        latesLoading: false,
        scroll: true,
        activeName: 'first',
        keyword: '',
        complaintInfo: {
          title: "首页"
        },
        pageSize: 10,
        pageNum: 1,
        total: 0,
        openid: (() => this.$cookies.get('openId'))(),
        popularityPlayers: [],
        activityId: this.$cookies.get("activityId"),
        overStatus: this.$cookies.get("overStatus"),
        defaultStatus: this.$cookies.get("defaultStatus"),
        code: this.getUrlKey('code'),
        voteStatus: this.$cookies.get("voteStatus"),
        totalCount: 4,
        tabHeight: 20,
        sortBy:'num'
      }
    },
    created() {
   //   this.$cookies.set('activityId', this.getUrlKey('activityId'), '30d')
      this.getH5HomePage( this.getUrlKey('activityId'))
      this.$parent.getHomeMessage( this.getUrlKey('activityId'))

    },
    methods: {
      async getOpenid(code) {
        if (this.code) {
          let {error, data, msg} = await this.$api.weChat.getOpenId({code: code})
          if (error) return this.$message.error(msg)
          data = JSON.parse(data)
          this.$cookies.set('openId', data.openid, '0')
          this.$parent.hasJoin(this.getUrlKey('activityId'), data.openid)
        }
      },
      signBtn() {
        if (this.overStatus == "true") {
          this.$message.error("活动已结束,无法报名！")
        } else if (this.defaultStatus == "true") {
          this.$message.error("活动还未开启,无法报名！")
        } else {
          if (this.voteStatus == 0) {
            this.$router.push({
              path: '/SignUpAfter',
              query: {id: this.playerId, activityId:  this.getUrlKey('activityId'), name: 'index'}
            });
          } else {
            this.$router.push({path: '/signUp', query: {activityId:  this.getUrlKey('activityId')}});
          }
        }
      },
      getUrlKey: function (name) { // 获取url 参数
        return decodeURIComponent((new RegExp('[?|&]' + name + '=' + '([^&;]+?)(&|#|;|$)').exec(location.href) || [, ''])[1].replace(/\+/g, '%20')) || null
      },
      handleClick(tab, event) {
        console.log(tab, event)
      },
      async getH5HomePage(activityId) {
        this.latesLoading = true
        const {error, msg, data, code} = await this.$api.h5.getH5HomePage({activityId})
        if (error) return this.$message.error(msg)
        this.latesLoading = false
        console.log("**************", this.openid)
       /* if (!this.openid) {
          this.getOpenid(this.code)
        } else {
          this.$parent.hasJoin(this.getUrlKey('activityId'), this.$cookies.get("openId"))
        }*/
        this.$parent.hasJoin(this.getUrlKey('activityId'), this.$cookies.get("openId"))
        if (code == 502) {
          this.$router.push({path: '/404'})
        }
        this.data = data
        if(data.votes>0){
          this.sortBy='votes desc'
        }
        this.activityEndTime = data.activityEndTime
        this.total = this.data.playerNum
        this.pagination(1)

      },
      async pagination(page) {
        this.loading = true
        const params = {
          activityId:  this.getUrlKey('activityId'),
          page: page,
          size: this.pageSize,
          sort:this.sortBy
        }
        const {error, msg, data} = await this.$api.h5.getPlayer(params)
        this.loading = false
        this.popularityPlayers = data.list
        if (error) return this.$message.error(msg)
      },
      search() {
        if (this.keyword) {
          this.getSearch(1)
        }
      },
      async getSearch(page) {
        const params = {
          activityId:  this.getUrlKey('activityId'),
          search: this.keyword,
          page: page,
          size: this.pageSize
        }
        const {error, msg, data} = await this.$api.h5.getPlayer(params)
        this.popularityPlayers = data.list
        if (error) return this.$message.error(msg)
      },
      handleSizeChange(val) {
        this.pageSize = val
        this.pagination(val)
      },
      handleCurrentChange(val) {
        this.pageNum = val
        this.pagination(val)
      }
    }
  }
</script>

<style lang="scss">
  #home {
    .el-tabs__nav {
      background: #fff;
      width: 100%;
      padding: 0.5rem 0;
      font-size: 0.7rem;
      border-bottom: 1px solid #e5e5e5;
      .el-tabs__item {
        padding: 0;
        width: 50%;
        border-right: 1px solid #ccc;
        box-sizing: border-box;
        height: 1rem;
        line-height: 1rem;
        font-size: 0.7rem;
      }

      .el-tabs__item:last-child {
        border-right: 0
      }
    }

    .index-voteNum {
      font-size: 0.55rem;
      background: #CB8427;
    }

    .text-hide-2 {
      font-size: 0.55rem
    }

    .hot li {
      width: 47%;
      margin: 10px 0 0 2%;
      float: left;
      text-align: left;
      font-size: 0.55rem;
      border: 1px solid #dcdcde;
      box-sizing: border-box;
      background: #fff;

      .pic {
        height: 6rem;
        line-height: 6rem;
        text-align: center;
        border-bottom: 1px solid #ccc;
      }
    }

    .hot li p {
      padding: 0 10px
    }

    .index-vote {
      right: 10px;
      width: 2.2rem;
      font-size: 0.55rem
    }

    .el-tabs__header {
      margin: 0
    }

    .searchWrap {
      width: 96%;
      margin: 10px auto 0;

      .el-input__inner {
        min-height: 1.2rem;
        border: 1px solid #dcdfe6;
        font-size: 0.55rem;
        text-indent: 10px;
        border-right: 0;
        border-top-left-radius: 5px;
        border-bottom-left-radius: 5px;
        border-bottom-left-radius: 5px;
      }

      .el-button--primary {
        background: #409EFF;
        border-color: #409EFF;
        color: #fff;
        border-top: 1px solid #409EFF
      }
    }

    .time {
      li {
        border-bottom: 1px solid #ccc;
        text-align: left;
        font-size: 0.6rem;
        padding: 0.3rem 10px;
        img {
          width: 0.6rem;
          height: 0.6rem;
          float: left;
          margin-top: 3px;
        }

        b {
          font-weight: 400
        }
      }

    }

    .pic img {
      width: 100%;
      height: 100%;
    }
  }

  .playerTitle {
    width: 60%;
    line-height: 2;
    font-size: .6rem;
    height: 1.8rem;
    line-height: 0.9rem;
    overflow: hidden;
    text-overflow: -o-ellipsis-lastline;
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    line-clamp: 2;
    -webkit-box-orient: vertical;
  }

  .time {
    color: #666;
    font-size: 0.7rem;
  }

  .time span {
    color: #000;
    font-weight: 800
  }

  .centerInfo {
    width: 96%;
    border-radius: 8px;
    margin: 10px auto 0;
    padding: 0.5rem 0
  }

  .btnList {
    .el-button {
      width: 31%
    }
  }
</style>
