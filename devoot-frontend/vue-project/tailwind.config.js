/** @type {import('tailwindcss').Config} */
export default {
  content: ["./index.html", "./src/**/*.{vue,js,ts,jsx,tsx}"],
  theme: {
    extend: {
      colors: {
        primary: {
          100: '#CDE3FF',
          200: '#9AC7FF',
          300: '#68AAFF',
          400: '#358EFF',
          500: '#0372FF',
        },
        gray: {
          100: '#F4F6F8',
          200: '#E8EBEE',
          300: '#9DA0A3',
          400: '#75787B',
          500: '#25282B',
        },
      },
    },
  },
  plugins: [],
}