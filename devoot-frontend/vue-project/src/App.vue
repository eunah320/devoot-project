<template>
    <div class="flex h-scree">
        <!-- 네비게이션 바 -->
        <AppNavigation class="fixed" />

        <!-- 메인 컨텐츠 영역 -->
        <div class="flex flex-col flex-1 ml-[4.5rem] lg:ml-[13.5rem] overflow-hidden">
            <!-- 헤더 -->
            <AppHeader v-if="shouldShowHeader" :type="headerType" />

            <!-- 컨테이너 -->
            <div
                id="container"
                class="flex-1 grid w-full max-w-[1440px] mx-auto grid-cols-12 gap-6 overflow-y-auto px-9"
            >
                <router-view class="col-span-12" />
            </div>
        </div>
    </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import AppNavigation from './components/Layout/AppNavigation.vue'
import AppHeader from './components/Layout/AppHeader.vue'

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

<style scoped></style>
