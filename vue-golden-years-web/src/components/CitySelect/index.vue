<template>
  <div class="citiesLinkage">
    <el-select class="provinces" v-model="province" @change="choiceProvince" placeholder="请选择省份">
      <el-option v-for="item in provincesName" :key="item" :value="item"></el-option>
    </el-select>
  </div>
</template>
<script>
import {addrobj, addrname} from '@/assets/addr'

export default {
  name: 'CitySelect',
  props: {
    province: {
      type: String
    }
  },
  data() {
    return {
    }
  },
  computed: {
    provincesName() {
      let provinceName = {};
      for (let i in addrobj) {
        provinceName[i] = addrname[i];
      }
      return provinceName
    },
    cityName() {
      let cityName = {};
      for (let i in addrobj[this.province]) {
        cityName[i] = addrname[i];
      }
      return cityName
    },
    countryName() {
      let countyName = {};
      for (let i in addrobj[this.province][this.city]) {
        let county = addrobj[this.province][this.city][i];
        countyName[county] = addrname[county]
      }
      return countyName
    },
  },
  watch: {
    'province': function (n, o) {
      if (n !== o) this.city = Object.getOwnPropertyNames(this.cityName)[0];
    },
    'city': function (n, o) {
      if (n !== o) this.country = Object.getOwnPropertyNames(this.countryName)[0];
    }
  },
  methods: {
    choiceProvince() {
      this.$emit('province',this.province)
    }
  }
}
</script>


<style scoped>

</style>
