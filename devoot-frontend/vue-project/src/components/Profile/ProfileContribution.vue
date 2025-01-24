<template>
    <div class="border border-gray-200 rounded-[1rem] p-[1.5rem] inline-flex flex-col gap-6">
        <!-- 상단 영역 -->
        <div class="flex items-center justify-between w-full">
            <!-- 왼쪽 텍스트 -->
            <p class="text-h3">내 발자국</p>
            <!-- 오른쪽 년도 선택 -->
            <div class="flex items-center gap-2">
                <img
                    src="@/assets/icons/navigate_left.svg"
                    alt="Navigate Left"
                    class="w-[1.125rem] h-[1.125rem]"
                />
                <span class="text-body">2025</span>
                <img
                    src="@/assets/icons/navigate_Right.svg"
                    alt="Navigate Right"
                    class="w-[1.125rem] h-[1.125rem]"
                />
            </div>
        </div>

        <!-- 하단 잔디 영역 -->
        <div class="flex justify-between w-full">
            <div class="inline-flex flex-col justify-between pt-4 text-gray-300 text-caption-sm">
                <span>mon</span>
                <span>thu</span>
                <span>sun</span>
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
                            class="p-0"
                            v-for="(column, columnIndex) in calendarData"
                            :key="columnIndex"
                        >
                            <div
                                v-for="(day, rowIndex) in column"
                                :key="rowIndex"
                                :data-date="day.date"
                                :data-level="day.level"
                                :class="'ContributionCalendar-day'"
                                tabindex="0"
                                role="gridcell"
                                :style="{
                                    backgroundColor: day.empty ? 'red' : getColor(day.level),
                                }"
                            ></div>
                        </td>
                    </tr>
                </table>
            </div>
        </div>
    </div>
</template>

<script>
import { ref, computed, watch, onMounted } from 'vue'

export default {
    setup() {
        // 컴포넌트가 초기화될 때 실행되며, 반응형 데이터와 함수를 정의

        // 1. 데이터와 캐싱 상태 관리
        const contributions = ref([]) // 데이터 저장
        const isDataLoaded = ref(false) // 데이터가 로드되었는지 여부

        // 2. JSON 데이터 로드 함수
        const loadContributions = async () => {
            // 이미 데이터가 로드되었으면 요청을 건너뜀
            if (isDataLoaded.value) return

            try {
                const response = await fetch('/contributions_dummy_data.json')
                const data = await response.json()

                // 데이터와 로드 상태 업데이트
                contributions.value = data.map((day) => ({
                    ...day,
                    level: getLevel(day.contributions),
                }))
                isDataLoaded.value = true // 데이터 로드 완료 상태로 변경
            } catch (error) {
                console.error('Error loading contributions data:', error)
            }
        }

        // 3. 기여도 수준(level) 계산 함수
        const getLevel = (contributions) => {
            if (contributions === 0) return 0
            if (contributions <= 2) return 1
            if (contributions <= 4) return 2
            if (contributions <= 5) return 3
            if (contributions <= 6) return 4
            return 5
        }

        // 4. 색상 반환 함수
        const getColor = (level) => {
            switch (level) {
                case 0: // 없음
                    // level이 0과 같을 때 실행할 코드
                    return '#FFFFFF'
                case 1:
                    return '#CDE3FF' // 낮음
                case 2:
                    return '#9AC7FF'
                case 3:
                    return '#68AAFF'
                case 4:
                    return '#358EFF'
                case 5:
                    return '#0372FF' // 매우 높음
            }
        }

        // 5. 주별(53주 x 7일) 데이터를 계산하는 `computed`
        const calendarData = computed(() => {
            const columns = [] // 주별 데이터를 저장할 배열

            contributions.value.forEach((day) => {
                const date = new Date(day.date) // 날짜를 Date 객체로 변환(Mon Jan 01 2024 00:00:00 GMT+0000 (UTC))
                const dayOfWeek = date.getDay() // 요일 계산 (0: 일요일, ..., 6: 토요일)
                const weekIndex = Math.floor(
                    (date - new Date(`${date.getFullYear()}-01-01`)) / (7 * 24 * 60 * 60 * 1000)
                ) // 현재 연도의 1월 1일 Date 객체로 생성 후 현재 날짜와의 차이를 계산하여 주(week)로 변환
                // floor: 소수점 이하를 버림

                // 해당 주(열)가 없으면 초기화
                if (!columns[weekIndex]) {
                    columns[weekIndex] = Array.from({ length: 7 }, () => ({ empty: true }))
                }

                // 현재 요일(dayOfWeek)에 데이터를 채움
                columns[weekIndex][dayOfWeek] = { ...day, empty: false }
                // 결과: { date: "2024-01-01", contributions: 3, empty: false }
            })

            return columns // 53주 x 7일 데이터 반환
        })

        // 데이터 변경 시 특정 날짜 업데이트
        watch(contributions, (newContributions, oldContributions) => {
            console.log(newContributions)
            const updatedColumns = [...calendarData.value] // 기존 데이터 복사

            newContributions.forEach((day) => {
                const date = new Date(day.date)
                const dayOfWeek = date.getDay()
                const weekIndex = Math.floor(
                    (date - new Date(`${date.getFullYear()}-01-01`)) / (7 * 24 * 60 * 60 * 1000)
                )

                if (!updatedColumns[weekIndex]) {
                    updatedColumns[weekIndex] = Array.from({ length: 7 }, () => ({ empty: true }))
                }

                // 기존 데이터 업데이트
                updatedColumns[weekIndex][dayOfWeek] = { ...day, empty: false }
            })

            // 업데이트된 데이터를 `calendarData`에 반영
            contributions.value = newContributions
        })

        // 6. `onMounted`로 데이터 로드
        onMounted(() => {
            loadContributions() // 컴포넌트가 로드될 때 JSON 데이터 가져오기
        })

        // 7. 반환값
        return {
            contributions,
            calendarData,
            getColor,
        }
    },
}
</script>

<style>
.ContributionCalendar-day {
    display: block; /* 세로로 쌓기 위해 block 사용 */
    border-radius: 50%; /* 둥근 모서리를 50%로 설정해 원형으로 만듦 */
    width: 1.25rem; /* 가로 크기 */
    height: 1.25rem; /* 세로 크기 */
}
</style>
