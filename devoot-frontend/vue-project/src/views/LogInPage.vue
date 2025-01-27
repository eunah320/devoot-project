<template>
    <div class="col-span-12 gap-4 lg:col-start-4 lg:col-span-6">
        <div class="flex flex-col gap-4 p-16 border border-gray-200 rounded-2xl">
            <!-- 로고 -->
            <div id="logo" class="flex flex-row items-center gap-3">
                <Logo class="w-8 h-8" />
                <div class="text-h1">개발바닥</div>
            </div>

            <!-- 멘트 -->
            <div class="text-gray-300 text-h2">
                개발의 모든 것, <br />
                개발바닥에서 시작해보세요!
            </div>

            <!-- 소셜 로그인 버튼 -->
            <div class="flex flex-row gap-4">
                <button
                    class="flex flex-row items-center justify-center w-full h-12 gap-4 text-black border border-gray-200 rounded-lg hover:bg-gray-200"
                    @click="handleGoogleLogin"
                >
                    <Google class="w-6 h-6" />
                    <span class="text-body-bold">Google로 로그인</span>
                </button>
            </div>
            <div class="flex flex-row gap-4">
                <button
                    class="flex flex-row items-center justify-center w-full h-12 gap-4 text-black border border-gray-200 rounded-lg hover:bg-gray-200"
                    @click="handleGithubLogin"
                >
                    <Github class="w-6 h-6" />
                    <span class="text-body-bold">GitHub로 로그인</span>
                </button>
            </div>
        </div>
    </div>
</template>

<script setup>
import Logo from '@/assets/icons/logo.svg'
import Google from '@/assets/icons/google.svg'
import Github from '@/assets/icons/github.svg'

const handleGoogleLogin = () => {
    console.log('Google Login')
    // 구글 OAuth 로그인 페이지로 리디렉션
}

const handleGithubLogin = () => {
    console.log('Github Login')
    // 깃허브 OAuth 로그인 페이지로 리디렉션
}
</script>

<style scoped></style>
