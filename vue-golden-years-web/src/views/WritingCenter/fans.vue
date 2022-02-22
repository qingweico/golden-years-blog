<template>
  <div class="main-page">
    <div class="title-box">
      <div class="title-wrapper">
        <span class="title-word">所有粉丝</span>
      </div>
    </div>

    <div class="all-fans-wrapper">
      <div class="single-fan" v-for="(fan, fanIndex) in fansList" v-key="fanIndex">
        <a target="_blank"
           class="fan-face"
           @click="passive(fan.id, fan.fanId)">
          <img :src="fan.face" class="fan-face" alt="face"/>
        </a>

        <div class="fan-nickname">{{ fan.fanNickname }}</div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "fans",
  data() {
    return {
      fansList: []
    }
  },
  methods: {
    passive() {
    }
  }
}
</script>

<style scoped>
.main-page {
  width: 980px;
  margin-left: 20px;
  background-color: white;
  padding-bottom: 40px;
}

.title-box {
  padding: 20px 0 10px 30px;
  border-bottom: 1px solid #e8e8e8;
}

.title-word {
  color: #c9394a;
  font-size: 20px;
}

.all-fans-wrapper {
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