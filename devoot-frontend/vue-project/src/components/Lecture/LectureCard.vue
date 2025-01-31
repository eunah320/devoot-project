<template>
    <div class="w-[16.875rem] h-[20.0625rem] bg-white rounded-[1.25rem] shadow-md relative">
        <!-- 북마크 아이콘 -->
        <button @click="toggleBookmark" class="absolute w-6 h-6 top-2 right-2">
            <component :is="BookmarkIcon" :class="bookmarkClass" />
        </button>

        <!-- 강의 썸네일 -->
        <div class="w-full h-[9.5rem] bg-gray-200 rounded-t-[1.25rem]"></div>

        <!-- 강의 정보 -->
        <div class="px-4 mt-3">
            <!-- 강의 플랫폼 및 강사명 -->
            <div class="flex items-center justify-between mb-1 text-gray-400 text-caption">
                <div class="flex items-center">
                    <a
                        href="https://www.inflearn.com/"
                        target="_blank"
                        rel="noopener noreferrer"
                        class="flex items-center"
                    >
                        <span>인프런</span>
                        <LinkExternalIcon class="w-3 h-3 ml-1" />
                    </a>
                </div>
                <span class="text-gray-400">인튜(INCU)</span>
            </div>

            <!-- 강의 제목 -->
            <h3 class="mb-1 leading-snug text-black text-body-bold line-clamp-2">
                백엔드 개발자 취업 토탈 가이드 (backend. 멘토링 경험기반)
            </h3>

            <!-- 별점 및 리뷰 수 -->
            <div class="flex items-center mb-1 text-black text-caption">
                <StarFilledIcon class="w-4 h-4 mr-1 text-[#FDE03A]" />
                <span>5.0</span>
                <ReviewIcon class="w-4 h-4 ml-3 mr-1 text-gray-300" />
                <span>100</span>
            </div>

            <!-- 가격 정보 -->
            <div class="text-right">
                <!-- 원래 가격 -->
                <span
                    v-bind:style="{ visibility: isDiscounted && !isFree ? 'visible' : 'hidden' }"
                    class="block text-gray-300 line-through text-caption"
                >
                    ₩{{ formatPrice(originalPrice) }}
                </span>
                <!-- 할인 메시지 및 판매 가격 -->
                <div v-if="isDiscounted" class="text-red-500 text-body-bold">
                    할인중
                    <span class="ml-2 text-black">₩{{ formatPrice(currentPrice) }}</span>
                </div>
                <!-- 무료 메시지 및 판매 가격 -->
                <div v-if="isFree" class="text-red-500 text-body-bold">
                    무료
                    <span class="ml-2 text-black">₩0</span>
                </div>
                <!-- 판매 가격만 표시 (할인 없음) -->
                <div v-if="!isDiscounted && !isFree" class="text-black text-body-bold">
                    ₩{{ formatPrice(currentPrice) }}
                </div>
            </div>

            <!-- 태그 리스트 -->
            <div class="flex gap-[4px] mt-1" :style="{ width: `${maxTotalWidth}px` }">
                <span
                    v-for="(tag, index) in limitedTags"
                    :key="index"
                    style="width: max-content"
                    class="truncate flex items-center justify-start px-2 h-[22px] bg-gray-100 rounded-[1.25rem] text-caption text-gray-300"
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
import LinkExternalIcon from '@/assets/icons/link_external.svg'
import ReviewIcon from '@/assets/icons/review.svg'

export default {
    name: 'LectureCard',
    components: {
        BookmarkDefaultIcon,
        BookmarkFilledIcon,
        StarFilledIcon,
        LinkExternalIcon,
        ReviewIcon,
    },
    props: {
        tags: {
            type: Array,
            required: true,
        },
        currentPrice: {
            type: Number,
            required: true,
        },
        originalPrice: {
            type: Number,
            required: true,
        },
    },
    computed: {
        isDiscounted() {
            return this.currentPrice !== this.originalPrice && this.currentPrice > 0
        },
        isFree() {
            return this.currentPrice === 0
        },
        BookmarkIcon() {
            // 상태에 따라 사용할 아이콘 컴포넌트 결정
            return this.isBookmarked ? BookmarkFilledIcon : BookmarkDefaultIcon
        },
        bookmarkClass() {
            // 북마크 상태에 따라 색상 클래스 설정
            return this.isBookmarked ? 'text-primary-500' : 'text-gray-300'
        },
        limitedTags() {
            // 태그를 최대 3개까지만 표시
            return this.tags.slice(0, 3)
        },
        tagWidths() {
            const remainingWidth =
                this.maxTotalWidth - (this.limitedTags.length - 1) * this.gapWidth

            return this.limitedTags.map((_, index) => remainingWidth / this.limitedTags.length)
        },
    },
    data() {
        return {
            isBookmarked: false, // 북마크 상태
            maxTotalWidth: 238, // 전체 태그와 gap의 총 너비 제한
            gapWidth: 4, // gap의 크기 (4px)
        }
    },
    methods: {
        toggleBookmark() {
            this.isBookmarked = !this.isBookmarked
        },
        formatPrice(price) {
            // 천 단위마다 쉼표 추가
            return price.toLocaleString()
        },
    },
}
</script>

<style scoped>
/* 추가적인 커스텀 스타일이 필요하다면 여기에 작성 */
</style>
