<template>
    <header
        class="flex items-center justify-between h-20 px-4 py-2 bg-white border-b border-gray-200"
    >
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
                class="relative flex items-center justify-center w-10 h-10 border rounded-full hover:bg-gray-100"
                aria-label="알림"
            >
                <BellIcon class="w-6 h-6" />
            </button>
        </div>

        <!-- 사용자 검색 모달 -->
        <UserSearchModal :isOpen="isUserSearchModalOpen" @close="closeUserSearchModal" />
    </header>
</template>

<script setup>
// Vue Router 및 상태 관리
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import CategoryIcon from '@/assets/icons/category.svg'
import UserSearchIcon from '@/assets/icons/user_search.svg'
import BellIcon from '@/assets/icons/bell.svg'
import SearchIcon from '@/assets/icons/search.svg'
import CategoryDropDown from '@/components/Common/CategoryDropDown.vue'
import UserSearchModal from '@/components/Common/UserSearchModal.vue'

// Props 정의 (기본값 포함)
defineProps({
    type: {
        type: String,
        required: false,
        default: 'lecture',
    },
})

// 검색 상태 및 라우터 설정
const searchQuery = ref('')
const router = useRouter()

// 드롭다운 상태 관리
const isCategoryDropdownVisible = ref(false)

// 드롭다운 토글 함수
const toggleCategoryDropdown = () => {
    isCategoryDropdownVisible.value = !isCategoryDropdownVisible.value
}

// 드롭다운 닫기 함수 (외부 클릭 및 카테고리 선택 시 호출)
const closeCategoryDropdown = () => {
    isCategoryDropdownVisible.value = false
}

// 외부 클릭 감지 로직 추가 및 제거
const handleClickOutside = (event) => {
    const dropdown = document.querySelector('.category-dropdown')
    const button = document.querySelector('button[aria-label="카테고리 선택"]')

    if (dropdown && !dropdown.contains(event.target) && button && !button.contains(event.target)) {
        closeCategoryDropdown()
    }
}

onMounted(() => {
    document.addEventListener('click', handleClickOutside)
})

onBeforeUnmount(() => {
    document.removeEventListener('click', handleClickOutside)
})

// 검색 실행 함수
const executeSearch = () => {
    const trimmedQuery = searchQuery.value.trim()

    if (trimmedQuery) {
        // 검색어가 있을 경우, 검색어를 쿼리로 전달하며 이동
        router.push({
            path: '/lecture',
            query: { q: trimmedQuery },
        })
    } else {
        // 검색어가 없을 경우, 쿼리 없이 기본 경로로 이동
        router.push({
            path: '/lecture',
        })
    }

    // 검색창 초기화
    searchQuery.value = ''
}

// 사용자 검색 모달 상태 관리
const isUserSearchModalOpen = ref(false)

const openUserSearchModal = () => {
    isUserSearchModalOpen.value = true
}

const closeUserSearchModal = () => {
    isUserSearchModalOpen.value = false
}
</script>

<style scoped></style>
