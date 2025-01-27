<template>
    <div class="flex flex-row">
        <!-- 네비게이션 바 -->
        <AppNavigation class="fixed" @menu-selected="handleMenuSelected" />

        <!-- 화면 -->
        <div
            id="container"
            class="grid ml-[4.5rem] lg:ml-[13.5rem] flex-1 grid-cols-12 gap-6 overflow-y-auto px-9"
        >
            <!-- 선택된 메뉴에 따라 화면 전환 부분을 구현 -->
        </div>
    </div>
</template>

<script setup>
import { ref } from 'vue'
import AppNavigation from './components/layout/AppNavigation.vue'

const selectedMenu = ref('home')

// 메뉴가 선택되었을 때 호출되는 함수
const handleMenuSelected = (menu) => {
    selectedMenu.value = menu
    console.log('Selected Menu:', menu) // 선택된 메뉴를 출력
}
</script>

<style scoped></style>
