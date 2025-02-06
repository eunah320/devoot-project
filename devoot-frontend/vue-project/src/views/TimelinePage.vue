<template>
    <div class="timeline-page">
        <h1 class="page-title">타임라인</h1>
        <div class="timeline-list">
            <!-- 타임라인 카드 반복 렌더링 -->
            <TimeLineCard
                v-for="(activity, index) in activities"
                :key="index"
                :type="activity.type"
                :userName="activity.userName"
                :lectureTitle="activity.lectureTitle"
                :tags="activity.tags"
                :beforeStatus="activity.beforeStatus"
                :afterStatus="activity.afterStatus"
                :footprints="activity.footprints"
                :date="activity.date"
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
    data() {
        return {
            activities: [], // 타임라인 활동 데이터
        }
    },
    async created() {
        try {
            // public/timeline_dummy_data.json에서 데이터 가져오기
            const response = await fetch('/timeline_dummy_data.json')
            this.activities = await response.json()
        } catch (error) {
            console.error('타임라인 데이터를 가져오는 중 오류 발생:', error)
        }
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
