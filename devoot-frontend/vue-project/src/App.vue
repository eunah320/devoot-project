<template>
    <div class="flex flex-row">
        <!-- 네비게이션 바 -->
        <AppNavigation class="fixed" />

        <!-- 화면 (1440px 제한 & 가운데 정렬) -->
        <div class="flex justify-center flex-1">
            <div
                id="container"
                class="grid w-full max-w-[1440px] ml-[4.5rem] lg:ml-[13.5rem] grid-cols-12 gap-6 overflow-y-auto px-9"
            >
                <router-view class="col-span-12" />
            </div>
        </div>
    </div>
</template>

<script setup>
import AppNavigation from './components/Layout/AppNavigation.vue'
</script>

<style scoped></style>
