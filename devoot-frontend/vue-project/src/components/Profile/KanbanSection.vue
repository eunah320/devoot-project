<template>
    <div class="flex justify-between w-[1192px] h-fit p-6">
        <div class="flex flex-col w-[352px] h-full gap-[12px]">
            <div class="inline-flex w-fit text-caption tag-gray">
                <p>수강 전</p>
            </div>

            <div class="overflow-hidden grow">
                <div
                    class="flex-col justify-start flex-wrap space-y-2.5 w-full h-[374px] bg-gray-100 p-3 rounded-2xl overflow-y-auto overflow-x-hidden container"
                    droppable="true"
                >
                    <KanbanCard
                        draggable="true"
                        :lecture="lecture"
                        v-for="lecture in lectureDatas"
                        :key="lecture.id"
                        class="draggable cursor-grab"
                    />
                </div>
            </div>
        </div>
        <div class="flex flex-col w-[352px] h-full gap-[12px]">
            <div class="inline-flex w-fit text-caption tag-gray">
                <p style="color: #fde03a !important">수강중</p>
            </div>

            <div class="overflow-hidden grow">
                <div
                    class="flex-col justify-start flex-wrap space-y-2.5 w-full h-[374px] bg-gray-100 p-3 rounded-2xl overflow-y-auto overflow-x-hidden container"
                    droppable="true"
                >
                    <KanbanCard
                        draggable="true"
                        :lecture="lecture"
                        v-for="lecture in lectureDatas"
                        :key="lecture.id"
                        class="draggable cursor-grab"
                    />
                </div>
            </div>
        </div>
        <div class="flex flex-col w-[352px] h-full gap-[12px]">
            <div class="inline-flex w-fit text-caption tag-gray">
                <p style="color: #0edb8c !important">수강 완료</p>
            </div>

            <div class="overflow-hidden grow">
                <div
                    class="flex-col justify-start flex-wrap space-y-2.5 w-full h-[374px] bg-gray-100 p-3 rounded-2xl overflow-y-auto overflow-x-hidden container"
                    droppable="true"
                >
                    <KanbanCard
                        draggable="true"
                        :lecture="lecture"
                        v-for="lecture in lectureDatas"
                        :key="lecture.id"
                        class="draggable cursor-grab"
                    />
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import KanbanCard from './KanbanCard.vue'
import { ref, onMounted, onUpdated } from 'vue'

const lectureDatas = ref([])

const loadLectureDatas = async () => {
    const response = await fetch('/kanbancard_dummy_data.json')
    const data = await response.json()
    lectureDatas.value = data
    // console.log('강의 데이터', lectureDatas.value)
}

onMounted(() => {
    loadLectureDatas() // JSON 데이터 가져오기
})

onUpdated(() => {
    const $ = (select) => document.querySelectorAll(select)
    const draggables = $('.draggable')
    const containers = $('.container')

    // console.log('드래그 가능한 엘리먼트', draggables)
    // console.log('컨테이너', containers)

    // 드래그 가능한 엘리먼트에 이벤트(드래그 시작, 드래그 종료) 추가
    draggables.forEach((el) => {
        el.addEventListener('dragstart', () => {
            el.classList.add('dragging', 'highlight', 'cursor-grabbing')
            console.log('드래그 시작')
        })

        el.addEventListener('dragend', () => {
            el.classList.remove('dragging', 'highlight')
            console.log('드래그 종료')
        })
    })

    // 현재 마우스 위치에서 가장 가까운 요소 찾기
    function getDragAfterElement(container, y) {
        // 1️⃣ `container` 내부의 모든 `.draggable` 요소를 가져오되,
        //    현재 드래그 중인(`.dragging`) 요소는 제외함.
        const draggableElements = [...container.querySelectorAll('.draggable:not(.dragging)')]

        // 2️⃣ `reduce`를 사용하여 `y` 위치에서 가장 가까운 요소 찾기
        return draggableElements.reduce(
            (closest, child) => {
                // closest() 메서드는 주어진 CSS 선택자와 일치하는 요소를 찾을 때까지, 자기 자신을 포함해 위쪽으로 문서 트리를 순회
                // 3️⃣ `child`(각 draggable 요소)의 위치와 크기 정보 가져오기
                const box = child.getBoundingClientRect()

                // 4️⃣ `offset` 계산 (마우스 y 좌표와 요소 중심 간의 거리)
                // `box.top`은 요소의 상단 위치
                // `box.height / 2`는 요소의 절반 높이 (즉, 중앙 지점)
                const offset = y - box.top - box.height / 2

                // 5️⃣ 마우스가 요소의 중앙 위쪽에 위치(`offset < 0`)하면서,
                //    현재까지 찾은 `closest.offset`보다 더 가까운 경우 `closest` 업데이트
                if (offset < 0 && offset > closest.offset) {
                    return { offset: offset, element: child }
                } else {
                    return closest
                }
            },
            // 6️⃣ `reduce`의 초기값 설정 (가장 작은 숫자, 즉 `Number.NEGATIVE_INFINITY`)
            //    이렇게 하면 첫 번째 비교에서 무조건 첫 번째 요소가 선택될 수 있도록 함.
            { offset: Number.NEGATIVE_INFINITY }
        ).element // 7️⃣ `.element`를 반환하여 가장 가까운 요소를 리턴
    }

    // 드래그중일 때
    containers.forEach((container) => {
        container.addEventListener('dragover', (e) => {
            e.preventDefault()
            const afterElement = getDragAfterElement(container, e.clientY) //clientY: 마우스 이벤트가 발생한 위치의 Y(수직) 좌표
            const draggable = document.querySelector('.dragging')
            if (afterElement) {
                container.insertBefore(draggable, afterElement)
            } else {
                container.appendChild(draggable) // 만약 `afterElement`가 없으면 마지막으로 보냄
            }
        })
    })
})
</script>

<style>
::-webkit-scrollbar {
    width: 6px; /* 세로축 스크롤바 폭 너비 */
}

::-webkit-scrollbar-thumb {
    background: #e8ebee; /* 스크롤바 막대 색상 */
    border: 1px solid #f4f6f8; /* 스크롤바 막대 테두리 설정  */
    border-radius: 12px 12px 12px 12px;
}

.dragging {
    opacity: 0.5; /* 반투명 효과 */
    transform: scale(1.05); /* 살짝 확대 */
    transition: transform 0.2s ease-in-out;
}
.highlight {
    /* border: 2px dashed #ff9800; */
    background-color: #cde3ff;
}
</style>
