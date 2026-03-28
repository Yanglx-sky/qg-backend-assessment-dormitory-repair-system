<template>
  <div id="app">
    <router-view></router-view>
  </div>
</template>

<script setup>
// App.vue 主组件
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  background: linear-gradient(135deg, #87CEEB 0%, #4682B4 100%);
  margin: 0;
  padding: 0;
  min-height: 100vh;
}

#app {
  min-height: 100vh;
}
</style>