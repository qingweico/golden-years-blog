<template>
  <div>
    <svg-icon class="websiteSvg" icon-class="website" @click.native="goto" style="height: 2.5em;font-size: 18px;cursor: pointer" />
  </div>
</template>

<script>
export default {
  name: 'qingweico',
  data() {
    return {
      url: "http://blog.qingweico.cn"
    }
  },
  methods: {
    goto() {
      window.open(this.url)
    }
  }
}
</script>
<style scoped>

</style>
