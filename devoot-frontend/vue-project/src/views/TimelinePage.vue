<template>
    <div class="timeline-page">
        <h1 class="page-title">타임라인</h1>
        <div class="timeline-list">
            <!-- 강의 상태 변경 카드 -->
            <TimeLineCard
                type="lecture-status-change"
                userName="새발바닥"
                lectureTitle="백엔드 개발자 취업 토탈 가이드(back-end, 멘토링 경험기반)"
                date="2025-01-19"
            />

            <!-- 새로운 강의 관심 카드 -->
            <TimeLineCard
                type="new-lecture-interest"
                userName="새발바닥"
                lectureTitle="백엔드 개발자 취업 토탈 가이드(back-end, 멘토링 경험기반)"
                date="2025-01-19"
            />

            <!-- 발자국 추가 카드 -->
            <TimeLineCard
                type="footprint-added"
                userName="새발바닥"
                footprints="['초보를 위한 파이썬 기초 강의', '1강: 언어 기초']"
                date="2025-01-19"
            />
        </div>
    </div>
</template>

<script>
import TimeLineCard from '@/components/Timeline/TimeLineCard.vue'

export default {
    name: 'TimelinePage',
    components: {
        TimeLineCard,
    },
}
</script>

<style scoped>
.timeline-page {
    padding: 16px;
}

.page-title {
    font-size: 24px;
    font-weight: bold;
    margin-bottom: 16px;
}

.timeline-list {
    display: flex;
    flex-direction: column;
    gap: 16px;
}
</style>
