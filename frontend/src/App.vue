<script setup>
import NavBar from "./components/NavBar.vue";
import { RouterView } from "vue-router";
</script>
<template>
  <div class="d-flex flex-column min-vh-100">
    <NavBar />
    <main class="main-content d-flex flex-column flex-grow-1">
      <RouterView />
    </main>
  </div>
</template>
