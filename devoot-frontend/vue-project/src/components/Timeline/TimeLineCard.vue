<template>
    <div class="p-4 mb-4 bg-white rounded-lg shadow-sm">
        <!-- 카드 헤더 -->
        <div class="flex items-center justify-between">
            <div class="flex items-center">
                <!-- 사용자 아바타 -->
                <div class="w-10 h-10 bg-gray-200 rounded-full"></div>
                <p class="ml-3 text-sm text-gray-700">
                    <strong>{{ userName }}</strong
                    >님의 발자국이 추가되었습니다
                    <span class="text-blue-500">🐾</span>
                </p>
            </div>
            <!-- 날짜 -->
            <span class="text-xs text-gray-400">{{ formattedDate }}</span>
        </div>

        <!-- 카드 내용 -->
        <div v-if="type === 'lecture-status-change'" class="mt-4">
            <div class="flex items-center space-x-2 text-sm text-gray-600">
                <span class="px-2 py-1 bg-gray-100 rounded">수정 전</span>
                <span>→</span>
                <span class="px-2 py-1 bg-gray-100 rounded">수정 후</span>
            </div>
            <div class="p-4 mt-3 rounded-lg bg-gray-50">
                <p class="text-sm font-semibold text-gray-800">{{ lectureTitle }}</p>
                <div class="flex mt-2 space-x-2 text-xs text-gray-500">
                    <span>#태그</span>
                    <span>#태그</span>
                    <span>#태그</span>
                </div>
            </div>
        </div>

        <div v-if="type === 'new-lecture-interest'" class="mt-4">
            <div class="p-4 mt-3 rounded-lg bg-gray-50">
                <p class="text-sm font-semibold text-gray-800">{{ lectureTitle }}</p>
                <div class="flex mt-2 space-x-2 text-xs text-gray-500">
                    <span>#태그</span>
                    <span>#태그</span>
                    <span>#태그</span>
                </div>
            </div>
        </div>

        <!-- 발자국 추가 카드 -->
        <div v-if="type === 'footprint-added'" class="mt-4">
            <div class="grid grid-cols-2 p-4 rounded-lg gap-x-4 bg-gray-50">
                <!-- 첫 번째 열: 강의 제목 -->
                <p class="text-sm font-medium text-gray-800">{{ footprints[0].lectureTitle }}</p>

                <!-- 두 번째 열: 세부 정보 -->
                <p class="text-sm text-gray-600">{{ footprints[0].detail }}</p>
            </div>
        </div>
    </div>
</template>

<script>
export default {
    name: 'TimeLineCard',
    props: {
        type: {
            type: String,
            required: true,
        },
        userName: {
            type: String,
            required: true,
        },
        actionText: {
            type: String,
            required: true,
        },
        lectureTitle: {
            type: String,
            default: '',
        },
        footprints: {
            type: Array,
            default: () => [],
        },
        date: {
            type: String,
            required: true,
        },
    },
    computed: {
        formattedDate() {
            const options = { year: 'numeric', month: '2-digit', day: '2-digit' }
            return new Date(this.date).toLocaleDateString('ko-KR', options)
        },
    },
}
</script>

<style scoped>
/* Tailwind CSS를 사용하므로 별도 스타일 정의는 최소화 */
</style>
