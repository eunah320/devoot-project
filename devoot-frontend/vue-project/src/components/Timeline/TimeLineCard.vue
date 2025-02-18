<template>
    <div class="p-6 mb-4 bg-white border border-gray-200 rounded-2xl w-[72rem]">
      <!-- 카드 헤더 -->
      <div class="flex items-center justify-between">
        <div class="flex items-center">
          <img
            :src="userImage"
            alt="프로필 사진"
            class="object-cover w-10 h-10 bg-gray-200 rounded-full"
          />
          <p
            class="flex items-center ml-3 text-sm text-gray-700 cursor-pointer"
            @click="goToProfile(profileId)"
          >
            <div class="text-body-bold">{{ userName }}</div>
            <div v-if="type === 'lecture-status-change'">
              님이
              <span
                v-if="beforeStatus"
                class="h-6 px-2 py-1 bg-gray-100 rounded-full"
                :class="statusColor(beforeStatus)"
              >
                {{ beforeStatus }}
              </span>
              <span v-if="beforeStatus" class="text-body-bold"> &gt; </span>
              <span
                v-if="afterStatus"
                class="h-6 px-2 py-1 bg-gray-100 rounded-full"
                :class="statusColor(afterStatus)"
              >
                {{ afterStatus }}
              </span>
              강의 상태를 변경하였습니다.
            </div>
            <div v-else-if="type === 'new-lecture-interest'">
              님이 새로운 강의에 관심을 가지기 시작했습니다.
            </div>
            <div v-else-if="type === 'footprint-added'">
              님의 발자국이 추가되었습니다
            </div>
          </p>
        </div>
        <span class="text-gray-400 text-caption">{{ formattedDate }}</span>
      </div>
  
      <!-- 강의 정보 (BOOKMARK & TODO) -->
      <div
        class="flex mt-4 border border-gray-200 cursor-pointer rounded-xl"
        @click="goToLecture(lectureId)"
      >
        <img
          :src="imageUrl"
          alt="강의 썸네일"
          class="object-cover rounded-l-xl w-[16.75rem] h-[10rem]"
        />
        <div class="flex flex-col w-full h-full mx-6 my-5">
          <!-- 사이트명(sourceName) 표시 -->
          <div class="mb-1 text-gray-400 text-body" v-if="sourceName">
            {{ sourceName }}
          </div>
  
          <!-- 강의 타이틀 + 북마크 아이콘 (아이콘만 표시) -->
          <div class="flex items-center justify-between">
            <p class="text-h3">{{ lectureTitle }}</p>
            <button @click.stop="toggleBookmark" class="p-2 focus:outline-none">
              <component
                :is="isBookmarked ? BookmarkFill : BookmarkDefault"
                class="w-6 h-6 text-primary-500"
              />
            </button>
          </div>
  
          <!-- 태그 영역 -->
          <div v-if="tags.length" class="flex mt-16 space-x-2">
            <span v-for="(tag, index) in tags" :key="index" class="tag-gray">
              #{{ tag }}
            </span>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script setup>
  import { computed, ref, onMounted } from 'vue'
  import { useRouter } from 'vue-router'
  import axios from 'axios'
  import BookmarkDefault from '@/assets/icons/bookmark_default.svg'
  import BookmarkFill from '@/assets/icons/bookmark_filled.svg'
  import { addBookmark, removeBookmark } from '@/helpers/lecture'
  import { useUserStore } from '@/stores/user'
  
  // ------------------------------
  // 1. Props 정의
  // ------------------------------
  const props = defineProps({
    profileId: { type: String, required: true, default: '' },
    lectureId: { type: String, required: true, default: '' },
    type: { type: String, required: true },
    userName: { type: String, required: true },
    userImage: { type: String, default: '/src/assets/icons/default-thumbnail.png' },
    lectureTitle: { type: String, default: '제목 없음' },
    imageUrl: { type: String, default: '/src/assets/icons/default-thumbnail.png' },
    sourceName: { type: String, default: '' },
    tags: { type: Array, default: () => [] },
    beforeStatus: { type: String, default: '' },
    afterStatus: { type: String, default: '' },
    date: { type: String, required: true },
    // 북마크 관련 초기 상태 (부모에서 내려줄 수 있음)
    isBookmarked: { type: Boolean, default: false },
    bookmarkId: { type: [Number, String], default: null },
  })
  
  // ------------------------------
  // 2. Composition API 로직
  // ------------------------------
  const router = useRouter()
  const userStore = useUserStore()
  
  // 프로필 페이지 이동
  function goToProfile(profileId) {
    if (!profileId) {
      console.error('❌ 프로필 ID가 없음!')
      return
    }
    router.push(`/profile/${profileId}`)
  }
  
  // 강의 상세 페이지 이동
  function goToLecture(lectureId) {
    if (!lectureId) {
      console.error('❌ 강의 ID가 없음!')
      return
    }
    router.push(`/lecture/${lectureId}`)
  }
  
  // 상태 텍스트에 따른 색상 클래스 반환 함수
  function statusColor(status) {
    switch (status) {
      case '수강 전':
        return 'text-gray-400'
      case '수강 중':
        return 'text-[#FDE03A]'
      case '수강 완료':
        return 'text-[#0EDB8C]'
      default:
        return ''
    }
  }
  
  // 날짜 포맷팅
  const formattedDate = computed(() => {
    return new Date(props.date).toLocaleDateString('ko-KR', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
    })
  })
  
  // ------------------------------
  // 3. 북마크 상태 및 토글 로직
  // ------------------------------
  const isBookmarked = ref(props.isBookmarked)
  const bookmarkId = ref(props.bookmarkId)
  
  // 현재 로그인한 사용자의 북마크 상태를 GET 요청으로 확인
  async function fetchBookmarkStatus() {
    try {
      // 주의: props.profileId 대신 현재 로그인한 사용자 ID를 사용
      const response = await axios.get(`/api/users/${userStore.userId}/bookmarks`, {
        headers: { Authorization: `Bearer ${userStore.token}` },
      })
      // 응답은 { "todo": [...], "in-progress": [...], "done": [...] } 구조로 가정
      const todo = response.data.todo || []
      const inProgress = response.data["in-progress"] || []
      const done = response.data.done || []
      const allBookmarks = [...todo, ...inProgress, ...done]
      // 현재 카드의 강의가 북마크 목록에 포함되어 있는지 체크 (lecture 객체의 id로 비교)
      const found = allBookmarks.find(
        (b) => b.lecture && b.lecture.id.toString() === props.lectureId.toString()
      )
      if (found) {
        isBookmarked.value = true
        bookmarkId.value = found.id
      } else {
        isBookmarked.value = false
        bookmarkId.value = null
      }
    } catch (error) {
      console.error('북마크 상태를 가져오는데 실패:', error)
    }
  }
  
  // 타임라인 카드가 마운트될 때 북마크 상태 확인
  onMounted(() => {
    fetchBookmarkStatus()
  })
  
  // 토글 북마크 함수
  async function toggleBookmark() {
    try {
      if (!isBookmarked.value) {
        // 북마크 추가 요청
        const response = await addBookmark(userStore.token, userStore.userId, props.lectureId)
        bookmarkId.value = response.data.id
        console.log('✅ 북마크 추가 성공:', bookmarkId.value)
      } else {
        // 북마크 삭제 요청
        if (bookmarkId.value) {
          await removeBookmark(userStore.token, userStore.userId, bookmarkId.value)
          console.log('✅ 북마크 삭제 성공')
          bookmarkId.value = null
        }
      }
      isBookmarked.value = !isBookmarked.value
    } catch (error) {
      console.error('❌ 북마크 토글 실패:', error)
    }
  }
  </script>
  