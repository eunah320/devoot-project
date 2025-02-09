<template>
    <header class="flex items-center justify-between h-20 px-4 py-2 bg-white">
        <!-- type이 'lecture'인 경우 -->
        <template v-if="type === 'lecture'">
            <div class="relative flex items-center gap-4">
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
            </div>

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
        </template>

        <!-- type이 'user'인 경우 -->
        <template v-else-if="type === 'user'">
            <div class="flex items-center gap-4">
                <!-- 사용자 검색 버튼 -->
                <button
                    class="flex items-center w-[10.75rem] h-[2.75rem] px-4 text-sm font-medium text-gray-500 border rounded hover:bg-gray-100"
                    aria-label="사용자 검색"
                    @click="openUserSearchModal"
                >
                    <UserSearchIcon class="w-5 h-5 mr-2" />
                    사용자 검색
                </button>
            </div>
        </template>

        <!-- 오른쪽: 알림 버튼 (공통) -->
        <div class="flex items-center gap-4">
            <button
                class="relative flex items-center justify-center w-10 h-10 rounded-full hover:bg-gray-100"
                aria-label="알림"
                @click="openNotificationModal"
            >
                <!-- 알람 여부에 따라 아이콘 변경 -->
                <component
                    :is="hasNotifications ? BellNotificationIcon : BellIcon"
                    class="w-6 h-6"
                />
            </button>
        </div>

        <!-- 사용자 검색 모달 -->
        <UserSearchModal :isOpen="isUserSearchModalOpen" @close="closeUserSearchModal" />
        <!-- 알림 모달 -->
        <NotificationModal
            :isOpen="isNotificationModalOpen"
            :notifications="notifications"
            @close="closeNotificationModal"
        />
    </header>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import CategoryIcon from '@/assets/icons/category.svg'
import UserSearchIcon from '@/assets/icons/user_search.svg'
import BellIcon from '@/assets/icons/bell.svg'
import BellNotificationIcon from '@/assets/icons/bell_notification.svg'
import SearchIcon from '@/assets/icons/search.svg'
import CategoryDropDown from '@/components/Common/CategoryDropDown.vue'
import UserSearchModal from '@/components/Common/UserSearchModal.vue'
import NotificationModal from '@/components/Common/NotificationModal.vue' // 알림

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
const isUserSearchModalOpen = ref(false)
const isNotificationModalOpen = ref(false) // 알림 모달 열림 여부
const hasNotifications = ref(false) // 알람 여부

// 라우터 설정
const router = useRouter()

// 드롭다운 토글 함수
const toggleCategoryDropdown = () => {
    isCategoryDropdownVisible.value = !isCategoryDropdownVisible.value
}

// 드롭다운 닫기 함수 (카테고리 선택 시 호출)
const closeCategoryDropdown = () => {
    isCategoryDropdownVisible.value = false
}

// 검색 실행 함수
const executeSearch = () => {
    const trimmedQuery = searchQuery.value.trim()
    if (trimmedQuery) {
        router.push({ path: '/lecture', query: { q: trimmedQuery } })
    } else {
        router.push({ path: '/lecture' })
    }
    searchQuery.value = ''
}

// 사용자 검색 모달 열기/닫기 함수
const openUserSearchModal = () => {
    isUserSearchModalOpen.value = true
}
const closeUserSearchModal = () => {
    isUserSearchModalOpen.value = false
}

// 알림 모달 열기 함수
const openNotificationModal = () => {
    isNotificationModalOpen.value = true
}
// 알림 모달 닫기 함수
const closeNotificationModal = () => {
    isNotificationModalOpen.value = false
}

// 더미 데이터로부터 알림 여부 확인
onMounted(async () => {
    try {
        const response = await fetch('/notification_dummy_data.json')
        const data = await response.json()
        // 배열 안에 객체가 하나라도 있으면 알림 존재 여부를 true로 설정
        hasNotifications.value =
            Array.isArray(data) && data.length > 0 && typeof data[0] === 'object'
    } catch (error) {
        console.error('Failed to load notification data:', error)
    }
})
</script>

<style scoped></style>
