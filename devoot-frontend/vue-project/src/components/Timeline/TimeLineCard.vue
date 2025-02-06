<template>
    <div class="p-4 mb-4 bg-white rounded-lg shadow-sm">
        <!-- 카드 헤더 -->
        <div class="flex items-center justify-between">
            <div class="flex items-center">
                <!-- 사용자 아바타 -->
                <div class="w-10 h-10 bg-gray-200 rounded-full"></div>
                <p class="flex items-center ml-3 text-sm text-gray-700">
                    <strong>{{ userName }}</strong>
                    <span v-if="type === 'lecture-status-change'"
                        >님이 강의 상태를 변경했습니다.</span
                    >
                    <span v-if="type === 'new-lecture-interest'"
                        >님이 새로운 강의에 관심을 가지기 시작했습니다.</span
                    >
                    <span v-if="type === 'footprint-added'" class="flex items-center">
                        님의 발자국이 추가되었습니다
                        <LogoIcon class="w-4 h-4 ml-1 text-primary-500" />
                    </span>
                </p>
            </div>
            <span class="text-xs text-gray-400">{{ formattedDate }}</span>
        </div>

        <!-- 카드 내용 -->
        <template v-if="type === 'lecture-status-change' || type === 'new-lecture-interest'">
            <!-- 상태 변경 정보 -->
            <div
                v-if="type === 'lecture-status-change'"
                class="flex items-center mt-2 space-x-2 text-sm text-gray-600"
            >
                <!-- beforeStatus -->
                <span :class="getStatusClass(beforeStatus)" class="px-2 py-1 bg-gray-100 rounded">
                    {{ beforeStatus }}
                </span>
                <span>→</span>
                <!-- afterStatus -->
                <span :class="getStatusClass(afterStatus)" class="px-2 py-1 bg-gray-100 rounded">
                    {{ afterStatus }}
                </span>
            </div>

            <!-- 강의 카드 -->
            <div class="flex items-center p-4 mt-4 rounded-lg bg-gray-50">
                <!-- 썸네일 이미지 -->
                <img
                    :src="imageUrl"
                    alt="강의 썸네일"
                    class="object-cover w-16 h-16 mr-4 rounded-lg"
                />
                <!-- 강의 정보 -->
                <div>
                    <p class="text-sm font-semibold text-gray-800">{{ lectureTitle }}</p>
                    <div
                        v-if="tags && tags.length"
                        class="flex mt-2 space-x-2 text-xs text-gray-500"
                    >
                        <span v-for="(tag, index) in tags" :key="index">#{{ tag }}</span>
                    </div>
                </div>
            </div>
        </template>

        <!-- 발자국 추가 내용 -->
        <template v-if="type === 'footprint-added'">
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
        </template>
    </div>
</template>

<script>
// footprint.svg를 import합니다.
import LogoIcon from '@/assets/icons/logo.svg'

export default {
    name: 'TimeLineCard',
    components: {
        LogoIcon,
    },
    props: {
        type: {
            type: String,
            required: true,
        },
        userName: {
            type: String,
            required: true,
        },
        lectureTitle: {
            type: String,
            default: '',
        },
        imageUrl: {
            type: String,
            default: '/src/assets/icons/default-thumbnail.png', // 기본 썸네일 이미지 경로
        },
        tags: {
            type: Array,
            default: () => [],
        },
        beforeStatus: {
            type: String,
            default: '',
        },
        afterStatus: {
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
    methods: {
        getStatusClass(status) {
            switch (status) {
                case '수강 전':
                    return 'text-gray-400'
                case '수강 중':
                    return 'text-[#FDE03A]' // 노란색
                case '수강 완료':
                    return 'text-[#0EDB8C]' // 초록색
                default:
                    return ''
            }
        },
    },
}
</script>

<style scoped></style>
