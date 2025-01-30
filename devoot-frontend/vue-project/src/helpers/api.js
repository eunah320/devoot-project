import axios from 'axios'
import router from '../router'

const instance = axios.create({
    baseURL: 'http://localhost:8080',
})

instance.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response) {
            switch (error.response.status) {
                case 401:
                    console.log('401 Unauthorized')
                    alert('로그인이 필요합니다. 다시 로그인해주세요.')
                    router.push({ name: 'login' }) // 로그인 페이지로 이동
                    break
                case 404:
                    console.log('User Not Found (404)')
                    break
                default:
                    console.log('Other error:', error.response.status)
                    alert(`에러 발생: ${error.response.status}`)
            }
        } else {
            console.error('No response from server', error)
        }
        return Promise.reject(error)
    }
)

// 유저 정보 가져오는 API 함수
export const getUserInfo = async (token) => {
    return instance.get('/api/users/me', {
        headers: { Authorization: `Bearer ${token}` },
    })
}

export default instance
