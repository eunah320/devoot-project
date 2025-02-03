<template>
    <div class="flex flex-row h-screen bg-gray-100">
        <!-- 네비게이션 바 -->
        <AppNavigation class="fixed h-full" />

        <!-- 화면 컨테이너 -->
        <div class="flex flex-col flex-1 ml-[4.5rem] lg:ml-[13.5rem]">
            <!-- 헤더 -->
            <AppHeader v-if="shouldShowHeader" :type="headerType" />

            <!-- 본문 -->
            <div id="container" class="flex flex-col flex-1 overflow-y-auto px-9">
                <!-- 라우터 뷰 -->
                <router-view />
            </div>
        </div>
    </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import AppNavigation from './components/layout/AppNavigation.vue'
import AppHeader from './components/layout/AppHeader.vue'

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

const shouldShowHeader = computed(() => {
    const hiddenPaths = ['/login', '/profile/:id/edit']
    const currentPath = route.path
    return !hiddenPaths.some((hiddenPath) => {
        const regex = new RegExp(`^${hiddenPath.replace(':id', '[^/]+')}$`)
        return regex.test(currentPath)
    })
})
</script>

<style scoped>
#container {
    min-height: calc(100vh - 5rem);
}
</style>
