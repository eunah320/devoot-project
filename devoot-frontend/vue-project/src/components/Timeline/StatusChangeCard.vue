<template>
    <div class="p-4 mb-4 bg-white rounded-lg shadow-sm">
        <div class="flex items-center justify-between">
            <div class="flex items-center">
                <div class="w-10 h-10 bg-gray-200 rounded-full"></div>
                <p class="ml-3 text-sm text-gray-700">
                    <strong>{{ userName }}</strong
                    >님이 강의 상태를 변경했습니다.
                </p>
            </div>
            <span class="text-xs text-gray-400">{{ formattedDate }}</span>
        </div>
        <div class="mt-4">
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
    </div>
</template>

<script>
export default {
    name: 'LectureStatusChangeCard',
    props: {
        userName: { type: String, required: true },
        lectureTitle: { type: String, required: true },
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
