<template>
    <div>
        <div class="flex justify-center col-span-12 gap-[26px] pb-11">
            <div class="w-fit h-fit px-[13px] py-[3px]">
                <img src="" alt="" class="bg-gray-200 w-[144px] h-[144px] rounded-full" />
            </div>
            <div class="flex flex-col w-[760px] gap-8 px-8 py-4">
                <div class="flex justify-between w-full">
                    <div class="flex flex-col flex-1 gap-2 h-fit">
                        <p class="flex items-center h-8 text-h3">ID</p>
                        <p class="flex items-center h-6 text-body">닉네임</p>
                    </div>
                    <div class="flex flex-col gap-2">
                        <div class="flex gap-3">
                            <div class="flex items-center w-[378px] h-[32px] gap-6">
                                <div class="flex items-center gap-2">
                                    <p class="text-gray-400 text-caption">북마크한 강의 수</p>
                                    <p class="text-body-bold">99+</p>
                                </div>
                                <div class="flex items-center gap-2">
                                    <p class="text-gray-400 text-caption">팔로워</p>
                                    <p class="text-body-bold">99+</p>
                                </div>
                                <div class="flex items-center gap-2">
                                    <p class="text-gray-400 text-caption">팔로잉</p>
                                    <p class="text-body-bold">99+</p>
                                </div>
                            </div>
                            <button class="button-primary">팔로우</button>
                        </div>
                        <div class="flex items-center h-6 text-gray-400 text-caption">
                            개발바닥 깃허브
                        </div>
                    </div>
                </div>
                <div>태그들</div>
            </div>
        </div>
        <ProfileContribution />
        <TodoList @open-add-modal="isAddModalOpen = true" />

        <!-- 할 일 추가하기 모달 (TodoList 아래에 위치) -->
        <TodoAddModal v-if="isAddModalOpen" @close="isAddModalOpen = false" />
        <div class="border border-gray-200 rounded-[20px]">
            <TabMenu
                tab-left="북마크한 강의"
                tab-right="내가 쓴 리뷰"
                @update-tab="handleTabChange"
            />
            <component :is="currentComponent" />
        </div>
    </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import TabMenu from '@/components/Common/TabMenu.vue'
import { useUserStore } from '@/stores/user'
const isAddModalOpen = ref(false)

const userStore = useUserStore() // Pinia 스토어 가져오기

// 📌 사용자 정보 가져오기
const userData = computed(() => userStore.user)

onMounted(() => {
    if (!userData.value) {
        console.log('사용자 정보가 비어 있습니다.')
        // 필요 시 userStore에서 사용자 정보를 불러오는 로직 추가
    } else {
        console.log('사용자 정보:', userData.value)
    }
})

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
