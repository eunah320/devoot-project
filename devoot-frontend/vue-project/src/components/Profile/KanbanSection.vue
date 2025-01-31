<template>
    <div class="flex justify-between w-[1192px] h-fit p-6 bg-green-200">
        <div class="flex flex-col w-[352px] h-full bg-yellow-100 gap-[12px]">
            <div class="inline-flex w-fit text-caption tag-gray">
                <p>수강 전</p>
            </div>

            <div class="overflow-hidden grow">
                <div
                    class="flex-col justify-start flex-wrap space-y-2.5 w-full h-[374px] bg-white p-3 rounded-2xl overflow-y-auto overflow-x-hidden container"
                    droppable="true"
                >
                    <KanbanCard
                        draggable="true"
                        :lecture="lecture"
                        v-for="lecture in lectureDatas"
                        :key="lecture.id"
                        class="draggable"
                    />
                </div>
            </div>
        </div>
        <div class="flex flex-col w-[352px] h-full bg-yellow-100 gap-[12px]">
            <div class="inline-flex w-fit text-caption tag-gray">
                <p>수강중</p>
            </div>

            <div class="overflow-hidden grow">
                <div
                    class="flex-col justify-start flex-wrap space-y-2.5 w-full h-[374px] bg-white p-3 rounded-2xl overflow-y-auto overflow-x-hidden container"
                    droppable="true"
                >
                    <KanbanCard
                        draggable="true"
                        :lecture="lecture"
                        v-for="lecture in lectureDatas"
                        :key="lecture.id"
                        class="draggable"
                    />
                </div>
            </div>
        </div>
        <div class="flex flex-col w-[352px] h-full bg-yellow-100 gap-[12px]">
            <div class="inline-flex w-fit text-caption tag-gray">
                <p>수강 완료</p>
            </div>

            <div class="overflow-hidden grow">
                <div
                    class="flex-col justify-start flex-wrap space-y-2.5 w-full h-[374px] bg-white p-3 rounded-2xl overflow-y-auto overflow-x-hidden container"
                    droppable="true"
                >
                    <KanbanCard
                        draggable="true"
                        :lecture="lecture"
                        v-for="lecture in lectureDatas"
                        :key="lecture.id"
                        class="draggable"
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
    console.log('onUpdated 실행됨') // 업데이트 발생 확인

    const $ = (select) => document.querySelectorAll(select)
    const draggables = $('.draggable')
    const containers = $('.container')

    console.log('드래그 가능한 엘리먼트', draggables)
    console.log('컨테이너', containers)

    // 드래그 가능한 엘리먼트에 이벤트(드래그 시작, 드래그 종료) 추가
    draggables.forEach((el) => {
        el.setAttribute('draggable', 'true') // draggable 속성 강제 추가
        el.addEventListener('dragstart', () => {
            el.classList.add('dragging')
            console.log('드래그 시작')
        })

        el.addEventListener('dragend', () => {
            el.classList.remove('dragging')
            console.log('드래그 종료')
        })
    })

    // 현재 마우스 위치에서 가장 가까운 요소 찾기
    function getDragAfterElement(container, y) {
        const draggableElements = [...container.querySelectorAll('.draggable:not(.dragging)')]

        return draggableElements.reduce(
            (closest, child) => {
                const box = child.getBoundingClientRect()
                const offset = y - box.top - box.height / 2
                if (offset < 0 && offset > closest.offset) {
                    return { offset: offset, element: child }
                } else {
                    return closest
                }
            },
            { offset: Number.NEGATIVE_INFINITY }
        ).element
    }

    containers.forEach((container) => {
        container.addEventListener('dragover', (e) => {
            e.preventDefault()
            const afterElement = getDragAfterElement(container, e.clientY)
            const draggable = document.querySelector('.dragging')
            if (afterElement) {
                container.insertBefore(draggable, afterElement)
            } else {
                container.appendChild(draggable) // 만약 `afterElement`가 없으면 마지막으로 보냄
            }
        })
    })
})

onUpdated(() => {
    console.log('onUpdated 실행됨') // 업데이트 발생 확인

    const $ = (select) => document.querySelectorAll(select)
    const draggables = $('.draggable')
    const containers = $('.container')

    console.log('드래그 가능한 엘리먼트', draggables)
    console.log('컨테이너', containers)

    // 드래그 가능한 엘리먼트에 이벤트(드래그 시작, 드래그 종료) 추가
    draggables.forEach((el) => {
        el.setAttribute('draggable', 'true') // draggable 속성 강제 추가
        el.addEventListener('dragstart', () => {
            el.classList.add('dragging')
            console.log('드래그 시작')
        })

        el.addEventListener('dragend', () => {
            el.classList.remove('dragging')
            console.log('드래그 종료')
        })
    })

    // 현재 마우스 위치에서 가장 가까운 요소 찾기
    function getDragAfterElement(container, y) {
        const draggableElements = [...container.querySelectorAll('.draggable:not(.dragging)')]

        return draggableElements.reduce(
            (closest, child) => {
                const box = child.getBoundingClientRect()
                const offset = y - box.top - box.height / 2
                if (offset < 0 && offset > closest.offset) {
                    return { offset: offset, element: child }
                } else {
                    return closest
                }
            },
            { offset: Number.NEGATIVE_INFINITY }
        ).element
    }

    containers.forEach((container) => {
        container.addEventListener('dragover', (e) => {
            e.preventDefault()
            const afterElement = getDragAfterElement(container, e.clientY)
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
</style>
