<template>
    <div class="col-span-4 overflow-hidden lg:col-span-3 h-80 rounded-2xl">
        <!-- 강의 썸네일 -->
        <div class="w-full bg-gray-200 h-1/2">
            <img
                :src="lecture.imageUrl"
                alt="Lecture Thumbnail"
                class="object-cover w-full h-full"
            />
        </div>

        <!-- 강의 정보 -->
        <div class="flex flex-col gap-2 px-4 py-3 bg-white">
            <!-- 강의 플랫폼 및 강사명 -->
            <div class="flex items-center justify-between text-caption">
                <div class="flex flex-row gap-1">
                    <p class="text-black">{{ lecture.sourceName }}</p>
                    <LinkExternal class="w-3 h-3 text-gray-300" />
                </div>
                <span>{{ lecture.lecturer }}</span>
            </div>

            <!-- 강의 제목 -->
            <h3 class="mb-1 font-bold text-black text-body-bold line-clamp-2">
                {{ lecture.name }}
            </h3>

            <!-- 별점 -->
            <div class="flex flex-row items-center gap-1">
                <Star class="w-4 h-4 text-yellow-300" />
                <span class="text-black text-caption">{{ lecture.rating }}</span>
            </div>

            <!-- 가격 정보 -->
            <div class="text-right"></div>

            <!-- 태그 리스트 -->
            <div class="flex gap-[4px] mt-1">
                <span
                    v-for="(tag, index) in limitedTags"
                    :key="index"
                    style="width: max-content"
                    class="truncate flex items-center justify-start px-2 h-[22px] bg-gray-100 rounded-[1.25rem] text-caption text-gray-300"
                >
                    #{{ tag }}
                </span>

                <div class="flex flex-row items-center gap-1">
                    <div v-for="(tag, index) in tagList" :key="index" class="tag-gray">
                        # {{ tag }}
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import Star from '@/assets/icons/star_filled.svg'
import LinkExternal from '@/assets/icons/link_external.svg'

const lecture = {
    category: '',
    tags: 'html, python',
    name: '맥킨지 컨설턴트 출신 전문가가 알려주는 퍼펙트 프레젠테이션 특강',
    lecturer: '김재성캡틴',
    currentPrice: 132000,
    originPrice: 132000,
    sourceName: 'goorm',
    imageUrl:
        'https://grm-project-template-bucket.s3.ap-northeast-2.amazonaws.com/lecture/lec_DPPNX_1732343668789/coverImage.png?_=1734933253654',
    rating: null,
}
</script>

<style scoped></style>
