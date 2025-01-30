<template>
    <div>
        <h1>강의 검색 결과</h1>
        <p v-if="searchQuery">검색어: {{ searchQuery }}</p>
        <p v-else>검색어를 입력하세요.</p>
    </div>
</template>

<script setup>
import { useRoute } from 'vue-router'

// 현재 경로의 쿼리 파라미터 가져오기
const route = useRoute()
const searchQuery = route.query.q || ''
</script>
