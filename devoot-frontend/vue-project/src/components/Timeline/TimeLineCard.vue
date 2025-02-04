<template>
    <div class="timeline-card">
        <div v-if="type === 'lecture-status-change'" class="card-content">
            <p>
                <strong>{{ userName }}</strong
                >님이 강의 상태를 변경하였습니다.
            </p>
            <div class="lecture-info">
                <span class="badge">수정 전</span>
                <span class="badge">수정 후</span>
                <div class="lecture-title">{{ lectureTitle }}</div>
            </div>
            <span class="date">{{ formattedDate }}</span>
        </div>

        <div v-else-if="type === 'new-lecture-interest'" class="card-content">
            <p>
                <strong>{{ userName }}</strong
                >님이 새로운 강의에 관심을 가지기 시작했습니다.
            </p>
            <div class="lecture-info">
                <div class="lecture-title">{{ lectureTitle }}</div>
            </div>
            <span class="date">{{ formattedDate }}</span>
        </div>

        <div v-else-if="type === 'footprint-added'" class="card-content">
            <p>
                <strong>{{ userName }}</strong
                >님의 발자국이 추가되었습니다.
            </p>
            <ul class="footprint-list">
                <li v-for="(footprint, index) in footprints" :key="index">
                    {{ footprint }}
                </li>
            </ul>
            <span class="date">{{ formattedDate }}</span>
        </div>
    </div>
</template>

<script>
export default {
    name: 'TimeLineCard',
    props: {
        type: {
            type: String,
            required: true,
            validator: (value) =>
                ['lecture-status-change', 'new-lecture-interest', 'footprint-added'].includes(
                    value
                ),
        },
        userName: {
            type: String,
            required: true,
        },
        lectureTitle: {
            type: String,
            default: '',
        },
        footprints: {
            type: Array,
            default: () => [],
        },
        date: {
            type: String,
            required: true,
        },
    },
    computed: {
        formattedDate() {
            const options = { year: 'numeric', month: '2-digit', day: '2-digit' }
            return new Date(this.date).toLocaleDateString('ko-KR', options)
        },
    },
}
</script>

<style scoped>
.timeline-card {
    border-bottom: 1px solid #e5e7eb;
    padding: 16px;
}

.card-content {
    display: flex;
    flex-direction: column;
}

.lecture-info {
    margin-top: 8px;
}

.badge {
    display: inline-block;
    background-color: #f3f4f6;
    color: #374151;
    padding: 4px 8px;
    border-radius: 4px;
    font-size: 12px;
}

.lecture-title {
    font-weight: bold;
    margin-top: 8px;
}

.date {
    margin-top: auto;
    font-size: 12px;
    color: #9ca3af;
}

.footprint-list {
    list-style-type: none;
    padding-left: 0;
}

.footprint-list li {
    margin-bottom: 4px;
}
</style>
