<template>
    <div id="navigation" class="w-[13.5rem] flex flex-col h-screen bg-white border-r border-gray-200">
        <!-- 로고 -->
    <div id="logo" class="flex flex-row">
        <div class="w-full text-h1">개발바닥</div>
    </div>

    <!-- 메뉴 -->
    <div id="menu-container" class="flex flex-col flex-1 py-3">
        <!-- 서비스 관련 메뉴 -->
        <div id="menu">
            <div class="w-full py-4 px-9">
                <p class="text-left text-body">홈</p>
            </div>
            <div class="w-full py-4 px-9">
                <p class="text-left text-body">강의</p>
            </div>
            <div class="w-full py-4 px-9">
                <p class="text-left text-body">타임라인</p>
            </div>
            <div class="w-full py-4 px-9">
                <p class="text-left text-body">프로필</p>
            </div>
        </div>
        
        <!-- 간격용 -->
        <div class="flex-1"></div>

        <!-- 회원 관련 메뉴-->
        <div id="member-menu">
            <div class="w-full py-4 px-9">
                <p class="text-left text-body">로그인/회원가입</p>
            </div>
            <div class="w-full py-4 px-9">
                <p class="text-left text-body">회원정보 수정</p>
            </div>
            <div class="w-full py-4 px-9">
                <p class="text-left text-body">로그아웃</p>
            </div>
        </div>
    </div>
    </div>
    
</template>

<script setup>

</script>

<style scoped>

</style>