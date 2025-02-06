<template>
    <!-- 캘린더 섹션 -->
    <section class="relative py-8">
        <div class="w-full px-4 mx-auto max-w-7xl">
            <!-- 상단 네비게이션 바 -->
            <div class="flex items-center gap-3 mb-5">
                <div class="flex items-center gap-4">
                    <!-- 현재 월 표시 -->
                    <h5 class="text-xl font-semibold leading-8 text-gray-900">
                        {{ currentYear }} {{ monthNames[currentMonth] }}
                    </h5>
                    <button @click="prevMonth">
                        <NavigateLeft class="w-[1.125rem] h-[1.125rem]" />
                    </button>
                    <button @click="nextMonth">
                        <NavigateRight class="w-[1.125rem] h-[1.125rem]" />
                    </button>
                </div>
            </div>
            <!-- 캘린더 요일 헤더 -->
            <div class="border-l border-gray-300">
                <div
                    class="grid h-8 max-w-lg grid-cols-7 border-t border-b border-gray-300 divide-gray-300"
                >
                    <div
                        v-for="day in weekDays"
                        :key="day"
                        class="flex flex-col items-center justify-center border-r border-gray-300"
                    >
                        <span class="text-sm font-medium text-gray-500">{{ day }}</span>
                    </div>
                </div>
                <!-- 캘린더 날짜 그리드 -->
                <div class="grid max-w-lg grid-cols-7 divide-gray-300">
                    <!-- 이전 달의 빈칸 (회색 배경 + 회색 테두리) -->
                    <div
                        v-for="empty in firstDayOfMonth"
                        :key="'empty-prev-' + empty"
                        class="p-3.5 bg-gray-200 border-b border-r border-gray-300 aspect-square"
                    ></div>

                    <!-- 현재 월의 날짜 -->
                    <div
                        v-for="date in daysInMonth"
                        :key="date"
                        class="p-3.5 border-b border-r border-gray-300 flex justify-start items-start aspect-square transition-all duration-300 hover:bg-primary-100"
                    >
                        <span
                            class="flex items-center justify-center text-gray-900 rounded-full text-body-bold w-7 h-7"
                        >
                            {{ date }}
                        </span>
                    </div>

                    <!-- 다음 달의 빈칸 (회색 배경 + 회색 테두리) -->
                    <div
                        v-for="empty in nextMonthDays"
                        :key="'empty-next-' + empty"
                        class="p-3.5 border-b border-r bg-gray-200 aspect-square"
                    ></div>
                </div>
            </div>
        </div>
    </section>
</template>

<script setup>
import { ref, computed } from 'vue'
import NavigateLeft from '@/assets/icons/navigate_left.svg'
import NavigateRight from '@/assets/icons/navigate_right.svg'

const currentYear = ref(new Date().getFullYear())
const currentMonth = ref(new Date().getMonth())
const monthNames = [
    'January',
    'February',
    'March',
    'April',
    'May',
    'June',
    'July',
    'August',
    'September',
    'October',
    'November',
    'December',
]
const weekDays = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat']

// 현재 월의 총 일 수
const daysInMonth = computed(() => {
    return new Date(currentYear.value, currentMonth.value + 1, 0).getDate()
})

// 현재 월의 첫 번째 요일 (빈칸 개수)
const firstDayOfMonth = computed(() => {
    return new Date(currentYear.value, currentMonth.value, 1).getDay()
})

// 현재 월의 마지막 요일을 기준으로 빈칸 개수 계산 (7칸을 맞추기 위해)
const nextMonthDays = computed(() => {
    const totalCells = firstDayOfMonth.value + daysInMonth.value
    return totalCells % 7 === 0 ? 0 : 7 - (totalCells % 7)
})

// 이전 달로 이동
const prevMonth = () => {
    if (currentMonth.value === 0) {
        currentMonth.value = 11
        currentYear.value--
    } else {
        currentMonth.value--
    }
}

// 다음 달로 이동
const nextMonth = () => {
    if (currentMonth.value === 11) {
        currentMonth.value = 0
        currentYear.value++
    } else {
        currentMonth.value++
    }
}
</script>
