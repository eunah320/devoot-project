import { defineStore } from 'pinia'
import {
    getAuth,
    signInWithPopup,
    GoogleAuthProvider,
    setPersistence,
    browserLocalPersistence,
    GithubAuthProvider,
} from 'firebase/auth'
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
                const auth = getAuth()
                await setPersistence(auth, browserLocalPersistence) // 로그인 유지

                const provider = new GoogleAuthProvider()
                const result = await signInWithPopup(auth, provider)

                // Firebase에서 받아온 사용자 정보 저장
                this.user = result.user
                this.token = await result.user.getIdToken(true)
                this.isAuthenticated = true

                // API 파일에서 getUserInfo() 호출
                const res = await getUserInfo(this.token)

                console.log('Login success! user:', res.data)

                return true // 로그인 성공
            } catch (error) {
                if (error.response?.status === 404) {
                    console.error('User not found. Redirecting to register page.')
                    return false // 회원가입 필요
                } else if (error.response?.status === 409) {
                    console.error('User already exists. Please log in.')
                    alert('이미 가입된 계정입니다. 로그인 해주세요.')
                    return null // 중복 가입 방지
                } else {
                    console.error('Unexpected error:', error)
                    alert('예상치 못한 오류가 발생했습니다. 다시 시도해주세요.')
                    return null // 기타 에러
                }
            }
        },

        // 구글 로그인
        async loginWithGithub() {
            try {
                const auth = getAuth()
                await setPersistence(auth, browserLocalPersistence) // 로그인 유지

                const provider = new GithubAuthProvider()
                const result = await signInWithPopup(auth, provider)

                // Firebase에서 받아온 사용자 정보 저장
                this.user = result.user
                this.token = await result.user.getIdToken(true)
                this.isAuthenticated = true

                // API 파일에서 getUserInfo() 호출
                const res = await getUserInfo(this.token)

                console.log('Login success! user:', res.data)

                return true // 로그인 성공
            } catch (error) {
                if (error.response?.status === 404) {
                    console.error('User not found. Redirecting to register page.')
                    return false // 회원가입 필요
                } else if (error.response?.status === 409) {
                    console.error('User already exists. Please log in.')
                    alert('이미 가입된 계정입니다. 로그인 해주세요.')
                    return null // 중복 가입 방지
                } else {
                    console.error('Unexpected error:', error)
                    alert('예상치 못한 오류가 발생했습니다. 다시 시도해주세요.')
                    return null // 기타 에러
                }
            }
        },

        async logout() {
            const auth = getAuth()
            await auth.signOut()
            this.user = null
            this.token = null
            router.push({ name: 'home' }) // 홈 페이지로 이동
        },
    },
})
