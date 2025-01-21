import js from '@eslint/js'
import pluginVue from 'eslint-plugin-vue'
import skipFormatting from '@vue/eslint-config-prettier/skip-formatting'

export default [
  {
    name: 'app/files-to-lint',
    files: ['**/*.{js,mjs,jsx,vue}'], // 린트가 적용될 파일 확장자
  },

  {
    name: 'app/files-to-ignore',
    ignores: ['**/dist/**', '**/dist-ssr/**', '**/coverage/**'],  // 제외할 디렉토리
  },

  js.configs.recommended, // JavaScript 기본 추천 규칙
  ...pluginVue.configs['flat/essential'], // Vue3 필수 규칙
  skipFormatting, // Prettier와 충돌 방지

  // 추가 규칙 예시 - 이부분을 수정하여 사용하면 됩니다.
  {
    rules: {
      'no-console': 'warn', // console.log 경고 처리
      'vue/multi-word-component-names': 'off', // Vue 단일 단어 컴포넌트 이름 허용
    },
  },
]
