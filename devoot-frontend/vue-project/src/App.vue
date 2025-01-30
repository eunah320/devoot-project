<template>
    <div class="flex flex-row h-screen">
        <!-- 네비게이션 바 -->
        <AppNavigation class="fixed h-full" />

        <!-- 화면 컨테이너 -->
        <div class="flex flex-col flex-1 ml-[4.5rem] lg:ml-[13.5rem]">
            <!-- 헤더 -->
            <AppHeader :type="headerType" />

            <!-- 본문 -->
            <div id="container" class="grid flex-1 grid-cols-12 gap-6 overflow-y-auto px-9">
                <router-view class="col-span-12" />
            </div>
        </div>
    </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import AppNavigation from './components/layout/AppNavigation.vue'
import AppHeader from './components/layout/AppHeader.vue'

// 현재 경로에 따라 헤더 타입 결정
const route = useRoute()
const headerType = computed(() => {
    const path = route.path
    if (path === '/' || path.startsWith('/lecture')) {
        return 'lecture'
    } else if (path.startsWith('/timeline') || path.startsWith('/profile')) {
        return 'user'
    }
    return 'default'
})
</script>

<style scoped>
#container {
    min-height: calc(100vh - h-20); /* 헤더 높이를 제외한 영역 */
}
</style>
