<template>
    <div class="flex w-[20.5rem] h-20 border border border-black rounded-lg overflow-hidden">
        <!-- Thumbnail Container -->
        <div class="w-[7.5rem] h-full bg-gray-300">Thunbnail container</div>

        <!-- Info Container -->
        <div class="flex flex-col items-center justify-center w-full h-full gap-2 px-3">
            <!-- Title Section -->
            <div class="flex w-full gap-x-0.5">
                <div class="w-full">
                    <p class="text-gray-400 text-caption-sm">인프런</p>
                    <p class="text-black text-body">인프런</p>
                </div>
                <div class="flex items-center">
                    <BookmarkFilled class="w-6 h-6 text-primary-500" />
                </div>
            </div>
            <!-- Tag Section -->
            <div class="inline-flex w-full gap-1.5">
                <span class="inline-flex gap-1 px-2 py-1 tag-gray">
                    <p>#</p>
                    <p>태그1</p>
                </span>
                <span class="inline-flex gap-1 px-2 py-1 tag-gray">
                    <p>#</p>
                    <p>태그2</p>
                </span>
            </div>
        </div>
    </div>
</template>

<script setup>
import BookmarkFilled from '@/assets/icons/bookmark_filled.svg'
</script>

<style></style>
