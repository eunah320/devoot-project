<template>
    <div class="flex flex-row">
        <!-- 네비게이션 바 -->
        <AppNavigation class="fixed" />
        <AppNavigation class="fixed" />

        <!-- 화면 -->
        <div
            id="container"
            class="grid ml-[4.5rem] lg:ml-[13.5rem] flex-1 grid-cols-12 gap-6 overflow-y-auto px-9"
        >
            <router-view class="col-span-12" />
            <router-view class="col-span-12" />
        </div>
    </div>
</template>

<script setup>
import AppNavigation from './components/layout/AppNavigation.vue'
</script>

<style scoped></style>
