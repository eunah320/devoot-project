<template>
    <div
        class="bg-white flex w-[20.5rem] h-[6rem] border border-gray-200 rounded-lg overflow-hidden"
        v-for="lecture in lectureDatas"
        :key="lecture.id"
    >
        <!-- Thumbnail Container -->
        <div class="w-[7.5rem] h-full bg-gray-300 flex-shrink-0 relative">
            <img :src="lecture.imageUrl" alt="" class="w-full h-full" />
            <Move class="absolute w-6 h-6 text-white top-[33.6px] cursor-move" />
        </div>

        <!-- Info Container -->
        <div class="flex flex-col w-full h-full gap-2 px-3 py-2">
            <!-- Title Section -->
            <div class="flex items-center justify-between w-full h-full gap-x-0.5">
                <div class="flex flex-col justify-center w-full h-full">
                    <p class="text-gray-400 text-caption-sm">{{ lecture.siteName }}</p>
                    <p
                        class="text-black text-overflow text-body cursor-text"
                        :title="lecture.courseName"
                    >
                        {{ lecture.courseName }}
                    </p>
                </div>
                <div class="flex">
                    <BookmarkFilled class="w-6 h-6 text-primary-500" />
                </div>
            </div>
            <!-- Tag Section -->
            <div class="flex flex-wrap gap-1.5">
                <div
                    class="inline-flex gap-1 text-caption-sm tag-gray"
                    v-for="tag in lecture.tags"
                    :key="tag"
                >
                    <p>#</p>
                    <p>{{ tag }}</p>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import BookmarkFilled from '@/assets/icons/bookmark_filled.svg'
import Move from '@/assets/icons/move.svg'
import { ref, onMounted } from 'vue'

const lectureDatas = ref([])

const loadLectureDatas = async () => {
    const response = await fetch('./kanbancard_dummy_data.json')
    const data = await response.json()
    lectureDatas.value = data
    console.log('강의 데이터', lectureDatas.value)
}

onMounted(() => {
    loadLectureDatas() // 컴포넌트가 로드될 때 JSON 데이터 가져오기
})
</script>

<style>
.text-overflow {
    text-overflow: ellipsis;
    overflow: hidden;
    word-break: break-word;

    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
}
</style>
