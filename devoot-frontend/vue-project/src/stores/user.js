import { defineStore } from 'pinia'
import {
    auth,
    googleProvider,
    githubProvider,
    signInWithPopup,
    setPersistence,
    browserLocalPersistence,
} from '@/firebase'
import instance from '@/helpers/api'
import { getUserInfo } from '@/helpers/api' // API 함수 불러오기
import router from '../router'

export const useUserStore = defineStore('user', {
    state: () => ({
        user: null, // 사용자 정보
        token: null, // Firebase 토큰
    }),

    getters: {
        isAuthenticated: (state) => !!state.token, // 로그인 여부 -> 동적으로 계산
    },

    actions: {
        // 구글 로그인
        async loginWithGoogle() {
            try {
                await setPersistence(auth, browserLocalPersistence) // 로그인 유지

                const result = await signInWithPopup(auth, googleProvider)

                // Firebase에서 받아온 사용자 정보 저장
                this.user = result.user
                this.token = await result.user.getIdToken(true)

                // API 파일에서 getUserInfo() 호출
                const res = await getUserInfo(this.token)

                console.log('Login success! user:', res.data)

                return true // 로그인 성공
            } catch (error) {
                return '로그인 중 오류가 발생했습니다.'
            }
        },

        // 깃허브 로그인
        async loginWithGithub() {
            try {
                await setPersistence(auth, browserLocalPersistence) // 로그인 유지

                const result = await signInWithPopup(auth, githubProvider)

                // Firebase에서 받아온 사용자 정보 저장
                this.user = result.user
                this.token = await result.user.getIdToken(true)

                // API 파일에서 getUserInfo() 호출
                const res = await getUserInfo(this.token)

                console.log('Login success! user:', res.data)

                return true // 로그인 성공
            } catch (error) {
                return '로그인 중 오류가 발생했습니다.'
            }
        },

        async logout() {
            await auth.signOut()
            this.user = null
            this.token = null
            router.push({ name: 'home' }) // 홈 페이지로 이동
            console.log('Logout success!')
        },
    },
})
