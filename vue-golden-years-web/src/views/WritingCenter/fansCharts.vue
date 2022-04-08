<template>
  <div class="main-page">
    <div class="title-box">
      <div class="title-wrapper">
        <span class="title-word">粉丝画像</span>
      </div>
    </div>
    <div class="all-charts-wrapper">
      <div class="sex-label">男女比例</div>
      <!-- 男女比例 -->
      <div class="sex-wrapper">

        <div class="sex-line-wrapper">
          <div id="manPercent" class="sex-man-line" style="width: 60%"></div>
          <div id="womanPercent" class="sex-woman-line" style="width: 40%;"></div>
        </div>

        <div class="sex-block-wrapper">
          <div class="sex-all-counts">总粉丝数：{{ manCounts + womanCounts }}人</div>
          <div class="sex-man-counts">
            <div class="sex-man-block"></div>
            男性：{{ manCounts }}人,占比 {{ getPercent(manCounts / (manCounts + womanCounts)) }}%
          </div>
          <div class="sex-woman-counts">
            <div class="sex-woman-block"></div>
            女性：{{ womanCounts }}人,占比 {{ getPercent(womanCounts / (manCounts + womanCounts)) }}%
          </div>
        </div>
      </div>
      <!-- 男女比例柱状图 -->
      <div class="histogram-wrapper">
        <div id="sexHistogram" style="width: 800px;height:500px;"></div>
      </div>
      <div class="pie-wrapper">
        <div id="sexPie" style="width: 800px;height:400px;padding: 15px;"></div>
      </div>

      <div class="city-label">地域分布</div>
      <div class="map-wrapper">
        <div id="mapChart" style="width: 800px;height:500px;"></div>
      </div>

    </div>
  </div>
</template>

<script>
import echarts from 'echarts'
import 'echarts/map/js/china.js'
import {queryRatio, queryRatioByRegion} from "@/api/fans";
import {mapGetters} from "vuex";

export default {
  name: "fansCharts",
  data() {
    return {
      manCounts: 0,
      womanCounts: 0,
      userInfo: {},
    }
  },
  created() {
    this.userInfo = this.getUserInfo();
  },
  methods: {
    ...mapGetters(['getUserInfo']),
    // 根据地域查询粉丝数量
    queryRatioByRegion() {
      let params = new URLSearchParams();
      params.append("userId", this.userInfo.id);
      queryRatioByRegion(params).then(res => {
        let list = res.data;
        // 初始化图表
        this.createMapChart(list);
      });
    },
    // 查询男女比例
    queryRatio() {
      let params = new URLSearchParams();
      params.append("userId", this.userInfo.id);
      queryRatio(params).then(res => {
        let manCounts = res.data.manCounts;
        let womanCounts = res.data.womanCounts;

        this.manCounts = manCounts;
        this.womanCounts = womanCounts;

        let manPercent = this.getPercent(manCounts / (manCounts + womanCounts)) + "%";
        $("#manPercent").css({width: manPercent});
        let womanPercent = this.getPercent(womanCounts / (manCounts + womanCounts)) + "%";
        $("#womanPercent").css({width: womanPercent});

        // 初始化图表
        this.createHistogram(manCounts, womanCounts);
        this.createPie(manCounts, womanCounts);
      });
    },
    // 转换百分数
    getPercent(number) {
      if (isNaN(number)) {
        return 0;
      }
      return Number(number * 100).toFixed(2)
    },
    // 创建柱状图
    createHistogram(manCounts, womanCounts) {
      // 基于准备好的dom, 初始化echarts实例
      const myChart = echarts.init(document.getElementById('sexHistogram'), 'walden');
      // 指定图表的配置项和数据
      let option = {
        title: {},
        tooltip: {
          trigger: 'axis',
          // 坐标轴指示器,坐标轴触发有效
          axisPointer: {
            // 默认为直线,可选为; 'line' | 'shadow'
            type: 'shadow'
          }
        },
        legend: {
          data: ['粉丝数量']
        },
        xAxis: {
          data: ["男性", "女性", "全部粉丝"]
        },
        yAxis: {},
        series: [{
          type: 'bar',
          data: [manCounts, womanCounts, manCounts + womanCounts]
        }]
      };
      // 使用刚指定的配置项和数据显示图表
      myChart.setOption(option);
    },
    // 饼状图
    createPie(manCounts, womanCounts) {
      const myChart = echarts.init(document.getElementById('sexPie'), 'walden');
      let option = {
        tooltip: {
          trigger: 'item',
          formatter: '{b} : {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          left: 'left',
          data: ['男性比例', '女性比例']
        },
        series: [
          {
            type: 'pie',
            radius: '55%',
            center: ['50%', '60%'],
            data: [
              {value: manCounts, name: '男性比例'},
              {value: womanCounts, name: '女性比例'}
            ],
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            }
          }
        ]
      };
      myChart.setOption(option);
    },
    // 根据不同地域粉丝数,在地图上展示
    createMapChart(list) {

      const data = [
        // 4个直辖市
        {name: '北京', value: 1000}, {name: '天津', value: 596},
        {name: '上海', value: 102}, {name: '重庆', value: 6320},


        // 23个省
        {name: '河北', value: 2000}, {name: '山西', value: 300},
        {name: '辽宁', value: 1111}, {name: '吉林', value: 600},
        {name: '黑龙江', value: 15687}, {name: '江苏', value: 1111},
        {name: '浙江', value: 5987}, {name: '安徽', value: 9631},
        {name: '福建', value: 607895}, {name: '江西', value: 4214},
        {name: '山东', value: 12143}, {name: '河南', value: 11057},
        {name: '湖北', value: 2222}, {name: '湖南', value: 8693},
        {name: '广东', value: 63214}, {name: '海南', value: 15978},
        {name: '四川', value: 1010102}, {name: '贵州', value: 708},
        {name: '云南', value: 23987}, {name: '陕西', value: 489},
        {name: '甘肃', value: 1047}, {name: '青海', value: 96874},
        {name: '台湾', value: 1111},

        // 5个自治区
        {name: '内蒙古', value: 1111}, {name: '广西', value: 3621},
        {name: '西藏', value: 120147}, {name: '宁夏', value: 32104},
        {name: '新疆', value: 6354},

        // 2个特别行政区
        {name: '香港', value: 26874}, {name: '澳门', value: 879631}
      ];

      let optionMap = {
        tooltip: {
          trigger: 'item'
        },
        visualMap: {
          min: 1,
          // 粉丝数范围,可以自定义
          max: 20,
          text: ['High', 'Low'],
          realtime: false,
          calculable: true,
          inRange: {
            color: ['#5475f5', '#9feaa5', '#85daef', '#74e2ca', '#e6ac53', '#9fb5ea']
          }
        },
        //配置属性
        series: [{
          name: '数据',
          type: 'map',
          mapType: 'china',
          roam: true,
          label: {
            normal: {
              // 省份名称
              show: true
            },
            emphasis: {
              show: false
            }
          },
          data: list
        }]
      };
      // 初始化echarts实例
      let myChart = echarts.init(document.getElementById('mapChart'));
      // 使用制定的配置项和数据显示图表
      myChart.setOption(optionMap);
    },
  },
  mounted() {

    // 查询男女比例
    this.queryRatio();
    // 根据地域查询粉丝数量
    this.queryRatioByRegion();

    // 初始化图表
    this.createHistogram();
    // 初始化饼状图
    this.createPie(12, 23);
    // 初始化地图
    this.createMapChart();
  },
}
</script>

<style scoped>
.main-page {
  width: 980px;
  margin-left: 20px;
  background-color: white;
  padding-bottom: 40px;
  display: flex;
  flex-direction: column;
}

.all-charts-wrapper {
  padding: 20px 30px;
  align-self: center;
}

/* 横向线型图 */
.sex-label {
  font-size: 16px;
  color: #222;
}

.sex-wrapper {
  width: 820px;
  margin-top: 10px;
  border: 1px solid #e8e8e8;
  padding: 20px 10px;

  display: flex;
  flex-direction: column;
}

.sex-line-wrapper {
  width: 800px;
  display: flex;
  flex-direction: row;

  align-self: center;
}

.sex-man-line {
  background-color: rgb(107, 208, 107);
  height: 25px;
}

.sex-woman-line {
  background-color: rgb(219, 135, 135);
  height: 25px;
}

.sex-block-wrapper {
  align-self: flex-start;
  margin-left: 10px;
  margin-top: 15px;
}

.sex-all-counts {
  font-size: 14px;
  color: #222;
}

.sex-man-counts {
  font-size: 14px;
  color: #222;

  display: flex;
  flex-direction: row;
  margin-top: 3px;
}

.sex-woman-counts {
  font-size: 14px;
  color: #222;

  display: flex;
  flex-direction: row;
  margin-top: 3px;
}

.sex-man-block {
  width: 18px;
  height: 18px;
  background-color: rgb(107, 208, 107);
  margin-right: 8px;
}

.sex-woman-block {
  width: 18px;
  height: 18px;
  background-color: rgb(219, 135, 135);
  margin-right: 8px;
}


/* 柱状图 */
.histogram-wrapper {
  border-bottom: 1px solid #e8e8e8;
  border-left: 1px solid #e8e8e8;
  border-right: 1px solid #e8e8e8;
}


/* 饼状图 */
.pie-wrapper {
  border-bottom: 1px solid #e8e8e8;
  border-left: 1px solid #e8e8e8;
  border-right: 1px solid #e8e8e8;
}

/* 地图地域显示 */
.city-label {
  font-size: 16px;
  color: #222;
  margin-top: 20px;
}

.map-wrapper {
  border: 1px solid #e8e8e8;
  margin-top: 10px;
}
</style>