<template>
    <div
        class="bg-white flex w-[20.5rem] h-[6rem] border border-gray-200 rounded-lg overflow-hidden"
        v-if="lecture"
    >
        <!-- Thumbnail Container -->
        <div class="w-[7.5rem] h-full bg-gray-300 flex-shrink-0 relative">
            <img :src="lecture.imageUrl" alt="강의 썸네일" class="w-full h-full" />
            <Move class="absolute w-6 h-6 text-white top-[33.6px]" />
        </div>

        <!-- Info Container -->
        <div class="flex flex-col w-full h-full gap-2 px-3 py-2">
            <!-- Title Section -->
            <div class="flex items-center justify-between w-full h-full gap-x-0.5">
                <div class="flex flex-col justify-center w-full h-full">
                    <p class="text-gray-400 text-caption-sm">{{ lecture.siteName }}</p>
                    <p
                        class="text-black cursor-pointer text-overflow text-body"
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
            <div class="flex gap-1.5 w-full">
                <div
                    class="inline-flex gap-1 text-caption-sm tag-gray max-w-[60px]"
                    v-for="tag in lecture.tags"
                    :key="tag"
                >
                    <p>#</p>
                    <p
                        class="overflow-hidden cursor-pointer text-ellipsis whitespace-nowrap"
                        :title="tag"
                    >
                        {{ tag }}
                    </p>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import BookmarkFilled from '@/assets/icons/bookmark_filled.svg'
import Move from '@/assets/icons/move.svg'
import { defineProps } from 'vue'

defineProps({
    lecture: {
        type: Object,
        required: true,
    },
})

// const lecture = computed(() => props.lecture)
// console.log('디버깅', lecture.value)
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
