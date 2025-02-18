<template>
    <div>
        <h1 class="mb-4 text-2xl text-h1">타임라인</h1>
        <div class="flex flex-col gap-4">
            <TimeLineCard
                v-for="(activity, index) in activities"
                :key="index"
                :profileId="activity.profileId"
                :type="activity.type"
                :userName="activity.userName"
                :userImage="activity.userImage"
                :lectureTitle="activity.lectureTitle"
                :lectureId="activity.lectureId"
                :imageUrl="activity.imageUrl"
                :tags="activity.tags"
                :beforeStatus="activity.beforeStatus"
                :afterStatus="activity.afterStatus"
                :footprints="activity.footprints"
                :date="activity.date"
                :sourceName="activity.sourceName"
                :sourceUrl="activity.sourceUrl"
                :isBookmarked="activity.isBookmarked"
                :bookmarkId="activity.bookmarkId"
            />
        </div>
    </div>
</template>

<script setup>
import { ref, watchEffect, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import TimeLineCard from '@/components/Timeline/TimeLineCard.vue'
import { fetchTimelineList } from '@/helpers/timeline'

const activities = ref([])
const userStore = useUserStore()

onMounted(async () => {
    await userStore.fetchUser()
    console.log('유저 정보 fetch 완료')
})

const fetchActivities = async () => {
    const token = userStore.token
    try {
        const response = await fetchTimelineList(token)
        console.log('타임라인 응답 데이터:', response.data)
        activities.value = response.data.content.map((item) => {
            // BOOKMARK의 경우 lecture 정보는 item.log.lecture, TODO는 item.log.todo에 있음
            const isBookmark = item.type === 'BOOKMARK'
            const lectureData = isBookmark ? item.log?.lecture : item.log?.todo

            return {
                profileId: item.user?.profileId ?? '',
                type: mapType(item),
                userName: item.user?.nickname ?? '알 수 없는 사용자',
                userImage: item.user?.imageUrl ?? '/src/assets/icons/default-thumbnail.png',
                // BOOKMARK: lectureData.name, TODO: lectureData.lectureName
                lectureTitle: lectureData?.name || lectureData?.lectureName || '제목 없음',
                // lectureId 숫자값을 문자열로 변환 (타입 일관성을 위해)
                lectureId: isBookmark
                    ? lectureData?.id?.toString()
                    : lectureData?.lectureId?.toString() || '',
                // BOOKMARK는 강의 썸네일, TODO는 기본 이미지 사용
                imageUrl: isBookmark
                    ? lectureData?.imageUrl
                    : '/src/assets/icons/default-thumbnail.png',
                // BOOKMARK의 경우에만 tags가 존재
                tags:
                    isBookmark && lectureData?.tags
                        ? lectureData.tags.split(',').map((tag) => tag.trim())
                        : [],
                // TODO는 상태 변경 정보가 없으므로 BOOKMARK인 경우에만 매핑
                beforeStatus: isBookmark ? mapStatus(item.log?.beforeStatus) : '',
                afterStatus: isBookmark ? mapStatus(item.log?.afterStatus) : '',
                footprints: item.log?.footprints ?? [],
                date: item.createdAt,
                // 북마크 관련 데이터: BOOKMARK인 경우 lectureData에 있음(예시)
                isBookmarked: isBookmark ? lectureData?.isBookmarked || false : false,
                bookmarkId: isBookmark ? lectureData?.bookmarkId || null : null,
                // BOOKMARK인 경우 sourceName, TODO인 경우 sourceUrl
                sourceName: isBookmark ? (lectureData?.sourceName ?? '') : '',
                sourceUrl: !isBookmark ? (lectureData?.sourceUrl ?? '') : '',
            }
        })
    } catch (error) {
        console.error('❌ [타임라인 오류] 데이터를 가져오는 중 문제가 발생했습니다:', error)
    }
}

// 토큰이 존재할 때만 fetchActivities 호출
watchEffect(() => {
    if (!userStore.token) return
    fetchActivities()
})

// BOOKMARK와 TODO 타입을 매핑하는 함수
const mapType = (item) => {
    if (item.type === 'BOOKMARK') {
        return item.log?.beforeStatus == null ? 'new-lecture-interest' : 'lecture-status-change'
    } else if (item.type === 'TODO') {
        return 'footprint-added'
    }
    return 'unknown'
}

// 상태 코드를 문자열로 매핑하는 함수
const mapStatus = (status) => {
    switch (status) {
        case 1:
            return '수강 전'
        case 2:
            return '수강 중'
        case 3:
            return '수강 완료'
        default:
            return ''
    }
}
</script>
