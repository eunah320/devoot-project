<template>
    <div>
        <TodoAddModal />
        <KanbanSection />
        <ProfileContribution />
        <KanbanCard />
    </div>
</template>

<script setup>
import ProfileContribution from '@/components/Profile/ProfileContribution.vue'
import KanbanCard from '@/components/Profile/KanbanCard.vue'
import KanbanSection from '@/components/Profile/KanbanSection.vue'
import TodoAddModal from '@/components/Profile/TodoAddModal.vue'
</script>
<style scoped></style>
