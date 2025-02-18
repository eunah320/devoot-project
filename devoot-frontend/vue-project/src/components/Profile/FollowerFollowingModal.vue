<template>
    <div
        v-if="isOpen"
        class="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50"
        @click="closeModal"
    >
        <div class="z-50 bg-white shadow-lg rounded-lg w-[300px] h-[400px] p-4" @click.stop>
            <div class="flex flex-col h-full">
                <div class="flex items-center justify-between mb-4">
                    <h2 class="text-h2">{{ modalTitle }}</h2>
                    <button
                        @click="closeModal"
                        class="flex items-center justify-center w-6 h-6 text-2xl text-gray-500 hover:text-black"
                    >
                        &times;
                    </button>
                </div>

                <ul class="flex-1 mt-4 overflow-y-auto no-scrollbar">
                    <li
                        v-for="user in users"
                        :key="user.id"
                        @click="navigateToProfile(user)"
                        class="flex items-center py-2 space-x-3 border-b cursor-pointer last:border-none hover:bg-gray-100"
                    >
                        <img
                            :src="user.imageUrl || defaultImage"
                            alt="profile"
                            class="object-cover w-10 h-10 rounded-full"
                        />
                        <div class="flex flex-col">
                            <p class="text-sm font-medium">{{ user.profileId }}</p>
                            <p class="text-xs text-gray-500">{{ user.nickname }}</p>
                        </div>
                    </li>
                </ul>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, watch, computed } from 'vue'
import { searchUsers } from '@/helpers/api'
import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router' // 추가: 라우터 사용
import { readFollowers, readFollowings } from '@/helpers/follow'
const router = useRouter() // 라우터 인스턴스 생성

const props = defineProps({
    isOpen: Boolean,
    userId: {
        type: String,
        required: true,
    },
    type: {
        type: String, // 'follower' 또는 'following' 값을 받을 예정
        required: true,
    },
})
const emit = defineEmits(['close'])

const modalTitle = computed(() => {
    if (props.type === 'follower') {
        return '팔로워 목록'
    } else if (props.type === 'following') {
        return '팔로잉 목록'
    }
    return '사용자 검색' // 기본값
})

// const users = ref([])
// 기본 이미지 URL (안정적인 URL 사용)
const defaultImage = 'https://placehold.co/40x40'

const userStore = useUserStore()
// const router = useRouter() // 라우터 인스턴스 생성

const closeModal = () => {
    emit('close')
}

const navigateToProfile = (user) => {
    // 예시: 라우트 경로가 /profile/:profileId 인 경우
    router.push({ path: `/profile/${user.profileId}` })
    closeModal()
}

const users = ref([]) // 팔로워 목록 저장

watch(
    () => [userStore.token, props.userId, props.type],
    async ([newToken, newUserId, newType]) => {
        if (newToken && newUserId) {
            try {
                let result

                if (newType === 'follower') {
                    result = await readFollowers(newToken, newUserId)
                } else {
                    result = await readFollowings(newToken, newUserId)
                }

                users.value = result.content || [] // ✅ 데이터 저장
            } catch (error) {
                console.error('❌ 목록 조회 실패:', error)
            }
        }
    },
    { immediate: true }
)
</script>

<style scoped>
.no-scrollbar::-webkit-scrollbar {
    display: none;
}
.no-scrollbar {
    scrollbar-width: none;
    -ms-overflow-style: none;
}
</style>
