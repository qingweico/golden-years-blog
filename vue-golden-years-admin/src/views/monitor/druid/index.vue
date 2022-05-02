<template>
  <i-frame :src="url"/>
</template>
<script>
import iFrame from "@/components/iFrame/index";

export default {
  name: "Druid",
  components: {iFrame},
  data() {
    return {
      url: process.env.GATEWAY_API + "/druid/login.html"
    };
  },
};
</script>
