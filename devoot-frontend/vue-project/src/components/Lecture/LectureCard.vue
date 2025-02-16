<!-- src/components/Lecture/LectureCard.vue -->
<template>
    <!-- 카드 컨테이너: 고정 너비(w-[16.875rem])와 고정 높이(h-80)를 유지하며,
         mx-auto를 사용해 그리드 셀 내에서 중앙 정렬 -->
    <div
        class="w-[16.875rem] h-80 bg-white rounded-[1.25rem] shadow-md relative mx-auto flex flex-col"
    >
        <!-- 강의 썸네일 -->
        <div class="w-full h-[9.5rem] bg-gray-200 rounded-t-[1.25rem]">
            <img
                :src="imageUrl"
                alt="Lecture Thumbnail"
                class="w-full h-full object-cover rounded-t-[1.25rem]"
            />
        </div>

        <!-- 강의 정보 -->
        <div class="flex-1 px-4 mt-3">
            <!-- 플랫폼 및 강사명 -->
            <div class="flex items-center justify-between mb-1 text-gray-400 text-caption">
                <span>{{ lecturer }}</span>
                <span class="text-caption">{{ platform }}</span>
            </div>

            <!-- 강의 제목 -->
            <h3
                class="mb-1 font-bold text-black text-body-bold line-clamp-2"
                style="
                    height: 2.625rem;
                    overflow: hidden;
                    display: -webkit-box;
                    -webkit-line-clamp: 2;
                    -webkit-box-orient: vertical;
                "
            >
                {{ name }}
            </h3>

            <!-- 별점 및 리뷰 수 -->
            <div class="flex items-center mb-1 text-black text-caption">
                <StarFilledIcon class="w-4 h-4 mr-1 text-[#FDE03A]" />
                <span>{{ rating }}</span>
                <ReviewIcon class="w-4 h-4 ml-3 mr-1 text-gray-300" />
                <span>{{ reviewCount }}</span>
            </div>

            <!-- 가격 정보 -->
            <div class="text-right">
                <!-- 원래 가격 (취소선) -->
                <span
                    v-if="isDiscounted && !isFree"
                    class="block text-gray-300 line-through text-caption"
                >
                    ₩{{ formatPrice(originalPrice) }}
                </span>

                <!-- 할인중 / 무료 / 정상가 표시 -->
                <div v-if="isDiscounted" class="text-red-500 text-body-bold">
                    할인중 <span class="ml-2 text-black">₩{{ formatPrice(currentPrice) }}</span>
                </div>
                <div v-else-if="isFree" class="text-red-500 text-body-bold">
                    무료 <span class="ml-2 text-black">₩0</span>
                </div>
                <div v-else class="text-black text-body-bold">₩{{ formatPrice(currentPrice) }}</div>
            </div>

            <!-- 태그 리스트 -->
            <div class="flex gap-[4px] mt-1">
                <span
                    v-for="(tag, index) in limitedTags"
                    :key="index"
                    class="truncate flex items-center justify-start px-2 h-[22px] bg-gray-100 rounded-[1.25rem] text-caption text-gray-300"
                >
                    #{{ tag }}
                </span>
            </div>
        </div>
    </div>
</template>

<script>
import StarFilledIcon from '@/assets/icons/star_filled.svg'
import ReviewIcon from '@/assets/icons/review.svg'

export default {
    name: 'LectureCard',
    components: {
        StarFilledIcon,
        ReviewIcon,
    },
    props: {
        id: { type: Number, required: true },
        name: { type: String, required: true },
        lecturer: { type: String, required: true },
        platform: { type: String, default: '인프런' },
        imageUrl: { type: String, required: true },
        tags: { type: Array, default: () => [] },
        currentPrice: { type: Number, required: true },
        originalPrice: { type: Number, required: true },
        rating: { type: Number, default: 0 },
        reviewCount: { type: Number, default: 0 },
        isBookmarked: { type: Boolean, default: false },
    },
    computed: {
        isDiscounted() {
            return this.currentPrice !== this.originalPrice && this.currentPrice > 0
        },
        isFree() {
            return this.currentPrice === 0
        },
        limitedTags() {
            return this.tags.slice(0, 3)
        },
    },
    methods: {
        formatPrice(price) {
            return price.toLocaleString()
        },
    },
}
</script>

<style scoped></style>
