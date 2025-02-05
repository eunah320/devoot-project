<template>
    <div class="p-4 mb-4 bg-white rounded-lg shadow-sm">
        <div class="flex items-center justify-between">
            <div class="flex items-center">
                <div class="w-10 h-10 bg-gray-200 rounded-full"></div>
                <p class="ml-3 text-sm text-gray-700">
                    <strong>{{ userName }}</strong
                    >님이 새로운 발자국을 남겼습니다.
                    <span class="text-blue-500">🐾</span>
                </p>
            </div>
            <span class="text-xs text-gray-400">{{ formattedDate }}</span>
        </div>

        <!-- 발자국 리스트 -->
        <ul class="mt-4 space-y-2">
            <li
                v-for="(footprint, index) in footprints"
                :key="index"
                class="grid grid-cols-[1fr_auto] p-3 bg-gray-50 rounded-lg"
            >
                <!-- 강의 제목 -->
                <p class="text-sm font-medium text-gray-800">{{ footprint.lectureTitle }}</p>

                <!-- 세부 정보 -->
                <p class="text-sm text-gray-600">{{ footprint.detail }}</p>
            </li>
        </ul>
    </div>
</template>

<script>
export default {
    name: 'FootprintAddedCard',
    props: {
        userName: { type: String, required: true },
        footprints: { type: Array, default: () => [] },
        date: { type: String, required: true },
    },
    computed: {
        formattedDate() {
            const options = { year: 'numeric', month: '2-digit', day: '2-digit' }
            return new Date(this.date).toLocaleDateString('ko-KR', options)
        },
    },
}
</script>

<style scoped></style>
