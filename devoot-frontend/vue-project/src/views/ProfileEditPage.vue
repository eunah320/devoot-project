<template>
    <div class="max-w-3xl col-span-8 col-start-3 2xl:col-start-auto">
        <!-- 프로필 이미지 (파일 업로드) -->
        <div id="profile-edit" class="flex flex-col items-center w-full gap-4 mb-6">
            <!-- 프로필 이미지 -->
            <div class="overflow-hidden rounded-full w-36 h-36">
                <!-- 추후 defaultProfileImage 설정 변경 필요 (서버에서 가져오기) -->
                <img
                    :src="profileImage || defaultProfileImage"
                    alt="프로필 이미지"
                    class="object-cover w-full h-full border border-gray-200"
                />
            </div>

            <!-- 이미지 업로드 버튼 -->
            <div class="flex items-center gap-4">
                <!-- 선택된 파일 이름
                <span id="file-name" class="text-gray-300 text-caption">{{
                    fileName || '선택된 파일 없음'
                }}</span> -->
                <!-- 파일 선택 버튼 -->
                <label for="file-upload" class="button-primary">파일 선택</label>
                <input
                    id="file-upload"
                    type="file"
                    accept="image/*"
                    class="hidden"
                    @change="onFileChange"
                />
            </div>
        </div>

        <hr class="mb-4" />

        <!-- 이메일 (Firebase 로그인에서 획득) -->
        <div id="email-edit" class="mb-6">
            <div class="flex flex-row items-center gap-12">
                <div class="flex-1">
                    <label for="email" class="w-full h-8 text-body">이메일</label>
                    <p v-if="email !== ''" class="text-gray-300 text-caption">
                        이메일은 수정할 수 없습니다.
                    </p>
                </div>
                <div class="flex-1">
                    <input
                        id="email"
                        v-model="email"
                        type="text"
                        placeholder="example@gmail.com"
                        class="w-full h-8 p-3 placeholder-gray-200 border border-gray-200 rounded focus:bg-gray-100 text-body focus:border-2 focus:border-primary-500 focus:outline-none"
                        :class="{ 'text-gray-200': email !== '' }"
                        :readonly="email !== ''"
                    />
                </div>
            </div>
        </div>

        <!-- 아이디(profileId) -->
        <div id="id-edit" class="mb-6">
            <div class="flex flex-row items-center gap-12">
                <div class="flex-1">
                    <label for="id" class="w-full h-8 text-body">아이디</label>
                </div>
                <div class="flex-1">
                    <div class="flex flex-row items-center">
                        <input
                            id="id"
                            v-model="id"
                            type="text"
                            placeholder="아이디를 입력해주세요 (6-20자)"
                            class="w-full h-8 p-3 placeholder-gray-200 border border-gray-200 rounded-r-none focus:bg-gray-100 rounded-l-md text-body focus:border-2 focus:border-primary-500 focus:outline-none"
                        />
                        <button
                            class="w-auto h-8 px-3 text-gray-300 border border-l-0 border-gray-200 rounded-l-none text-caption whitespace-nowrap rounded-r-md hover:bg-gray-200 hover:text-primary-500"
                        >
                            중복확인
                        </button>
                    </div>
                    <!-- 중복 확인 결과 표시 -->
                    <p class="text-red-500 text-caption">사용 불가능한 아이디입니다.</p>
                </div>
            </div>
        </div>

        <!-- 닉네임 -->
        <div id="nickname-edit" class="mb-6">
            <div class="flex flex-row items-center gap-12">
                <div class="flex-1">
                    <label for="nickname" class="w-full h-8 text-body">닉네임</label>
                </div>
                <div class="flex-1">
                    <input
                        id="nickname"
                        v-model="nickname"
                        type="text"
                        placeholder="닉네임을 입력해주세요 (6-20자)"
                        class="w-full h-8 p-3 placeholder-gray-200 border border-gray-200 rounded-md focus:bg-gray-100 text-body focus:border-2 focus:border-primary-500 focus:outline-none"
                    />
                </div>
            </div>
        </div>

        <hr class="mb-4" />

        <!-- 공개 여부 (isPublic) -->
        <!-- 둘 중에 하나만 선택하게 세팅해야함 -->
        <div id="public-edit" class="mb-6">
            <div class="flex flex-row items-center gap-12">
                <div class="flex-1">
                    <div class="w-full text-body">계정 공개 범위</div>
                    <p class="text-gray-300 text-caption">
                        비공개 상태인 경우, 회원님이 승인한 팔로워만 프로필을 볼 수 있습니다.
                    </p>
                </div>
                <div class="flex-1">
                    <div class="flex flex-row items-center">
                        <button
                            :class="{
                                'bg-primary-100 text-primary-500': isPublic === 'true',
                                'text-black': isPublic !== 'true',
                            }"
                            class="w-full h-8 border border-gray-200 text-caption rounded-l-md hover:bg-gray-200 focus:bg-primary-100 focus:text-primary-500"
                            @click="isPublic = 'true'"
                        >
                            공개
                        </button>
                        <button
                            :class="{
                                'bg-primary-100 text-primary-500': isPublic === 'false',
                                'text-black': isPublic !== 'false',
                            }"
                            class="w-full h-8 border border-l-0 border-gray-200 text-caption rounded-r-md hover:bg-gray-200 focus:bg-primary-100 focus:text-primary-500"
                            @click="isPublic = 'false'"
                        >
                            비공개
                        </button>
                    </div>
                </div>
            </div>
        </div>

        <!-- 링크(제목, URL) -->
        <div id="link-edit" class="mb-6">
            <div class="flex flex-row gap-12">
                <div class="flex-1">
                    <div class="w-full text-body">링크</div>
                    <p class="text-gray-300 text-caption">
                        다른 사용자들에게 공유할 SNS 주소를 설정해주세요.
                    </p>
                </div>
                <div id="link" class="flex flex-col flex-1 gap-6">
                    <div class="flex flex-col gap-1">
                        <label for="link-title" class="text-gray-400 text-caption">제목</label>
                        <input
                            id="link-title"
                            v-model="linkTitle"
                            type="text"
                            placeholder="링크 제목을 입력해주세요"
                            class="w-full h-8 p-3 placeholder-gray-200 border border-gray-200 rounded-md focus:bg-gray-100 text-body focus:border-2 focus:border-primary-500 focus:outline-none"
                        />
                    </div>
                    <!-- 올바른 형태의 URL인지 검증 필요 -->
                    <div class="flex flex-col gap-1">
                        <label for="link-url" class="text-gray-400 text-caption">URL</label>
                        <input
                            id="link-url"
                            v-model="linkURL"
                            type="text"
                            placeholder="URL 주소를 입력해주세요"
                            class="w-full h-8 p-3 placeholder-gray-200 border border-gray-200 rounded-md focus:bg-gray-100 text-body focus:border-2 focus:border-primary-500 focus:outline-none"
                        />
                    </div>
                </div>
            </div>
        </div>

        <!-- 관심 태그 (최대 5개) -->
        <div id="link-edit" class="mb-6">
            <div class="flex flex-row gap-12">
                <div class="flex-1">
                    <div class="w-full text-body">관심 태그</div>
                    <p class="text-gray-300 text-caption">
                        관심 있는 기술 태그를 선택해주세요. <br />
                        (최대 5개)
                    </p>
                </div>
                <div class="flex-1">
                    <div>
                        <div
                            class="flex flex-wrap w-full gap-2 p-4 border border-gray-200 rounded-md"
                        >
                            <!-- 선택된 태그 먼저 표시 -->
                            <button
                                v-for="tag in sortedTags"
                                :key="tag"
                                :disabled="!isTagSelected(tag) && selectedTags.length >= 5"
                                :class="{
                                    'tag-gray': !isTagSelected(tag),
                                    'tag-primary': isTagSelected(tag),
                                }"
                                :title="
                                    !isTagSelected(tag) && selectedTags.length >= 5
                                        ? '최대 5개까지 선택 가능합니다.'
                                        : ''
                                "
                                @click="toggleTag(tag)"
                            >
                                <div class="flex flex-row items-center gap-1">
                                    {{ tag }}
                                    <div v-if="isTagSelected(tag)">
                                        <Delete class="w-3 h-3" />
                                    </div>
                                </div>
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <hr class="mb-4" />

        <!-- 저장 버튼 -->
        <div class="flex flex-row gap-2">
            <div class="flex-1"></div>
            <button class="button-primary">변경사항 저장</button>
            <button class="button-gray">취소</button>
        </div>
    </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import defaultProfileImage from '@/assets/default_profile_image.png'
import Delete from '@/assets/icons/delete.svg'

// 서버로 보낼 Form 데이터
const form = ref({
    profileId: '',
    nickname: '',
    links: '', // JSON 문자열 (단일 객체)
    isPublic: true,
    imageUrl: '',
    tags: '',
})

// [프로필 이미지]
// 선택된 파일 이름과 프로필 이미지 URL을 저장하는 변수
const fileName = ref(null)
const profileImage = ref(null)

// 파일 변경 핸들러
const onFileChange = (event) => {
    const file = event.target.files[0]
    if (file) {
        fileName.value = file.name

        // 이미지 미리보기 생성
        const reader = new FileReader()
        reader.onload = (e) => {
            profileImage.value = e.target.result
        }
        reader.readAsDataURL(file)
    }
}

// [이메일]
// Google Login : Firebase에서 가져와서 표시만, 수정 불가
// GitHub Login : 초기값 설정 후 수정 불가가
const email = ref('')

// [아이디]
const id = ref('')

// [닉네임]
const nickname = ref('')

// [계정 공개 범위]
const isPublic = ref('true')

// [링크]
const linkTitle = ref('')
const linkURL = ref('')

// links를 단일 JSON 문자열로 업데이트
watch(
    [linkTitle, linkURL],
    ([newTitle, newUrl]) => {
        if (newTitle && newUrl) {
            form.value.links = JSON.stringify({ title: newTitle, url: newUrl })
        } else {
            form.value.links = '' // 제목이나 URL이 비어 있으면 links 초기화
        }
    },
    { immediate: true }
)

// [태그]
// 태그 데이터
const allTags = [
    'HTML',
    'JavaScript',
    'Java',
    'Ruby',
    'TypeScript',
    'Swift',
    'Kotlin',
    'Python',
    'C',
    'C++',
    'C#',
    'Go',
    'Scala',
    'Dart',
    'MySQL',
    'Oracle',
    'Markdown',
    '데이터 분석',
    '데이터 엔지니어링',
    '딥러닝/머신러닝',
    '컴퓨터 비전',
    '자연어 처리',
    '시스템/운영체제',
    '블록체인',
    '컴퓨터 구조',
    '임베디드/IoT',
    '반도체',
    '로보공학',
    'UX/UI',
]

const selectedTags = ref([]) // 선택된 태그를 담는 배열

// 태그 선택/취소 토글
function toggleTag(tag) {
    if (isTagSelected(tag)) {
        // 선택된 태그를 클릭하면 선택 취소
        selectedTags.value = selectedTags.value.filter((t) => t !== tag)
    } else if (selectedTags.value.length < 5) {
        // 선택되지 않은 태그 추가
        selectedTags.value.push(tag)
    }
}

// 태그가 선택되었는지 확인
function isTagSelected(tag) {
    return selectedTags.value.includes(tag)
}

// 선택된 태그를 앞에 정렬
const sortedTags = computed(() => {
    return [
        ...selectedTags.value, // 선택된 태그
        ...allTags.filter((tag) => !selectedTags.value.includes(tag)), // 선택되지 않은 태그
    ]
})
</script>

<style scoped></style>
