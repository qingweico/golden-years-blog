<template>
<div class="container">
  <div id="title">
    <span class="time"></span>
    <span style="color: black">全国疫情实时展示</span>
  </div>
  <div id="up-left"></div>
  <div id="bottom-left"></div>
  <div id="up-center">
    <div class="item">
      <div class="number confirm"></div>
      <div class="content">累计确诊</div>
    </div>
    <div class="item">
      <div class="number heal"></div>
      <div class="content">累计治愈</div>
    </div>
    <div class="item">
      <div class="number dead"></div>
      <div class="content">累计死亡</div>
    </div>
    <div class="item">
      <div class="number localConfirm"></div>
      <div class="content">现有确诊</div>
    </div>
    <div class="item">
      <div class="number noInfect"></div>
      <div class="content">无症状感染者</div>
    </div>
    <div class="item">
      <div class="number importedCase"></div>
      <div class="content">境外输入</div>
    </div>
  </div>
  <div id="bottom-center"></div>
  <div id="upright"></div>
  <div id="bottom-right"></div>
</div>
</template>
<script>
import echarts from 'echarts'
import 'echarts/map/js/china.js'
export default {
  data() {
    return {}
  },
  created() {
    this.getData();
    setInterval(this.showTime, 1000);
  },
  methods: {
    // 显示实时时间
    showTime() {
      let time = new Date();
      let year = time.getFullYear();
      let month = (time.getMonth() + 1 + '').padStart(2, '0');
      let day = (time.getDate() + '').padStart(2, '0');
      let hour = (time.getHours() + '').padStart(2, '0');
      let minute = (time.getMinutes() + '').padStart(2, '0');
      let second = (time.getSeconds() + '').padStart(2, '0');
      let content = `${year}年${month}月${day}日 ${hour}:${minute}:${second}`;
      $('#title .time').text(content);
    },

    getData() {
      $.ajax({
        url: 'https://view.inews.qq.com/g2/getOnsInfo?name=disease_h5',
        data: {
          name: 'disease_h5'
        },
        dataType: 'jsonp',
        success: (res) => {
          let data = JSON.parse(res.data);
          this.upCenter(data);
          this.bottomCenter(data);
          this.upRight(data);
          this.bottomRight(data);
        }
      });
      $.ajax({
        url: 'https://api.inews.qq.com/newsqa/v1/query/inner/publish/modules/list',
        type: 'post',
        data: {
          modules: 'chinaDayList,chinaDayAddList,nowConfirmStatus,provinceCompare'
        },
        dataType: 'json',
        success: (res) => {
          this.upLeft(res.data);
          this.bottomLeft(res.data);
        }
      })
    },
    upCenter(data) {
      $(".confirm").text(data.chinaTotal.confirm);
      $(".heal").text(data.chinaTotal.heal);
      $(".dead").text(data.chinaTotal.dead);
      $(".localConfirm").text(data.chinaTotal.localConfirm);
      $(".noInfect").text(data.chinaTotal.noInfect);
      $(".importedCase").text(data.chinaTotal.importedCase);
    },

    bottomCenter(data) {

      let myChart = echarts.init(document.getElementById('bottom-center'), 'dark');

      let option = {
        title: {
          text: ''
        },
        tooltip: {
          trigger: 'item'
        },
        visualMap: {
          show: true,
          x: 'left',
          y: 'bottom',
          textStyle: {
            fontSize: 8
          },
          // 左侧小导航图标
          splitList: [
            {start: 1, end: 9},
            {start: 10, end: 99},
            {start: 100, end: 999},
            {start: 1000, end: 9999},
            {start: 10000}
          ],
          color: ['#8A3310', '#C64918', '#E55B25', '#F2AD92', '#F9DCD1']
        },
        series: [
          {
            name: '累计确诊人数',
            type: 'map',
            mapType: 'china',
            // 禁用拖动和缩放
            roam: false,
            // 图形样式
            itemStyle: {
              normal: {
                // 区域边框高度
                borderWidth: 0.5,
                // 区域边框颜色
                borderColor: '#009fe8',
                // 区域颜色
                areaColor: '#ffefd5'
              },
              // 鼠标滑过地图高亮的设置
              emphasis: {
                borderWidth: 0.5,
                borderColor: '#4b0082',
                areaColor: '#fff'
              }
            },
            // 图形上的文本标签
            label: {
              normal: {
                show: true,
                fontSize: 8
              },
              emphasis: {
                show: true,
                fontSize: 8
              }
            },
            data: []
          }
        ]
      };

      let provinces = data.areaTree[0].children;
      for (let province of provinces) {
        let obj = {};
        obj.name = province.name;
        obj.value = province.total.confirm;
        option.series[0].data.push(obj);
      }
      myChart.setOption(option);
    },

    upRight(data) {
      let myChart = echarts.init(document.getElementById('upright'), 'dark');
      let option = {
        title: {
          text: '全国确诊省市TOP10',
          textStyle: {
            color: 'white'
          },
          left: 'left'
        },
        color: ['#3398DB'],
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          }
        },
        xAxis: {
          type: 'category',
          data: []
        },
        yAxis: {
          type: 'value',
          axisLabel: {
            show: true,
            color: 'white',
            fontSize: 12,
            formatter: function (value) {
              if (value >= 1000) {
                value = value / 1000 + 'k';
              }
              return value;
            }
          },
        },
        series: [
          {
            data: [],
            type: 'bar',
            barMaxWidth: "50%"
          }
        ]
      };

      let topData = [];
      let provinces = data.areaTree[0].children;
      for (let province of provinces) {
        let obj = {};
        obj.name = province.name;
        obj.value = province.total.confirm;
        topData.push(obj);

      }
      topData.sort((a, b) => {
        return b.value - a.value;
      });

      topData = topData.slice(0, 10);
      for (let province of topData) {
        option.xAxis.data.push(province.name);
        option.series[0].data.push(province.value);
      }
      myChart.setOption(option);
    },

    bottomRight(data) {
      let myChart = echarts.init(document.getElementById('bottom-right'), 'dark');

      let option = {
        title: {
          text: '境外输入TOP5',
          left: 'center'
        },
        tooltip: {
          trigger: 'item'
        },
        legend: {
          orient: 'vertical',
          left: 'left',
          data: []
        },
        series: [
          {
            name: '省市名称',
            type: 'pie',
            radius: '50%',
            data: [],
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
      let topData = [];
      let provinces = data.areaTree[0].children;
      for (let province of provinces) {
        province.children.forEach((item) => {
          if (item.name === '境外输入') {
            let obj = {};
            obj.name = province.name;
            obj.value = item.total.confirm;
            topData.push(obj);
          }
        })
      }
      topData.sort((a, b) => {
        return b.value - a.value;
      });

      topData.length = 5;
      for (let province of topData) {
        option.legend.data.push(province.name);
        option.series[0].data.push(province);
      }
      myChart.setOption(option);
    },

    upLeft(data) {
      let myChart = echarts.init(document.getElementById('up-left'), 'dark');

      let option = {
        title: {
          text: '全国累计趋势',
          textStyle: {
            color: 'white'
          },
          left: 'left'
        },
        tooltip: {
          trigger: 'axis',
          // 指示器
          axisPointer: {
            type: 'line',
            lineStyle: {
              color: '#7171C6'
            }
          }
        },
        legend: {
          data: ['累计确诊', '累计治愈', '累计死亡'],
          left: 'right'
        },
        grid: {
          left: '4%',
          right: '6%',
          bottom: '4%',
          top: 50,
          containLabel: true
        },
        xAxis: [
          {
            type: 'category',
            boundaryGap: false,
            data: []
          }
        ],
        yAxis: [{
          type: 'value',
          // y轴字体设置
          axisLabel: {
            show: true,
            fontSize: 12,
            color: 'white',
            formatter: function (value) {
              if (value > 1000) {
                value = value / 100 + 'k';
              }
              return value;
            },
            // y轴线设置
            axisLine: {
              show: true
            },
            // 与x轴平行的线样式
            splitLine: {
              show: true,
              lineStyle: {
                color: '#17273B',
                width: 1,
                type: 'solid'
              }
            }
          }
        }],
        series: [
          {
            name: '累计确诊',
            type: 'line',
            smooth: true,
            data: []
          },
          {
            name: '累计治愈',
            type: 'line',
            smooth: true,
            data: []
          },
          {
            name: '累计死亡',
            type: 'line',
            smooth: true,
            data: []
          },
        ]
      };
      let chinaDayList = data.chinaDayList;
      for (let day of chinaDayList) {
        option.xAxis[0].data.push(day.date);
        option.series[0].data.push(day.confirm);
        option.series[1].data.push(day.heal);
        option.series[2].data.push(day.dead);
      }

      myChart.setOption(option);
    },

    bottomLeft(data) {
      let myChart = echarts.init(document.getElementById('bottom-left'), 'dark');

      let option = {
        title: {
          text: '全国新增趋势',
          textStyle: {
            color: 'white'
          },
          left: 'left'
        },
        tooltip: {
          trigger: 'axis',
          // 指示器
          axisPointer: {
            type: 'line',
            lineStyle: {
              color: '#7171C6'
            }
          }
        },
        legend: {
          data: ['新增确诊', '新增疑似'],
          left: 'right'
        },
        grid: {
          left: '4%',
          right: '6%',
          bottom: '4%',
          top: 50,
          containLabel: true
        },
        xAxis: [
          {
            type: 'category',
            boundaryGap: false,
            data: []
          }
        ],
        yAxis: [{
          type: 'value',
          // y轴字体设置
          axisLabel: {
            show: true,
            fontSize: 12,
            color: 'white',
            formatter: function (value) {
              if (value > 1000) {
                value = value / 100 + 'k';
              }
              return value;
            },
            // y轴线设置
            axisLine: {
              show: true
            },
            // 与x轴平行的线样式
            splitLine: {
              show: true,
              lineStyle: {
                color: '#17273B',
                width: 1,
                type: 'solid'
              }
            }
          }
        }],
        series: [
          {
            name: '新增确诊',
            type: 'line',
            smooth: true,
            data: []
          },
          {
            name: '新增疑似',
            type: 'line',
            smooth: true,
            data: []
          }
        ]
      };
      let chinaDayAddList = data.chinaDayAddList;
      for (let day of chinaDayAddList) {
        option.xAxis[0].data.push(day.date);
        option.series[0].data.push(day.confirm);
        option.series[1].data.push(day.suspect);
      }
      myChart.setOption(option);
    }
  }
}
</script>
<style scoped>
.container {
  background-color: #100c2a;
}
#title {
  width: 100%;
  height: 10%;
  position: absolute;
  top: 0;
  left: 0;

  /*弹性布局*/
  display: flex;
  justify-content: center;
  align-items: center;
  color: white;
  font-size: 40px;
}

#title .time {
  color: #100c2a;
  position: absolute;
  font-size: 20px;
  left: 10px;
}

#up-left {
  width: 30%;
  height: 45%;
  position: absolute;
  top: 10%;
  left: 0;
}

#bottom-left {
  width: 30%;
  height: 45%;
  position: absolute;
  top: 55%;
  left: 0;
}

#up-center {
  width: 40%;
  height: 25%;
  position: absolute;
  top: 10%;
  left: 30%;
  display: flex;
  flex-wrap: wrap;
}

.item {
  width: 33.33%;

}

.number {
  height: 60%;
  display: flex;
  justify-content: center;
  align-items: center;
  color: #920784;
  font-size: 40px;
  font-weight: bold;

}

.content {
  height: 40%;
  display: flex;
  justify-content: center;
  align-items: center;
  color: #100c2a;
  font-size: 20px;
  font-weight: bold;
}

#bottom-center {
  width: 40%;
  height: 65%;
  position: absolute;
  top: 35%;
  left: 30%;
}

#upright {
  width: 30%;
  height: 45%;
  position: absolute;
  top: 10%;
  right: 0;
}

#bottom-right {
  width: 30%;
  height: 45%;
  position: absolute;
  top: 55%;
  right: 0;
}
</style>