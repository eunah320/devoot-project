import axios from 'axios'

const instance = axios.create({
    baseURL: 'http://localhost:8080',
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

// 유저 정보 가져오는 API 함수
const getUserInfo = async (token) => {
    return instance.get('/api/users/me', {
        headers: { Authorization: `Bearer ${token}` },
    })
}

// 유저 정보 수정하는 API 함수
const updateUserInfo = async (token, formData) => {
    return instance.put('/api/users/register', formData, {
        headers: {
            Authorization: `Bearer ${token}`,
        },
    })
}

// 유저 회원가입 API 함수
const registerUser = async (token, formData) => {
    return instance.post('/api/users/register', formData, {
        headers: {
            Authorization: `Bearer ${token}`,
        },
    })
}

// 유저 이메일 중복확인 API 함수
const checkProfileId = async (token, profileId) => {
    return instance.get('/api/users/check-profile-id', {
        headers: { Authorization: `Bearer ${token}` },
        params: { profileId },
    })
}

export { getUserInfo, updateUserInfo, registerUser, checkProfileId }
export default instance
