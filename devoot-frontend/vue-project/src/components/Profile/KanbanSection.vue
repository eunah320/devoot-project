<template>
    <div class="flex justify-between w-[1192px] h-fit p-6 bg-green-200">
        <div class="flex flex-col w-[352px] h-full bg-yellow-100 gap-[12px]">
            <div class="inline-flex w-fit text-caption tag-gray">
                <p>수강 전</p>
            </div>

            <div class="overflow-hidden grow">
                <div
                    class="w-full h-[374px] bg-white p-3 rounded-2xl overflow-y-auto overflow-x-hidden"
                >
                    <KanbanCard />
                </div>
            </div>
        </div>
        <div class="flex flex-col w-[352px] h-full bg-blue-100 gap-[12px]">
            <div class="inline-flex w-fit text-caption tag-gray">
                <p>수강중</p>
            </div>
            <div class="w-full h-[374px] bg-white rounded-2xl">칸반</div>
        </div>
        <div class="flex flex-col w-[352px] h-full bg-red-100 gap-[12px]">
            <div class="inline-flex w-fit text-caption tag-gray">
                <p>수강 완료</p>
            </div>
            <div class="w-full h-[374px] bg-white rounded-2xl">칸반</div>
        </div>
    </div>
</template>

<script setup>
import KanbanCard from './KanbanCard.vue'
import { ref, onMounted } from 'vue'

const lectureDatas = ref([])

const loadLectureDatas = async () => {
    const response = await fetch('/kanbancard_dummy_data.json')
    const data = await response.json()
    lectureDatas.value = data
    console.log('강의 데이터', lectureDatas.value)
}

onMounted(() => {
    loadLectureDatas() // 컴포넌트가 로드될 때 JSON 데이터 가져오기
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
