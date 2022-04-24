<template>
  <div class="main-page">
    <div class="title-box">
      <div class="title-wrapper">
        <span class="title-word">所有粉丝</span>
      </div>
    </div>

    <div class="all-fans-wrapper" v-if="fansList.length">
      <div class="single-fan" v-for="(fan, fanIndex) in fansList" :key="fanIndex">
        <a target="_blank"
           class="fan-face"
           @click="goUserHomepage(fan)">
          <img :src="fan.face" class="fan-face" alt="face"/>
        </a>

        <div class="fan-nickname">{{ fan.fanNickname }}</div>
      </div>
    </div>
    <el-empty description="你还没有粉丝" v-else style="background-color: white;"></el-empty>

    <!--分页-->
    <div class="block paged" v-if="fansList.length">
      <el-pagination
          @current-change="handleCurrentChange"
          :current-page.sync="currentPage"
          :page-size="pageSize"
          layout="total, prev, pager, next, jumper"
          :total="totalPage">
      </el-pagination>
    </div>
  </div>
</template>

<script>
import {passive, queryFansList} from "@/api/fans";
import {mapGetters} from "vuex";

export default {
  name: "fans",
  data() {
    return {
      fansList: [],
      currentPage: 1,
      pageSize: 10,
      records: 0,
      totalPage: 0,
      userInfo: {},
    }
  },
  created() {
    this.userInfo = this.getUserInfo();
    this.queryFansList();

  },
  methods: {
    ...mapGetters(['getUserInfo']),
    // 被动更新粉丝信息
    passive(relationId, fanId) {
      let params = {};
      params.relationId = relationId;
      params.fanId = fanId;
      passive(params)
    },
    goUserHomepage(fan) {
      this.passive(fan.id, fan.fanId);
      this.$router.push({path: '/homepage', query: {id: fan.fanId}})

    },
    handleCurrentChange(val) {
      this.currentPage = val;
      this.queryFansList();
    },

    queryFansList() {
      let params = new URLSearchParams();
      params.append("userId", this.userInfo.id);
      params.append("page", this.currentPage);
      params.append("pageSize", this.pageSize);
      queryFansList(params).then(res => {
        let content = res.data;
        this.fansList = content.rows;
        this.records = content.records;
        this.currentPage = content.currentPage;
        this.totalPage = content.totalPage;
      });
    },
  }
}
</script>

<style scoped>
.all-fans-wrapper {
  background-color: white;
  padding: 20px 30px;
  display: flex;
  flex-direction: row;
  justify-content: flex-start;
  flex-wrap: wrap;
}

.single-fan {
  width: 160px;
  height: 150px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  border: 1px solid #e8e8e8;
  border-radius: 5px;
  box-shadow: 0 0 3px #f4f2f2;
  margin-left: 10px;
  margin-top: 10px;
}

.single-fan:hover {
  box-shadow: 0 0 3px #c4c2c2;
}

.fan-face {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  align-self: center;
}

.fan-nickname {
  margin-top: 10px;
  align-self: center;
  color: #555;
  font-size: 14px;
}
</style>