<template>
    <div class="timeline-page">
        <h1 class="page-title">타임라인</h1>

        <!-- 강의 상태 변경 카드 -->
        <StatusChangeCard
            userName="새발바닥"
            lectureTitle="백엔드 개발자 취업 토탈 가이드(back-end, 멘토링 경험기반)"
            date="2025-01-19"
        />

        <!-- 새로운 강의 관심 카드 -->
        <NewBookmarkCard
            userName="새발바닥"
            lectureTitle="백엔드 개발자 취업 토탈 가이드(back-end, 멘토링 경험기반)"
            date="2025-01-19"
        />

        <!-- 발자국 추가 카드 -->
        <FootprintCard
            userName="새발바닥"
            :footprints="[
                { lectureTitle: '초보를 위한 파이썬 기초 강의', detail: '1강 언어 기초' },
            ]"
            date="2025-01-19"
        />
    </div>
</template>

<script>
// 세 가지 카드 컴포넌트 가져오기
import FootprintCard from '@/components/Timeline/FootprintCard.vue'
import NewBookmarkCard from '@/components/Timeline/NewBookmarkCard.vue'
import StatusChangeCard from '@/components/Timeline/StatusChangeCard.vue'

export default {
    name: 'TimelinePage',
    components: {
        FootprintCard,
        NewBookmarkCard,
        StatusChangeCard,
    },
}
</script>

<style scoped></style>
