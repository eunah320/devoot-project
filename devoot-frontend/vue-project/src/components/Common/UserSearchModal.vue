<template>
    <!-- 모달 외부 영역 추가 -->
    <div v-if="isOpen" class="fixed inset-0 z-40" @click="closeModal">
        <!-- 모달 컨테이너 -->
        <div
            class="absolute top-20 left-[13.5rem] z-50 bg-white shadow-lg rounded-lg w-[22.9375rem] h-[calc(100vh-5rem)]"
            @click.stop
        >
            <div class="flex flex-col h-full p-4">
                <!-- 기존 모달 내용 -->
                <div class="flex items-center justify-between mb-4">
                    <h2 class="text-base font-bold">사용자 검색</h2>
                    <button @click="closeModal" class="text-gray-500 hover:text-black">
                        &times;
                    </button>
                </div>
                <div class="relative">
                    <input
                        v-model="searchQuery"
                        type="text"
                        placeholder="검색"
                        class="w-full py-2 pl-4 pr-8 text-sm border rounded bg-gray-50 focus:outline-none"
                    />
                </div>
                <ul class="flex-1 mt-4 overflow-y-auto">
                    <li
                        v-for="(user, index) in filteredUsers"
                        :key="index"
                        class="flex items-center py-2 space-x-3"
                    >
                        <div class="w-8 h-8 bg-gray-200 rounded-full"></div>
                        <div>
                            <p class="text-sm font-medium">{{ user.username }}</p>
                            <p class="text-xs text-gray-500">{{ user.nickname }}</p>
                        </div>
                    </li>
                </ul>
            </div>
        </div>
    </div>
</template>

<script>
export default {
    props: {
        isOpen: {
            type: Boolean,
            required: true,
        },
    },
    data() {
        return {
            searchQuery: '',
            users: [
                { username: 'sae_balbadak', nickname: '새발바닥' },
                { username: 'user_1', nickname: '사용자1' },
                { username: 'user_2', nickname: '사용자2' },
                { username: 'gae_balbadak', nickname: '개발바닥' },
                { username: 'user_3', nickname: 'user_3' },
                { username: 'user_4', nickname: 'user_4' },
            ],
        }
    },
    computed: {
        filteredUsers() {
            return this.users.filter(
                (user) =>
                    user.username.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
                    user.nickname.toLowerCase().includes(this.searchQuery.toLowerCase())
            )
        },
    },
    methods: {
        closeModal() {
            this.$emit('close')
        },
    },
}
</script>

<style scoped>
ul {
    scrollbar-width: none;
    -ms-overflow-style: none;
}

ul::-webkit-scrollbar {
    display: none;
}
</style>
