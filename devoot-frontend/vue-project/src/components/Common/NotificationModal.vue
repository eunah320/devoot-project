<template>
    <div
        v-if="isOpen"
        class="fixed inset-0 z-50 flex items-center justify-center"
        @click.self="$emit('close')"
    >
        <div class="p-4 bg-white rounded-lg shadow-lg w-80">
            <header class="flex items-center justify-between pb-2 border-b">
                <h2 class="text-lg font-semibold">알림보기</h2>
                <button class="text-gray-500 hover:text-black" @click="$emit('close')">✕</button>
            </header>
            <ul class="mt-4 space-y-4">
                <li
                    v-for="notification in notifications"
                    :key="notification.id"
                    class="flex items-start space-x-3"
                >
                    <div class="flex flex-col items-center">
                        <div class="flex flex-row">
                            <img src="#" alt="프로필 이미지" class="w-10 h-10 rounded-full" />
                            <p>
                                <span class="font-semibold">{{ notification.fromUser }}</span
                                >님이
                                <span v-if="notification.followId">팔로우 요청을 보냈습니다.</span>
                                <span v-else>팔로우하기 시작했습니다.</span>
                            </p>
                        </div>
                        <button
                            v-if="notification.followId"
                            class="px-4 py-1 mt-2 w-[13.25rem] h-[1.563rem] text-white bg-primary-500 rounded-md hover:bg-blue-600"
                            @click.prevent="acceptFollow(notification.followId)"
                        >
                            팔로우 수락
                        </button>
                    </div>
                </li>
            </ul>
        </div>
    </div>
</template>

<script setup>
// Props 정의
defineProps({
    isOpen: Boolean,
    notifications: Array,
})

// 팔로우 요청 수락 처리 함수
async function acceptFollow(followId) {
    try {
        const response = await fetch(`/api/follow/accept`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ followId }),
        })

        if (!response.ok) {
            throw new Error('팔로우 요청 수락에 실패했습니다.')
        }

        const result = await response.json()
        alert(`팔로우 요청(${followId})을 수락했습니다.`)
        console.log('서버 응답:', result)
    } catch (error) {
        console.error('에러 발생:', error)
        alert('팔로우 요청 수락 중 문제가 발생했습니다.')
    }
}
</script>

<style scoped>
/* 필요 시 스타일 추가 */
</style>
