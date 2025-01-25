<template>
    <div class="border border-gray-200 rounded-[1rem] p-[1.5rem] inline-flex flex-col gap-6">
        <!-- 상단 영역 -->
        <div class="flex items-center justify-between w-full">
            <!-- 왼쪽 텍스트 -->
            <p class="text-h3">내 발자국</p>
            <!-- 오른쪽 년도 선택 -->
            <div class="flex items-center gap-2 text-black">
                <NavigateLeft
                    class="w-[1.125rem] h-[1.125rem] cursor-pointer"
                    @click="navigateYear(-1)"
                />
                <span class="text-body">{{ year }}</span>
                <NavigateRight
                    class="w-[1.125rem] h-[1.125rem] cursor-pointer"
                    @click="navigateYear(1)"
                />
            </div>
        </div>

        <!-- 하단 잔디 영역 -->
        <div class="flex justify-between w-full">
            <div class="inline-flex flex-col justify-between pt-4 text-gray-300 text-caption-sm">
                <span>sun</span>
                <span>wed</span>
                <span>sat</span>
            </div>
            <div class="flex flex-col gap-2">
                <div class="text-gray-300 text-caption-sm">
                    <p class="flex justify-between text-center">
                        <span class="w-5">jan</span>
                        <span class="w-5">feb</span>
                        <span class="w-5">mar</span>
                        <span class="w-5">apr</span>
                        <span class="w-5">may</span>
                        <span class="w-5">jun</span>
                        <span class="w-5">jul</span>
                        <span class="w-5">aug</span>
                        <span class="w-5">sep</span>
                        <span class="w-5">oct</span>
                        <span class="w-5">nov</span>
                        <span class="w-5">dec</span>
                        <span class="w-5"></span>
                    </p>
                </div>

                <table class="inline-flex">
                    <tr>
                        <td
                            v-for="(column, columnIndex) in calendarData"
                            :key="columnIndex"
                            class="p-0"
                        >
                            <div
                                v-for="(day, rowIndex) in column"
                                :key="rowIndex"
                                class="contribution-wrapper"
                            >
                                <!-- 모든 데이터를 렌더링 -->
                                <div
                                    :data-date="day.date"
                                    :data-level="day.level"
                                    :class="{
                                        'ContributionCalendar-day': !day.empty,
                                        'empty-cell': day.empty,
                                    }"
                                    tabindex="0"
                                    role="gridcell"
                                >
                                    <!-- 데이터가 있는 경우 FootPrint 렌더링 -->
                                    <div class="relative group">
                                        <FootPrint
                                            v-if="day.date"
                                            class="w-3.5 h-3.5 rotate-45"
                                            :class="[
                                                {
                                                    'text-white': day.level === 0,
                                                    'text-primary-100': day.level === 1,
                                                    'text-primary-200': day.level === 2,
                                                    'text-primary-300': day.level === 3,
                                                    'text-primary-400': day.level === 4,
                                                    'text-primary-500': day.level === 5,
                                                },
                                            ]"
                                        />
                                        <div
                                            v-if="day.date"
                                            id="tooltip-default"
                                            role="tooltip"
                                            class="flex whitespace-nowrap flex-col items-center absolute left-1/2 bottom-full translate-x-[-50%] translate-y-[-10px] z-50 invisible group-hover:visible opacity-0 group-hover:opacity-100 transition-opacity duration-300 bg-gray-300 text-black text-caption-sm rounded-lg px-2 py-2 shadow-lg w-auto text-center space-y-1"
                                        >
                                            <span>{{ day.date }}</span>
                                            <span>완료: {{ day.contributions }}개</span>
                                            <div class="tooltip-arrow"></div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </td>
                    </tr>
                </table>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import NavigateLeft from '@/assets/icons/navigate_left.svg'
import NavigateRight from '@/assets/icons/navigate_right.svg'
import FootPrint from '@/assets/icons/footprint.svg'

// 0. 상태 변수 정의
const year = ref(new Date().getFullYear()) // 현재 연도를 저장하는 반응형 변수
console.log('현재년도', year.value)
const navigateYear = (offset) => {
    year.value += offset
    isDataLoaded.value = false // 데이터 로드 상태 초기화
    loadContributions() // 새로운 연도의 데이터를 로드
}

// 1. 데이터와 상태를 정의
const contributions = ref([]) // 기여도 데이터를 저장하는 반응형 변수
const isDataLoaded = ref(false) // 데이터가 로드되었는지 여부를 나타내는 반응형 변수

// 2. JSON 데이터를 로드하는 함수
const loadContributions = async () => {
    // 이미 데이터가 로드된 경우, 추가 요청을 건너뜀
    if (isDataLoaded.value) return

    try {
        // 서버로부터 기여도 데이터를 비동기로 가져옴
        const response = await fetch('/contributions_dummy_data.json')
        const data = await response.json()
        // console.log('데이터', data) // 가져온 데이터를 콘솔에 출력
        // console.log('ref 년도', year.value) // ref로 저장된 년도를 콘솔에 출력

        // 가져온 데이터를 contributions에 저장
        // 각 데이터에 `level` 정보를 계산하여 추가
        const filteredDates = data.filter((day) => {
            const date = new Date(day.date) // 문자열을 Date 객체로 변환
            // console.log('date', date)
            return date.getFullYear() == year.value // 선택한 연도와 비교
        })
        console.log('필터링', filteredDates)

        contributions.value = filteredDates.map((day) => ({
            ...day, // 기존 day 객체의 모든 속성을 복사
            level: getLevel(day.contributions), // 기여도 수준(level) 계산 후 추가
        }))
        isDataLoaded.value = true // 데이터 로드 상태를 true로 변경
    } catch (error) {
        console.error('Error loading contributions data:', error) // 오류 발생 시 콘솔에 출력
    }
}

// 3. 기여도 수준(level)을 계산하는 함수
const getLevel = (contributions) => {
    // 기여도에 따라 레벨을 계산하여 반환
    if (contributions === 0) return 0 // 기여도가 없으면 레벨 0
    if (contributions <= 2) return 1 // 기여도가 1~2이면 레벨 1
    if (contributions <= 4) return 2 // 기여도가 3~4이면 레벨 2
    if (contributions <= 5) return 3 // 기여도가 5이면 레벨 3
    if (contributions <= 6) return 4 // 기여도가 6이면 레벨 4
    return 5 // 기여도가 7 이상이면 레벨 5
}

// 4. 특정 날짜의 주차(week index)를 계산하는 함수
const getWeekIndex = (date) => {
    const start = new Date(date.getFullYear(), 0, 1) // 해당 연도의 1월 1일
    const dayOffset = start.getDay() // 1월 1일의 요일 (일요일: 0, 월요일: 1, ...)
    const daysSinceStart = Math.floor((date - start) / (24 * 60 * 60 * 1000)) // 1월 1일부터 해당 날짜까지 경과한 일 수
    return Math.floor((daysSinceStart + dayOffset) / 7) // 전체 경과 일수와 요일을 기반으로 주차 계산
}

// 5. 캘린더 데이터를 계산하는 computed 속성
const calendarData = computed(() => {
    // 캘린더의 기본 구조 생성 (53주 x 7일)
    const columns = Array.from(
        { length: 53 },
        () => Array.from({ length: 7 }, () => ({ empty: false })) // 초기값은 모두 빈 칸으로 설정
    )

    // 1월 1일 및 12월 31일 계산
    const firstDate = new Date(`${year.value}-01-01`) // 선택된 연도의 1월 1일
    const lastDate = new Date(`${year.value}-12-31`) // 선택된 연도의 12월 31일
    const firstDayOfWeek = firstDate.getDay() // 1월 1일의 요일

    // 0주차 데이터 처리 (캘린더의 첫 번째 주)
    for (let i = 0; i < 7; i++) {
        if (i < firstDayOfWeek) {
            // 1월 1일 이전의 날짜는 빈 칸으로 유지
            columns[0][i] = { empty: true }
        } else {
            // 1월 1일부터의 날짜 데이터를 생성
            const dayOffset = i - firstDayOfWeek // 요일에 따른 날짜 차이 계산
            const date = new Date(firstDate) // 1월 1일의 복사본 생성
            date.setDate(firstDate.getDate() + dayOffset) // 날짜를 조정
        }
    }

    // 1월 1일부터 12월 31일까지 모든 날짜를 `empty: false`로 설정
    let currentDate = new Date(firstDate) // 현재 날짜를 1월 1일부터 시작
    while (currentDate <= lastDate) {
        const dayOfWeek = currentDate.getDay()
        const weekIndex = getWeekIndex(currentDate)

        if (!columns[weekIndex]) {
            columns[weekIndex] = Array.from({ length: 7 }, () => ({ empty: true }))
        }

        // 다음 날짜로 이동
        currentDate.setDate(currentDate.getDate() + 1)
    }

    // contributions 데이터 반영
    contributions.value.forEach((day) => {
        const date = new Date(day.date) // 기여도 데이터의 날짜
        const dayOfWeek = date.getDay() // 해당 날짜의 요일
        const weekIndex = getWeekIndex(date) // 해당 날짜의 주차 계산

        // 0주차는 이미 처리되었으므로, 이후 주차에 데이터를 추가
        const columnIndex = weekIndex === 0 ? 0 : weekIndex
        if (!columns[columnIndex]) {
            columns[columnIndex] = Array.from({ length: 7 }, () => ({ empty: true }))
        }
        columns[columnIndex][dayOfWeek] = { ...day, empty: false } // 해당 날짜 데이터를 추가
    })

    // 12월 31일 이후를 빈 칸으로 설정
    const lastWeekIndex = getWeekIndex(lastDate)
    const lastDayOfWeek = lastDate.getDay()
    for (let i = lastDayOfWeek + 1; i < 7; i++) {
        columns[lastWeekIndex][i] = { empty: true }
    }

    console.log(columns) // 계산된 캘린더 데이터를 콘솔에 출력
    return columns // 최종적으로 계산된 캘린더 데이터를 반환
})

// 데이터 변경 시 특정 날짜 업데이트
watch(contributions, (newContributions) => {
    console.log(newContributions)

    const updatedColumns = [...calendarData.value] // 기존 데이터 복사

    newContributions.forEach((day) => {
        const date = new Date(day.date)
        const dayOfWeek = date.getDay() // 요일
        const weekIndex = getWeekIndex(date) // 주차 계산

        // 0주차는 이미 처리되었으므로, 열 인덱스를 조정
        const columnIndex = weekIndex === 0 ? 0 : weekIndex

        if (!updatedColumns[columnIndex]) {
            updatedColumns[columnIndex] = Array.from({ length: 7 }, () => ({ empty: true }))
        }

        // 기존 데이터 업데이트
        updatedColumns[columnIndex][dayOfWeek] = { ...day, empty: false }
    })

    // 업데이트된 데이터를 `calendarData`에 반영
    calendarData.value = updatedColumns
})

// 6. `onMounted`로 데이터 로드
onMounted(() => {
    loadContributions() // 컴포넌트가 로드될 때 JSON 데이터 가져오기
})
</script>

<style>
.ContributionCalendar-day {
    @apply flex justify-center items-center rounded-full border border-[#f4f6f8] w-5 h-5;
}

.empty-cell {
    @apply bg-transparent border-none w-5 h-5;
}

.tooltip-arrow {
    @apply absolute left-1/2 top-full translate-x-[-50%] translate-y-[-4px] w-0 h-0 border-t-[4px] border-l-[6px] border-r-[6px] border-t-gray-300 border-l-transparent border-r-transparent;
}
</style>
