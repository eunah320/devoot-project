<template>
    <div v-if="isLoaded">
        <div class="flex justify-center col-span-12 gap-[26px] pb-11">
            <div class="w-fit h-fit px-[13px] py-[3px]">
                <img
                    :src="userData.photoURL"
                    alt="이미지"
                    class="bg-gray-200 w-[144px] h-[144px] rounded-full"
                />
            </div>
            <div class="flex flex-col w-[760px] gap-8 px-8 py-4">
                <div class="flex justify-between w-full">
                    <div class="flex flex-col flex-1 gap-2 h-fit">
                        <p class="flex items-center h-8 text-h3">{{ userId }}</p>
                        <p class="flex items-center h-6 text-body">{{ userData.displayName }}</p>
                    </div>
                    <div class="flex flex-col gap-2">
                        <div class="flex gap-3">
                            <div class="flex items-center w-[378px] h-[32px] gap-6">
                                <div class="flex items-center gap-2">
                                    <p class="text-gray-400 text-caption">북마크한 강의 수</p>
                                    <p class="text-body-bold">
                                        {{ myData.bookmarkCnt > 99 ? '99+' : myData.bookmarkCnt }}
                                    </p>
                                </div>
                                <div class="flex items-center gap-2">
                                    <p class="text-gray-400 text-caption">팔로워</p>
                                    <p class="text-body-bold">
                                        {{ myData.followerCnt > 99 ? '99+' : myData.followerCnt }}
                                    </p>
                                </div>
                                <div class="flex items-center gap-2">
                                    <p class="text-gray-400 text-caption">팔로잉</p>
                                    <p class="text-body-bold">
                                        {{ myData.followingCnt > 99 ? '99+' : myData.followingCnt }}
                                    </p>
                                </div>
                            </div>
                            <button class="button-primary">팔로우</button>
                        </div>
                        <div class="flex items-center h-6 text-gray-400 text-caption">
                            개발바닥 깃허브
                        </div>
                    </div>
                </div>
                <!-- Tag Section -->
                <div class="flex gap-1.5 w-full">
                    <div
                        v-for="tag in myData.tags.split(',')"
                        :key="tag"
                        class="inline-flex gap-1 text-caption-sm tag-gray max-w-[60px]"
                    >
                        <p>#</p>
                        <p
                            class="overflow-hidden cursor-pointer text-ellipsis whitespace-nowrap"
                            :title="tag"
                        >
                            {{ tag }}
                        </p>
                    </div>
                </div>
            </div>
        </div>
        <ProfileContribution v-if="userToken && userData" :user="userData" :token="userToken" />
        <TodoList
            v-if="userToken && userData"
            @open-add-modal="isAddModalOpen = true"
            :user-id="userId"
            :token="userToken"
        />

        <!-- 할 일 추가하기 모달 (TodoList 아래에 위치) -->
        <TodoAddModal
            v-if="isAddModalOpen && userToken && userData"
            @close="isAddModalOpen = false"
            :user="userData"
            :token="token"
        />
        <div class="border border-gray-200 rounded-[20px]">
            <TabMenu
                tab-left="북마크한 강의"
                tab-right="내가 쓴 리뷰"
                @update-tab="handleTabChange"
            />
            <component
                v-if="userToken && userData"
                :user-id="userId"
                :token="userToken"
                :is="currentComponent"
            />
        </div>
    </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import axios from 'axios'
import TabMenu from '@/components/Common/TabMenu.vue'
import { useUserStore } from '@/stores/user'
const isAddModalOpen = ref(false)

const userStore = useUserStore() // Pinia 스토어 가져오기

// 사용자 정보 가져오기
const userId = computed(() => userStore.userId)
const userData = computed(() => userStore.user)
const userToken = computed(() => userStore.token)
const isLoaded = ref(false)

const myData = ref([])

const loadMyDatas = async (token, userId) => {
    try {
        const mock_server_url = 'http://localhost:8080'
        const API_URL = `${mock_server_url}/api/users/${userId}`

        const response = await axios.get(API_URL, {
            headers: {
                'Content-Type': 'application/json',
                Authorization: `Bearer ${token}`, // ✅ 토큰 전달
            },
        })

        myData.value = response.data
        // console.log('📚 콘솔마이데이터:', myData.value)
    } catch (error) {
        console.error('❌ 에러 발생:', error)
    }
}

// 사용자 정보와 토큰의 상태 변화를 감지
watch(
    () => [userData.value, userToken.value, userId.value], // ✅ 여러 값을 동시에 감시
    async ([newUser, newToken, newUserId]) => {
        if (newUser && newToken && newUserId) {
            // console.log('✅ 사용자 정보와 토큰이 준비되었습니다.')
            // console.log('유저데이터:', newUser)
            console.log('유저토큰:', newToken)
            // console.log('유저아이디:', newUserId)

            if (!isLoaded.value) {
                await loadMyDatas(newToken, newUserId) // ✅ 토큰을 전달해서 데이터 로드
                isLoaded.value = true // ✅ 로딩 상태 true로 변경
            }
        }
    },
    { immediate: true } // ✅ 초기값도 확인
)

// 현재 선택된 탭 (기본값 : 'left')
const currentTab = ref('left')

// 탭에 따라 렌더링할 컴포넌트 설정
const currentComponent = computed(() => {
    return currentTab.value === 'left' ? KanbanSection : ProfileReviewSection
})

// 탭 변경 이벤트 핸들러
const handleTabChange = (tab) => {
    currentTab.value = tab
}
import ProfileContribution from '@/components/Profile/ProfileContribution.vue'
import KanbanSection from '@/components/Profile/KanbanSection.vue'
import TodoAddModal from '@/components/Profile/TodoAddModal.vue'
import TodoList from '@/components/Profile/TodoList.vue'
import ProfileReviewSection from '@/components/Profile/ProfileReviewSection.vue'
</script>

<style scoped></style>
<style scoped></style>
