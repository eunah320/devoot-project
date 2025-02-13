<template>
    <div v-if="ProfileData" class="flex flex-col gap-y-8">
        <div class="flex justify-center col-span-12 gap-[26px] pb-11">
            <div class="w-fit h-fit px-[13px] py-[3px]">
                <img
                    :src="ProfileData.imageUrl"
                    alt="이미지"
                    class="bg-gray-200 w-[144px] h-[144px] rounded-full"
                />
            </div>
            <div class="flex flex-col w-[760px] gap-8 px-8 py-4">
                <div class="flex justify-between w-full">
                    <div class="flex flex-col flex-1 gap-2 h-fit">
                        <p class="flex items-center h-8 text-h3">{{ ProfileData.profileId }}</p>
                        <p class="flex items-center h-6 text-body">{{ ProfileData.nickname }}</p>
                    </div>
                    <div class="flex flex-col gap-2">
                        <div class="flex gap-3">
                            <div class="flex items-center w-[378px] h-[32px] gap-6">
                                <div class="flex items-center gap-2">
                                    <p class="text-gray-400 text-caption">북마크한 강의 수</p>
                                    <p class="text-body-bold">
                                        {{
                                            ProfileData.bookmarkCnt > 99
                                                ? '99+'
                                                : ProfileData.bookmarkCnt
                                        }}
                                    </p>
                                </div>
                                <div class="flex items-center gap-2 cursor-pointer">
                                    <p class="text-gray-400 text-caption">팔로워</p>
                                    <p class="text-body-bold">
                                        {{
                                            ProfileData.followerCnt > 99
                                                ? '99+'
                                                : ProfileData.followerCnt
                                        }}
                                    </p>
                                </div>
                                <div class="flex items-center gap-2">
                                    <p class="text-gray-400 text-caption">팔로잉</p>
                                    <p class="cursor-pointer text-body-bold">
                                        {{
                                            ProfileData.followingCnt > 99
                                                ? '99+'
                                                : ProfileData.followingCnt
                                        }}
                                    </p>
                                </div>
                            </div>
                            <button
                                v-if="ProfileData?.followStatus !== null"
                                :class="{
                                    'button-primary': ProfileData?.followStatus === 'NOTFOLLOWING',
                                    'button-gray': ProfileData?.followStatus === 'FOLLOWING',
                                    'button-gray cursor-default':
                                        ProfileData?.followStatus === 'PENDING',
                                }"
                                @click="
                                    handleFollowClick(
                                        userToken,
                                        route.params.id,
                                        ProfileData.followId
                                    )
                                "
                            >
                                {{
                                    ProfileData?.followStatus === 'NOTFOLLOWING'
                                        ? '팔로우'
                                        : ProfileData?.followStatus === 'FOLLOWING'
                                          ? '팔로우 취소'
                                          : ProfileData?.followStatus === 'PENDING'
                                            ? '요청 대기중'
                                            : ''
                                }}
                            </button>
                        </div>
                        <div
                            class="flex items-center h-6 text-gray-400 cursor-pointer text-caption"
                        >
                            <a :href="ProfileData.links.url">{{ ProfileData.links.title }}</a>
                        </div>
                    </div>
                </div>
                <!-- Tag Section -->
                <div class="flex gap-1.5 w-full">
                    <div
                        v-for="tag in ProfileData.tags.split(',')"
                        :key="tag"
                        class="inline-flex gap-1 text-caption-sm tag-gray"
                    >
                        <p>#</p>
                        <p
                            class="overflow-hidden cursor-default text-ellipsis whitespace-nowrap"
                            :title="tag"
                        >
                            {{ tag }}
                        </p>
                    </div>
                </div>
            </div>
        </div>
        <ProfileContribution
            v-if="userToken && ProfileData.isPublic"
            :user-id="route.params.id"
            :token="userToken"
        />
        <TodoList
            v-if="userToken && ProfileData.isPublic"
            :user-id="route.params.id"
            :token="userToken"
            :follow-status="ProfileData.followStatus"
            @open-add-modal="isAddModalOpen = true"
        />

        <!-- 할 일 추가하기 모달 (TodoList 아래에 위치) -->
        <TodoAddModal
            v-if="isAddModalOpen && userToken && ProfileData.isPublic"
            :user-id="route.params.id"
            :token="userToken"
            @close="isAddModalOpen = false"
        />

        <div v-if="ProfileData.isPublic" class="border border-gray-200 rounded-[20px]">
            <TabMenu v-model="selectedTab" tab-left="북마크한 강의" tab-right="내가 쓴 리뷰" />
            <KanbanSection
                v-if="userToken && userData && selectedTab === 'left'"
                :user-id="route.params.id"
                :token="userToken"
            />
            <ProfileReviewSection
                v-if="userToken && userData && selectedTab === 'right'"
                :user-id="route.params.id"
                :token="userToken"
            />
        </div>
    </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import axios from 'axios'
import TabMenu from '@/components/Common/TabMenu.vue'
import { useUserStore } from '@/stores/user'
import { useRoute } from 'vue-router' // ✅ useRoute 훅 불러오기

const route = useRoute() // ✅ 라우트 정보 가져오기
const isAddModalOpen = ref(false) // TodoAddModal 상태 관리
const selectedTab = ref('left') // TabMenu 관리, 기본값: 칸반 섹션

const userStore = useUserStore() // Pinia 스토어 가져오기

// 사용자 정보 가져오기
const userId = computed(() => userStore.userId)
const userData = computed(() => userStore.user)
const userToken = computed(() => userStore.token)
const isLoaded = ref(false)

const ProfileData = ref([])

const loadProfileDatas = async (token, id) => {
    try {
        const mock_server_url = 'http://localhost:8080'
        const API_URL = `${mock_server_url}/api/users/${id}`

        const response = await axios.get(API_URL, {
            headers: {
                'Content-Type': 'application/json',
                Authorization: `Bearer ${token}`, // ✅ 토큰 전달
            },
        })

        ProfileData.value = response.data
        // console.log('📚 콘솔마이데이터:', myData.value)
    } catch (error) {
        console.error('❌ 에러 발생:', error)
    }
}

// 사용자 정보와 토큰의 상태 변화를 감지
watch(
    () => [userData.value, userToken.value, userId.value, route.params.id], // ✅ 여러 값을 동시에 감시
    async ([newUser, newToken, newUserId, newId]) => {
        if (newUser && newToken && newUserId && newId) {
            // console.log('✅ 사용자 정보와 토큰이 준비되었습니다.')
            // console.log('유저데이터:', newUser)
            console.log('유저토큰:', newToken)
            // console.log('유저아이디:', newUserId)

            if (!isLoaded.value) {
                await loadProfileDatas(newToken, newId) // ✅ 토큰을 전달해서 데이터 로드
                // isLoaded.value = true // ✅ 로딩 상태 true로 변경
            }
        }
    },
    { immediate: true } // ✅ 초기값도 확인
)

// 팔로우 요청 함수
const sendFollowRequest = async (token, userId) => {
    // console.log('API: 팔로우 요청 전송')
    try {
        const mock_server_url = 'http://localhost:8080'
        const API_URL = `${mock_server_url}/api/follows`

        const response = await axios.post(
            API_URL,
            {
                profileId: userId, // 팔로우 할 사용자의 id(route로 넘어오는)
            },
            {
                headers: {
                    'Content-Type': 'application/json', //필수 헤더 추가
                    Authorization: `Bearer ${token}`, // 필요 시 Bearer 토큰 추가
                },
            }
        )
        console.log('응답', response)
        // 상태 업데이트 (프론트엔드에서도 즉시 반영)
    } catch (error) {
        console.error('에러:', error)
        console.log('프로필토큰', token)
        console.log('프로필페이지 주인 id', userId)
    }
}

// 팔로우 취소 함수
const cancelFollowRequest = async (token, followId) => {
    // console.log('API: 팔로우 취소 요청 전송')
    try {
        const mock_server_url = 'http://localhost:8080'
        const API_URL = `${mock_server_url}/api/follows/${followId}`
        const response = await axios.delete(API_URL, {
            headers: {
                Authorization: `Bearer ${token}`, // Bearer 토큰을 헤더에 포함
            },
        })
        console.log('응답', response)
    } catch (error) {
        console.error('에러:', error)
    }
}

// 팔로우 버튼 클릭시 요청 함수
const handleFollowClick = async (token, userId, followId) => {
    try {
        if (ProfileData.value.followStatus === 'NOTFOLLOWING') {
            console.log('팔로우 요청 중...')
            await sendFollowRequest(token, userId) // 팔로우 요청 함수 호출
            ProfileData.value.followStatus = 'PENDING' // 상태 업데이트
        } else if (ProfileData.value.followStatus === 'FOLLOWING') {
            console.log('팔로우 취소 요청 중...')
            await cancelFollowRequest(token, followId) // 팔로우 취소 함수 호출
            ProfileData.value.followStatus = 'NOTFOLLOWING' // 상태 업데이트
        }
    } catch (error) {
        console.error('❌ 요청 중 오류 발생:', error)
    }
}
import ProfileContribution from '@/components/Profile/ProfileContribution.vue'
import KanbanSection from '@/components/Profile/KanbanSection.vue'
import TodoAddModal from '@/components/Profile/TodoAddModal.vue'
import TodoList from '@/components/Profile/TodoList.vue'
import ProfileReviewSection from '@/components/Profile/ProfileReviewSection.vue'
</script>

<style scoped></style>
<style scoped></style>
