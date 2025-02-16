import { defineStore } from 'pinia'
import { ref, watch } from 'vue'
import axios from 'axios'
import { useUserStore } from './user'
import { API_BASE_URL } from '@/config'

const instance = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json', // JSON 응답 기대
    },
})

instance.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response) {
            console.error(`❌ API 요청 실패 (HTTP ${error.response.status}):`, error.response.data)
        } else {
            console.error('❌ 네트워크 오류 또는 서버 응답 없음:', error)
        }
        return Promise.reject(error) // 호출한 곳에서 추가 처리 가능
    }
)

const userStore = useUserStore() // Pinia 스토어 가져오기

export const useTodoStore = defineStore('todo', () => {
    const todos = ref([]) // 할 일 목록
    const inprogressLectures = ref([])

    // 진행중인 강의 목록 요청(모달에서 사용)
    const getInprogressLecture = async (token, userId) => {
        try {
            const response = await instance.get(`/api/users/${userId}/bookmarks`, {
                headers: { Authorization: `Bearer ${token}` },
            })
            inprogressLectures.value = response.data['in-progress']
        } catch (error) {
            console.error('진행중인 강의 불러오기 에러:', error)
        }
    }
    const addTodo = async (todoData, token, userId) => {
        try {
            const response = await instance.post(
                `/api/users/${userId}/todos`,
                todoData, // body로 todoData 전달
                {
                    headers: { Authorization: `Bearer ${token}` },
                }
            )
            return response.data // 응답 데이터를 반환
        } catch (error) {
            console.error('🚨 Todo 추가 실패:', error)
            console.log('데이터가 문제임?', todoData)
            throw error // 호출한 쪽에서 에러를 처리할 수 있도록 에러를 던짐
        }
    }
    watch(
        () => [userStore.token, userStore.userId, todos, inprogressLectures], // ✅ 두 값을 동시에 감시
        async ([newToken, newUserId, newTodos]) => {
            if (newToken && newUserId) {
                // 두 값이 모두 존재할 때만 실행
                // console.log('✅ 토큰과 userId가 준비되었습니다.')
                await getInprogressLecture(newToken, newUserId)
            }
        },
        { immediate: true } // 이미 값이 존재할 경우 즉시 실행
    )

    return { todos, getInprogressLecture, inprogressLectures, addTodo }
})
