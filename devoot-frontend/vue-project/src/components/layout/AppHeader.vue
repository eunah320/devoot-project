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
                />
            </div>

            <!-- 검색창 -->
            <div class="relative w-[47.5rem] h-[2.75rem]">
                <input
                    type="text"
                    v-model="searchQuery"
                    placeholder="강의명, 강사명, 키워드 검색"
                    class="w-full h-full px-4 pr-10 text-sm border rounded focus:outline-none bg-gray-50"
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
    </header>
</template>

<script setup>
// Vue Router 및 상태 관리
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import CategoryIcon from '@/assets/icons/category.svg'
import UserSearchIcon from '@/assets/icons/user_search.svg'
import BellIcon from '@/assets/icons/bell.svg'
import SearchIcon from '@/assets/icons/search.svg'
import CategoryDropDown from '@/components/Common/CategoryDropDown.vue'

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

// 검색 실행 함수
const executeSearch = () => {
    if (searchQuery.value.trim()) {
        router.push({
            path: '/lecture',
            query: { q: searchQuery.value },
        })
    }
}
</script>
