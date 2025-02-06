<template>
    <div class="grid col-12">
        <div class="flex flex-row items-center border-b border-gray-200 h-11">
            <div
                class="flex-1 text-center cursor-pointer text-h3"
                :class="{ 'text-primary-500': selectedTab === 'left' }"
                @click="selectTab('left')"
            >
                {{ props.tabLeft }}
            </div>
            <div class="h-full border border-l border-gray-200"></div>
            <div
                class="flex-1 text-center cursor-pointer text-h3"
                :class="{ 'text-primary-500': selectedTab === 'right' }"
                @click="selectTab('right')"
            >
                {{ props.tabRight }}
            </div>
        </div>
    </div>
</template>

<script setup>
import { defineProps, defineEmits, ref } from 'vue'

// 부모 컴포넌트에서 받을 props
const props = defineProps({
    tabLeft: {
        type: String,
        required: true,
    },
    tabRight: {
        type: String,
        required: true,
    },
})

// 선택된 탭 상태
const selectedTab = ref('left')

// 부모 컴포넌트에 이벤트 전달
const emit = defineEmits(['updateTab'])

const selectTab = (tab) => {
    selectedTab.value = tab
    emit('updateTab', tab)
}
</script>

<style scoped></style>
