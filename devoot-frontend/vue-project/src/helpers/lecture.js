import axios from 'axios'

const instance = axios.create({
    // baseURL: 'http://localhost:8080',
    baseURL: 'https://d360cba8-fcbe-47c7-b19f-a38bcd9a5824.mock.pstmn.io', // Postman Mock Server 주소
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

//===============================================
// 북마크 관련 API
//===============================================

// 북마크 추가
const addBookmark = async (token, profileId, lectureId) => {
    return instance.post(
        `/api/users/${profileId}/bookmarks`,
        { lectureId }, // body에 포함
        {
            headers: { Authorization: `Bearer ${token}` },
        }
    )
}

// 북마크 제거
const removeBookmark = async (token, profileId, lectureId) => {
    return instance.delete(`/api/users/${profileId}/bookmarks/${lectureId}`, {
        headers: { Authorization: `Bearer ${token}` },
    })
}

export { addBookmark, removeBookmark }
export default instance
