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
      url: process.env.PIC_API + "/swagger-ui/index.html"
    };
  },
};
</script>
