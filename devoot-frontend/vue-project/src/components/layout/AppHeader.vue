<template>
    <header class="flex items-center justify-between h-20 px-4 py-2 bg-white">
        <!-- 왼쪽: 강의 또는 사용자 검색 -->
        <div v-if="type === 'lecture'" class="relative flex items-center gap-4">
            <!-- 카테고리 버튼 -->
            <button
                class="flex items-center w-[10.75rem] h-[2.75rem] px-4 text-sm font-medium text-gray-500 border rounded hover:bg-gray-100"
                aria-label="카테고리 선택"
                @click="toggleCategoryDropdown"
            >
                <CategoryIcon class="w-5 h-5 mr-2" />
                카테고리
            </button>

            <!-- 카테고리 드롭다운 -->
            <CategoryDropDown
                v-if="isCategoryDropdownVisible"
                class="absolute top-full left-0 mt-2 w-[10.75rem] bg-white border rounded shadow-lg z-10"
                @closeDropdown="closeCategoryDropdown"
            />

            <!-- 검색창 -->
            <div class="relative w-[47.5rem] h-[2.75rem]">
                <input
                    type="text"
                    v-model="searchQuery"
                    placeholder="강의명, 강사명, 키워드 검색"
                    class="w-full h-full px-4 pr-10 text-sm border rounded focus:outline-none bg-gray-50"
                    @keyup.enter="executeSearch"
                />
                <!-- 검색 아이콘 -->
                <SearchIcon
                    class="absolute w-5 h-5 text-gray-400 transform -translate-y-1/2 cursor-pointer top-1/2 right-3"
                    @click="executeSearch"
                />
            </div>
        </div>

        <!-- 오른쪽: 알림 버튼 -->
        <div class="flex items-center gap-4">
            <button
                class="relative flex items-center justify-center w-10 h-10 rounded-full hover:bg-gray-100"
                aria-label="알림"
                @click="toggleNotificationModal"
            >
                <!-- 알람 여부에 따라 아이콘 변경 -->
                <component
                    :is="hasNotifications ? BellNotificationIcon : BellIcon"
                    class="w-6 h-6"
                />
            </button>
        </div>

        <!-- 알림 모달 -->
        <NotificationModal
            :isOpen="isNotificationModalOpen"
            :notifications="notifications"
            @close="closeNotificationModal"
        />
    </header>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import CategoryIcon from '@/assets/icons/category.svg'
import BellIcon from '@/assets/icons/bell.svg'
import BellNotificationIcon from '@/assets/icons/bell_notification.svg'
import SearchIcon from '@/assets/icons/search.svg'
import CategoryDropDown from '@/components/Common/CategoryDropDown.vue'
import NotificationModal from '@/components/Common/NotificationModal.vue'

// Props 정의 (기본값 포함)
defineProps({
    type: {
        type: String,
        required: false,
        default: 'lecture',
    },
})

// 상태 관리 변수
const searchQuery = ref('')
const isCategoryDropdownVisible = ref(false)
const isNotificationModalOpen = ref(false) // 알림 모달 열림 여부
const hasNotifications = ref(false) // 알람 여부
const notifications = ref([])

// 드롭다운 토글 함수
const toggleCategoryDropdown = () => {
    isCategoryDropdownVisible.value = !isCategoryDropdownVisible.value
}

// 드롭다운 닫기 함수
const closeCategoryDropdown = () => {
    isCategoryDropdownVisible.value = false
}

// 알림 모달 열기/닫기 함수
const toggleNotificationModal = () => {
    isNotificationModalOpen.value = !isNotificationModalOpen.value
}

const closeNotificationModal = () => {
    isNotificationModalOpen.value = false
}

// 더미 데이터 로드 및 알림 여부 확인
onMounted(async () => {
    try {
        const response = await fetch('/public/notification_dummy_data.json')
        const data = await response.json()
        notifications.value = data.filter((notification) => !notification.hasRead)
        hasNotifications.value = notifications.value.length > 0
    } catch (error) {
        console.error('Failed to load notification data:', error)
    }
})
</script>

<style scoped></style>
