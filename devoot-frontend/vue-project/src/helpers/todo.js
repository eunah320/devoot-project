import instance from './instance' // 공통 axios 인스턴스 가져오기

// 📌 할 일 목록 가져오기
export const getTodos = async (profileId, date) => {
    const response = await instance.get(`/api/users/${profileId}/todos`, {
        params: { date },
    })
    return response.data // 응답 데이터 반환
}

// 📌 할 일 추가하기
export const addTodo = async (profileId, todoData) => {
    const response = await instance.post(`/api/users/${profileId}/todos`, todoData)
    return response.data // 응답 데이터 반환
}

// 📌 할 일 상태 업데이트
export const updateTodoStatus = async (profileId, todoId, finishedStatus) => {
    const response = await instance.patch(`/api/users/${profileId}/todos/${todoId}`, {
        finished: finishedStatus,
    })
    return response.data // 응답 데이터 반환
}

// 📌 할 일 삭제하기
export const deleteTodo = async (profileId, todoId) => {
    const response = await instance.delete(`/api/users/${profileId}/todos/${todoId}`)
    return response.data // 응답 데이터 반환
}

// 📌 미완료 할 일을 다음 날로 미루기
export const moveUndoneTodos = async (profileId, date) => {
    const response = await instance.post(`/api/users/${profileId}/todos/move-undone`, {
        date,
    })
    return response.data // 응답 데이터 반환
}
