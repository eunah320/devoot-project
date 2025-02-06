<template>
    <!-- 전체 컨테이너: 가운데 정렬, 배경색 적용, 너비 지정 -->
    <div class="flex flex-col items-center bg-white w-[1054px] h-fit gap-6 p-6">
        <!-- 상단 강의 추가 섹션 -->
        <div class="flex items-center justify-between w-full text-black text-h3">
            <p>어떤 강의를 추가하시겠어요?</p>
            <div class="flex items-center gap-2">
                <div class="button-primary">강의 추가</div>
                <Delete class="w-6 h-6 bg-white" />
            </div>
        </div>
        <!-- 날짜 선택 및 강의 목록 컨테이너 -->
        <div class="flex flex-col gap-y-2.5 w-[58.25rem]">
            <div
                class="flex items-center border border-gray-200 w-fit h-9 gap-x-2 px-[0.75rem] rounded relative"
                @click="toggleCalendarDropdown"
            >
                <p class="text-body-bold">{{ selectedDate }}</p>
                <NavigateDown class="w-5 h-5" />
            </div>

            <TodoAddModalCalendar
                v-if="isCalendarDropdownOpen"
                @select-date="selectDate"
                @click-outside="closeCalendarDropdown"
                class="absolute top-0 left-0 z-50 scale-75 bg-white border border-gray-300 rounded-lg shadow-lg w-fit"
            />
            <!-- 강의 선택 및 선택된 강의 컨테이너 -->
            <div
                class="flex w-full h-fit rounded-[20px] overflow-hidden bg-gray-100 border border-gray-200"
            >
                <div class="w-[29.125rem] h-[240.8px] overflow-y-auto">
                    <!-- 나중에 :class에서 siteName대신 id로 바꾸기-->
                    <div
                        v-for="lectureData in todoStore.inprogressLectures"
                        :key="lectureData.id"
                        class="flex flex-col h-auto gap-1 px-4 py-3 border border-gray-200"
                        :class="{
                            'bg-primary-100': selectedCourseName === lectureData.courseName,
                            'bg-white': selectedCourseName !== lectureData.courseName,
                        }"
                        @click="selectLecture(lectureData)"
                    >
                        <p class="text-gray-300 text-caption-sm">
                            {{ lectureData.lecture.sourceName }}
                        </p>
                        <p
                            class="overflow-hidden text-black cursor-pointer text-body text-ellipsis whitespace-nowrap"
                            :title="lectureData.lecture.name"
                        >
                            {{ lectureData.lecture.name }}
                        </p>
                    </div>
                </div>
                <!-- 선택된 강의 목록 (오른쪽 영역) -->
                <div class="w-[29.125rem] h-[240.8px] overflow-y-auto">
                    <div
                        v-for="(selectLecture, index) in selectedLectures"
                        :key="index"
                        class="flex flex-col gap-1 px-4 py-3 border border-gray-200"
                        :class="{
                            'bg-primary-100': selectedLectureIndex === index,
                            'bg-white': selectedLectureIndex !== index,
                        }"
                        @click="selectLectureIndex(index)"
                    >
                        <p class="text-gray-300 text-caption-sm">
                            {{ selectLecture.split(':')[0] }}
                        </p>
                        <p
                            class="overflow-hidden text-black cursor-pointer text-body text-ellipsis whitespace-nowrap selectLecture"
                        >
                            {{ selectLecture.split(':')[1] }}
                        </p>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import TodoAddModalCalendar from './TodoAddModalCalendar.vue'
import Delete from '@/assets/icons/delete.svg'
import NavigateDown from '@/assets/icons/navigate_down.svg'
import { ref, onMounted } from 'vue'
import { useTodoStore } from '@/stores/todo'

const todoStore = useTodoStore() // Pinia 스토어 가져오기

// lecturedata가 kanbansection에도 사용되고, todo에도 사용되는데, 그냥 store로 옮길까..?

// const lectureDatas = ref([]) // 전체 강의 목록
const selectedLectures = ref([]) // 선택된 강의의 lectures 배열
// const selectedLectureId = ref(null) // 현재 선택된 강의의 ID
const selectedCourseName = ref(null) // 현재 선택된 강의의 ID
const selectedLectureIndex = ref(null) // 클릭한 강의의 인덱스 저장

// 오늘 날짜 가져오기
const today = new Date()
const formattedDate = `${today.getMonth() + 1}월 ${today.getDate()}일`

// 기본 날짜를 오늘 날짜로 설정
const selectedDate = ref(formattedDate)
const isCalendarDropdownOpen = ref(false) // 드롭다운 상태

// const loadLectureDatas = async () => {
//     const response = await fetch('/todoaddmodal_dummy_data.json')
//     const data = await response.json() // JSON 데이터 가져오기
//     lectureDatas.value = data
//     // console.log(lectureDatas.value[0].siteName)
// }

const selectLecture = (lectureData) => {
    selectedLectures.value = lectureData.lecture.subLectures
    selectedCourseName.value = lectureData.courseName // 선택된 강의 ID 저장

    // console.log('선택된 강의:', selectedLectures.value)
    // console.log('선택된 강의 name:', selectedLectureName.value)
}

const selectLectureIndex = (index) => {
    console.log('인덱스', index)
    selectedLectureIndex.value = index // 클릭한 강의 인덱스 저장
    console.log('선택된 강의 인덱스:', selectedLectureIndex.value)
}

// 드롭다운 열기/닫기
const toggleCalendarDropdown = () => {
    isCalendarDropdownOpen.value = !isCalendarDropdownOpen.value
}

// 드롭다운 닫기
const closeCalendarDropdown = () => {
    isCalendarDropdownOpen.value = false
}

// 선택한 날짜 업데이트
const selectDate = (date) => {
    selectedDate.value = date
    closeCalendarDropdown()
}

onMounted(() => {
    todoStore.getInprogressLecture() // 컴포넌트가 로드될 때 JSON 데이터 가져오기
})
</script>

<style></style>
