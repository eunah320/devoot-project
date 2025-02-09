<template>
    <div class="flex flex-col border border-gray-200 rounded-[20px] w-full items-center p-6 gap-6">
        <div class="flex items-center justify-between w-full">
            <p class="text-h3">할 일 목록</p>
            <button class="flex gap-1 p-1 button-primary" @click="$emit('open-add-modal')">
                <Plus class="w-[1.125rem] h-[1.125rem]" />
                <p>할 일 추가하기</p>
            </button>
        </div>
        <div class="flex items-center justify-between w-full">
            <div class="flex items-center gap-2 text-black">
                <NavigateLeft
                    class="w-[1.125rem] h-[1.125rem] cursor-pointer"
                    @click="NavigateDay(-1)"
                />
                <span class="text-h3"
                    >{{ selectedDate.getMonth() + 1 }}월 {{ selectedDate.getDate() }}일</span
                >
                <NavigateRight
                    class="w-[1.125rem] h-[1.125rem] cursor-pointer"
                    @click="NavigateDay(1)"
                />
            </div>
            <button class="flex gap-1 p-1 button-line" @click="moveUndone()">
                <Arrow class="w-[1.125rem] h-[1.125rem]" />
                <p>미완료 할 일 내일로 미루기</p>
            </button>
        </div>
        <div
            v-for="todo in todos"
            :key="todo.id"
            class="flex items-center w-full h-[4.25rem] rounded-lg border border-gray-200 px-1"
        >
            <div>
                <Move class="w-6 h-6 text-gray-300" />
            </div>
            <div class="flex items-center gap-3 w-full h-full py-2.5 pr-4 border-r border-gray-200">
                <div
                    class="flex items-center justify-center w-5 h-5 border border-gray-200 rounded cursor-pointer"
                    :class="todo.finished ? 'bg-primary-500 ' : 'bg-white'"
                    @click="updateTodoStatus(todo)"
                >
                    <Check v-if="todo.finished" class="w-[1.125rem] h-[1.125rem] text-white" />
                </div>
                <p class="text-body">{{ todo.lectureName }}</p>
                <a :href="todo.sourceUrl" class="text-gray-300 text-caption-sm">강의 상세로 이동</a>
            </div>
            <div class="flex items-center justify-between w-full px-4">
                <p class="text-body">{{ todo.subLectureName }}</p>
                <Delete class="w-6 h-6 cursor-pointer" @click="deleteTodo(todo)" />
            </div>
        </div>
    </div>
</template>

<script setup>
import TodoAddModal from './TodoAddModal.vue'
import Plus from '@/assets/icons/plus.svg'
import NavigateLeft from '@/assets/icons/navigate_left.svg'
import NavigateRight from '@/assets/icons/navigate_right.svg'
import Arrow from '@/assets/icons/arrow.svg'
import Move from '@/assets/icons/move.svg'
import Delete from '@/assets/icons/delete.svg'
import Check from '@/assets/icons/check.svg'
import { onMounted, ref } from 'vue'
import axios from 'axios'
import { useUserStore } from '@/stores/user'
const isAddModalOpen = ref(false)

const userStore = useUserStore() // Pinia 스토어 가져오기

// 오늘 날짜 가져오기
// const today = new Date()

// 기본 날짜를 오늘 날짜로 설정
const selectedDate = ref(new Date()) // Date 객체로 설정
// console.log(selectedDate.value)

const NavigateDay = (day) => {
    const newDate = new Date(selectedDate.value)
    // console.log('전 selectedDate', selectedDate.value)
    newDate.setDate(newDate.getDate() + day)
    // console.log('newDate', newDate)
    selectedDate.value = newDate
    // selectedDate.value = `${newDate.getMonth() + 1}월 ${newDate.getDate()}일`

    // console.log('후 selectedDate', selectedDate.value)
}

const todos = ref([]) // 응답 데이터를 저장할 변수

const getTodos = async () => {
    try {
        const mock_server_url = 'https://d360cba8-fcbe-47c7-b19f-a38bcd9a5824.mock.pstmn.io'
        const profileId = 'l3olvy' // 여기에 실제 사용자 ID를 넣어야 함
        const date = selectedDate.value // 선택한 날짜로 변경해야 함
        console.log('date', date)
        const API_URL = `${mock_server_url}/api/users/${profileId}}/todos?date=${date}`
        // const token = 'asdfasdfasdf' // 여기에 Bearer 토큰을 넣어야 함

        // const response = await axios.get(API_URL)

        const response = await axios.get(API_URL, {
            headers: {
                Authorization: `Bearer ${userStore.token}`, // Bearer 토큰을 헤더에 포함
            },
        })

        // console.log('응답 데이터:', response.data)
        todos.value = response.data // todo 리스트 저장
    } catch (error) {
        console.error('에러:', error)
    }
}
const todoId = ref(null) // 선택한 Todo의 ID를 저장할 변수
// 투두 상태 변경
const updateTodoStatus = async (todo) => {
    console.log('전달된 todo:', todo) // `todo` 값 확인
    try {
        const mock_server_url = 'https://d360cba8-fcbe-47c7-b19f-a38bcd9a5824.mock.pstmn.io'
        const profileId = 'l3olvy' // 여기에 실제 사용자 ID를 넣어야 함
        // const date = '2024-01-01' // 선택한 날짜로 변경해야 함
        todoId.value = todo.id // 선택한 todo의 ID 저장
        console.log('todoId', todoId.value)
        const API_URL = `${mock_server_url}/api/users/${profileId}/todos/${todoId.value}`
        const token = 'asdfasdfasdf' // 여기에 Bearer 토큰을 넣어야 함
        // 상태 반전
        const updatedFinishedStatus = !todo.finished

        const response = await axios.patch(
            API_URL,
            {
                finished: updatedFinishedStatus, // 상태 변경
                nextId: null,
            },
            {
                headers: {
                    'Content-Type': 'application/json', //필수 헤더 추가
                    Authorization: `Bearer ${token}`, // 필요 시 Bearer 토큰 추가
                },
            }
        )
        console.log('응답', response)
        // 상태 업데이트 (프론트엔드에서도 즉시 반영)
        todo.finished = updatedFinishedStatus
    } catch (error) {
        console.error('에러:', error)
    }
}

const deleteTodo = async (todo) => {
    console.log('전달된 todo:', todo) // `todo` 값 확인
    try {
        const mock_server_url = 'https://d360cba8-fcbe-47c7-b19f-a38bcd9a5824.mock.pstmn.io'
        // const profileId = '1' // 여기에 실제 사용자 ID를 넣어야 함
        const profileId = 'l3olvy' // 여기에 실제 사용자 ID를 넣어야 함

        // const date = '2024-01-01' // 선택한 날짜로 변경해야 함
        todoId.value = todo.id // 선택한 todo의 ID 저장
        console.log('todoId', todoId.value)
        const API_URL = `${mock_server_url}/api/users/${profileId}/todos/${todoId.value}`
        // const token = 'asdfasdfasdf' // 여기에 Bearer 토큰을 넣어야 함

        const response = await axios.delete(API_URL)
        // const response = await axios.delete(API_URL, {
        //     headers: {
        //         Authorization: `Bearer ${token}`, // Bearer 토큰을 헤더에 포함
        //     },
        // })
        console.log('응답', response)
        // 삭제된 todo 제외
        todos.value = todos.value.filter((item) => item.id !== todoId.value)
    } catch (error) {
        console.error('에러:', error)
    }
}

const moveUndone = async () => {
    try {
        const mock_server_url = 'https://d360cba8-fcbe-47c7-b19f-a38bcd9a5824.mock.pstmn.io'
        const profileId = 'l3olvy' // 여기에 실제 사용자 ID를 넣어야 함

        const date = new Date()
        date.setDate(date.getDate() + 1) // 다음 날로 설정
        const formattedDate = date.toISOString().split('T')[0] // YYYY-MM-DD 형식
        console.log('date', date)
        console.log('formattedDate', formattedDate)
        const API_URL = `${mock_server_url}/api/users/${profileId}/todos/move-undone?date=${formattedDate}`
        console.log(API_URL)
        const token = 'asdfasdfasdf' // 여기에 Bearer 토큰을 넣어야 함
        console.log('사용 중인 Bearer 토큰:', token)

        const response = await axios.post(
            API_URL,
            {},
            {
                headers: {
                    'Content-Type': 'application/json', // 필수 헤더
                    Authorization: `Bearer ${token}`, // Bearer 토큰 추가
                },
            } // `todos` 키로 배열을 보내기
        )
        console.log('응답', response)
        // 새로운 todo 추가
        todos.value.push(response.data)
    } catch (error) {
        console.error('에러:', error)
    }
}

// 모달 상태 관리
const isModalOpen = ref(false)

// 모달 열기
const openModal = () => {
    isModalOpen.value = true
}

// 모달 닫기
const closeModal = () => {
    isModalOpen.value = false
}

// 함수 실행 (컴포넌트 마운트 시 실행하려면 onMounted 사용 가능)
onMounted(async () => {
    await getTodos() // 비동기 실행 후 값 확인
    // console.log('todos:', todos.value)
})
</script>

<style></style>
