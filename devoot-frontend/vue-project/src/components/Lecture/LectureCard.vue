<template>
    <div class="w-[16.875rem] h-[20.0625rem] bg-white rounded-lg shadow-md p-4 relative">
        <!-- 북마크 아이콘 -->
        <button @click="toggleBookmark" class="absolute w-6 h-6 top-2 right-2">
            <component :is="BookmarkIcon" :class="bookmarkClass" />
        </button>

        <!-- 강의 썸네일 (빈 공간) -->
        <div class="w-full h-[50%] bg-gray-200 rounded-md"></div>

        <!-- 강의 정보 -->
        <div class="mt-4">
            <p class="mb-1 text-gray-400 text-caption-sm">인프런</p>
            <h3 class="mb-2 font-bold leading-snug text-gray-500 text-h3">
                백엔드 개발자 취업 토탈 가이드 (backend. 멘토링 경험기반)
            </h3>

            <!-- 별점 및 리뷰 수 -->
            <div class="flex items-center mb-2 text-gray-400 text-body-bold">
                <StarFilledIcon class="w-4 h-4 mr-1" />
                <span>5.0</span>
                <span class="ml-2">(100)</span>
            </div>

            <!-- 가격 정보 -->
            <div>
                <span class="mr-2 text-gray-300 line-through text-body">₩31,900</span>
                <span class="text-primary-500 text-body-bold">₩23,980</span>
            </div>

            <!-- 태그 리스트 -->
            <div class="flex gap-2 mt-3">
                <span
                    v-for="tag in tags"
                    :key="tag"
                    class="px-2 py-1 bg-gray-100 rounded-full text-caption"
                >
                    #{{ tag }}
                </span>
            </div>
        </div>
    </div>
</template>

<script>
import BookmarkDefaultIcon from '@/assets/icons/bookmark_default.svg'
import BookmarkFilledIcon from '@/assets/icons/bookmark_filled.svg'
import StarFilledIcon from '@/assets/icons/star_filled.svg'

export default {
    name: 'LectureCard',
    components: {
        BookmarkDefaultIcon,
        BookmarkFilledIcon,
        StarFilledIcon,
    },
    props: {
        tags: {
            type: Array,
            default: () => ['태그', '태그', '태그', '태그'],
        },
    },
    data() {
        return {
            isBookmarked: false, // 북마크 상태
        }
    },
    computed: {
        BookmarkIcon() {
            // 상태에 따라 사용할 아이콘 컴포넌트 결정
            return this.isBookmarked ? BookmarkFilledIcon : BookmarkDefaultIcon
        },
        bookmarkClass() {
            // 북마크 상태에 따라 색상 클래스 설정
            return this.isBookmarked ? 'text-primary-500' : 'text-gray-300'
        },
    },
    methods: {
        toggleBookmark() {
            // 북마크 상태 토글
            this.isBookmarked = !this.isBookmarked
        },
    },
}
</script>

<style scoped>
/* 추가적인 커스텀 스타일이 필요하다면 여기에 작성 */
</style>
