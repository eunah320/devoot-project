<template>
    <div>
        profile
        <TabMenu tab-left="북마크한 강의" tab-right="내가 쓴 리뷰" @update-tab="handleTabChange" />
        <!-- <component :is="currentComponent" /> -->
        <TodoList />
        <TodoAddModal />
        <KanbanSection />

        <ProfileContribution />
        <KanbanCard />
    </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import TabMenu from '@/components/Common/TabMenu.vue'
// import KanbanSection from '@/components/Profile/KanbanSection.vue'
// import ProfileReviewSection from '@/components/Profile/ProfileReviewSection.vue'

// 현재 선택된 탭 (기본값 : 'left')
const currentTab = ref('left')

// 탭에 따라 렌더링할 컴포넌트 설정
// const currentComponent = computed(() => {
//     return currentTab.value === 'left' ? KanbanSection : ProfileReviewSection
// })

// 탭 변경 이벤트 핸들러
const handleTabChange = (tab) => {
    currentTab.value = tab
}
import ProfileContribution from '@/components/Profile/ProfileContribution.vue'
import KanbanCard from '@/components/Profile/KanbanCard.vue'
import KanbanSection from '@/components/Profile/KanbanSection.vue'
import TodoAddModal from '@/components/Profile/TodoAddModal.vue'
import TodoAddModalCalendar from '@/components/Profile/TodoAddModalCalendar.vue'
import TodoList from '@/components/Profile/TodoList.vue'
</script>

<style scoped></style>
<style scoped></style>
