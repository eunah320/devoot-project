import axios from 'axios'
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

// 잔디 conrtribution 개수 불러오기
const getContributions = async (selectedYear, token, userId) => {
    return instance.get(`/api/users/${userId}/todos/contributions`, {
        params: { year: selectedYear },
        headers: { Authorization: `Bearer ${token}` },
    })
}
export { getContributions }
export default instance
