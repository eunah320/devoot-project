<template>
    <div class="flex flex-row h-screen bg-gray-100">
        <!-- 네비게이션 바 -->
        <AppNavigation class="fixed" />

        <!-- 화면 (1440px 제한 & 가운데 정렬) -->
        <div class="flex justify-center flex-1">
            <!-- 헤더 -->
            <AppHeader v-if="shouldShowHeader" :type="headerType" />

            <!-- 바디 -->
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
</style>
