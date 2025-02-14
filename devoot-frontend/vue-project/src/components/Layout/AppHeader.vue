<!-- src\components\Layout\AppHeader.vue -->
<template>
    <header class="flex items-center justify-between w-full h-20 bg-white">
        <!-- type이 'lecture'인 경우 -->
        <template v-if="type === 'lecture'">
            <!-- 카테고리 버튼 -->
            <div class="relative flex flex-row items-center gap-4">
                <button
                    class="header-button hover:bg-gray-100"
                    aria-label="카테고리 선택"
                    @click="toggleCategoryDropdown"
                >
                    <CategoryIcon class="w-6 h-6 mr-2.5" />
                    카테고리
                </button>

                <!-- 카테고리 드롭다운 -->
                <CategoryDropDown
                    v-if="isCategoryDropdownVisible"
                    class="absolute left-0 mt-2 overflow-hidden rounded-lg shadow-lg top-full w-44 z-5"
                    @closeDropdown="closeCategoryDropdown"
                />
            </div>

            <!-- 검색창 -->
            <div class="relative w-[47.5rem] h-11">
                <input
                    type="text"
                    v-model="searchQuery"
                    placeholder="강의명, 강사명, 키워드 검색"
                    class="w-full h-full px-4 pr-10 border rounded-lg text-body focus:outline-none bg-gray-50"
                    @keyup.enter="executeSearch"
                />
                <!-- 검색 아이콘 -->
                <SearchIcon
                    class="absolute w-6 h-6 text-black transform -translate-y-1/2 cursor-pointer top-1/2 right-3"
                    @click="executeSearch"
                />
            </div>
        </template>

        <!-- type이 'user'인 경우 -->
        <template v-else-if="type === 'user'">
            <div class="flex items-center gap-4">
                <!-- 사용자 검색 버튼 -->
                <button
                    class="header-button hover:bg-gray-100"
                    aria-label="사용자 검색"
                    @click="openUserSearchModal"
                >
                    <UserSearchIcon class="w-6 h-6 mr-2.5" />
                    사용자 검색
                </button>
            </div>

            <!-- 사용자 검색 모달 -->
            <UserSearchModal :isOpen="isUserSearchModalOpen" @close="closeUserSearchModal" />
        </template>

        <!-- 오른쪽: 알림 버튼 (공통) -->
        <div class="flex items-center gap-4">
            <button
                class="relative inline-flex items-center justify-center w-10 h-10 rounded-full hover:bg-gray-100"
                aria-label="알림"
                @click="openNotificationModal"
            >
                <!-- 알림 여부에 따라 아이콘 변경 -->
                <component
                    :is="hasNotifications ? BellNotificationIcon : BellIcon"
                    class="w-6 h-6"
                />
            </button>
        </div>

        <!-- 알림 모달 -->
        <NotificationModal
            :isOpen="isNotificationModalOpen"
            :token="userStore.token"
            @close="closeNotificationModal"
        />
    </header>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { hasUnread } from '@/helpers/notification'

import CategoryIcon from '@/assets/icons/category.svg'
import CategoryDropDown from '@/components/Common/CategoryDropDown.vue'
import SearchIcon from '@/assets/icons/search.svg'
import BellIcon from '@/assets/icons/bell.svg'
import BellNotificationIcon from '@/assets/icons/bell_notification.svg'
import NotificationModal from '@/components/Common/NotificationModal.vue'
import UserSearchModal from '@/components/Common/UserSearchModal.vue'

// Props 정의
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
const isNotificationModalOpen = ref(false)
const hasNotifications = ref(false)
const isUserSearchModalOpen = ref(false)

const userStore = useUserStore()
const router = useRouter()

// 카테고리 드롭다운 토글
const toggleCategoryDropdown = () => {
    isCategoryDropdownVisible.value = !isCategoryDropdownVisible.value
}

// 카테고리 드롭다운 닫기
const closeCategoryDropdown = () => {
    isCategoryDropdownVisible.value = false
}

// 사용자 검색 모달 열기
const openUserSearchModal = () => {
    isUserSearchModalOpen.value = true
}

// 사용자 검색 모달 닫기
const closeUserSearchModal = () => {
    isUserSearchModalOpen.value = false
}

// 검색 실행 함수
const executeSearch = () => {
    const trimmedQuery = searchQuery.value.trim()
    if (trimmedQuery) {
        router.push({ path: '/lecture', query: { q: trimmedQuery } })
    }
    searchQuery.value = ''
}

// 알림 존재 여부 확인 후 아이콘 변경
const updateHasUnread = async () => {
    if (!userStore.token) return

    try {
        const response = await hasUnread(userStore.token)
        hasNotifications.value = response.data
        console.log('🔔 읽지 않은 알림 존재 여부:', response.data)
    } catch (error) {
        console.error('❌ 읽지 않은 알림 존재 여부 확인 실패:', error)
    }
}

// 알림 모달 열기
const openNotificationModal = () => {
    isNotificationModalOpen.value = true
}

// 알림 모달 닫기
const closeNotificationModal = () => {
    isNotificationModalOpen.value = false
}

// 페이지 로드 및 토큰 변경 시 알림 존재 여부 확인
onMounted(() => {
    updateHasUnread()
})

watch(
    () => userStore.token,
    (newToken) => {
        updateHasUnread()
    }
)
</script>

<style scoped></style>
