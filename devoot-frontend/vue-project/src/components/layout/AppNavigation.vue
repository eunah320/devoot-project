<template>
    <div
        id="navigation"
        class="w-[4.5rem] lg:w-[13.5rem] flex flex-col h-screen bg-white border-r ease-in-out border-gray-200 transition-all lg:transition-transform duration-500"
    >
        <!-- 로고 -->
        <div id="logo" class="flex flex-row items-center justify-center h-20 gap-3">
            <Logo class="w-8 h-8" />
            <div class="hidden text-h1 lg:block">개발바닥</div>
        </div>

        <!-- 메뉴 -->
        <div id="menu-container" class="flex flex-col flex-1 py-3">
            <!-- 서비스 관련 메뉴 -->
            <div id="menu">
                <div
                    v-for="(menu, index) in serviceMenu"
                    :key="index"
                    class="flex flex-row items-center justify-center gap-5 px-0 py-4 cursor-pointer lg:justify-start lg:px-9 hover:text-black"
                    :class="selectedMenu === menu.name ? 'text-black' : 'text-gray-300'"
                    @click="navigateTo(menu)"
                >
                    <component
                        :is="selectedMenu === menu.name ? menu.iconFill : menu.iconDefault"
                        class="w-6 h-6"
                    />
                    <p class="hidden text-left text-body lg:block">
                        {{ menu.label }}
                    </p>
                </div>
            </div>

            <!-- 간격용 -->
            <div class="flex-1"></div>

            <!-- 회원 관련 메뉴-->
            <div id="member-menu">
                <div
                    v-for="(menu, index) in memberMenu"
                    :key="index"
                    class="flex flex-row items-center justify-center gap-5 px-0 py-4 cursor-pointer lg:justify-start lg:px-9 hover:text-black"
                    :class="selectedMenu === menu.name ? 'text-black' : 'text-gray-300'"
                    @click="navigateTo(menu)"
                >
                    <component :is="menu.icon" class="w-6 h-6" />
                    <p class="hidden text-left text-body lg:block">{{ menu.label }}</p>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import Logo from '@/assets/icons/logo.svg'
import HomeDefault from '@/assets/icons/home_default.svg'
import HomeFill from '@/assets/icons/home_fill.svg'
import LectureDefault from '@/assets/icons/lecture_default.svg'
import LectureFill from '@/assets/icons/lecture_fill.svg'
import TimelineDefault from '@/assets/icons/timeline_default.svg'
import TimelineFill from '@/assets/icons/timeline_fill.svg'
import ProfileDefault from '@/assets/icons/profile_default.svg'
import ProfileFill from '@/assets/icons/profile_fill.svg'
import LogIn from '@/assets/icons/login.svg'
import UpdateProfile from '@/assets/icons/update_profile.svg'
import LogOut from '@/assets/icons/logout.svg'

// 라우터 사용
const router = useRouter()
// 선택된 메뉴를 저장하는 변수 (기본값: 'home')
const selectedMenu = ref('home')

// 사용자 ID (변수)
const userId = ref(1)

// 메뉴를 선택했을 때 호출되는 함수
const navigateTo = (menu) => {
    selectedMenu.value = menu.name
    const path = menu.path.replace(':id', userId.value) // 경로의 :id를 userId로 대체
    router.push(path) // 메뉴에 지정된 경로로 이동
}

// 서비스 메뉴 항목 배열
const serviceMenu = [
    { name: 'home', label: '홈', path: '/', iconDefault: HomeDefault, iconFill: HomeFill },
    { name: 'lecture', label: '강의', path: '/lecture', iconDefault: LectureDefault, iconFill: LectureFill },
    { name: 'timeline', label: '타임라인', path: '/timeline', iconDefault: TimelineDefault, iconFill: TimelineFill },
    { name: 'profile', label: '프로필', path: '/profile/:id', iconDefault: ProfileDefault, iconFill: ProfileFill },
]

// 회원 관련 메뉴 항목 배열
const memberMenu = [
    { name: 'login', label: '로그인/회원가입', path: '/login', icon: LogIn },
    { name: 'edit-profile', label: '회원정보 수정', path: '/profile/:id/edit', icon: UpdateProfile },
    { name: 'logout', label: '로그아웃', path: '/', icon: LogOut }, // 로그아웃은 별도 로직 필요
]
</script>

<style scoped></style>
