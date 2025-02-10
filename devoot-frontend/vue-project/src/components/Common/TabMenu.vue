<template>
    <div class="grid col-12">
        <div class="flex flex-row items-center border-b border-gray-200 h-11">
            <div
                class="flex-1 text-center cursor-pointer text-h3"
                :class="{ 'text-primary-500': modelValue === 'left' }"
                @click="$emit('update:modelValue', 'left')"
            >
                {{ tabLeft }}
            </div>
            <div class="h-full border border-l border-gray-200"></div>
            <div
                class="flex-1 text-center cursor-pointer text-h3"
                :class="{ 'text-primary-500': modelValue === 'right' }"
                @click="$emit('update:modelValue', 'right')"
            >
                {{ tabRight }}
            </div>
        </div>
    </div>
</template>

<script setup>
// 부모 컴포넌트에서 받을 props
defineProps({
    tabLeft: {
        type: String,
        required: true,
    },
    tabRight: {
        type: String,
        required: true,
    },
    modelValue: {
        type: String,
        required: true,
    },
})

// 부모 컴포넌트에 이벤트 전달
defineEmits(['update:modelValue'])
</script>

<style scoped></style>
