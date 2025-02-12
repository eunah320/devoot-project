FROM node:22.13

WORKDIR /app

COPY package.json package-lock.json ./
COPY dist/ ./dist/

RUN npm ci --only=production

EXPOSE 3000

CMD ["node", "dist/index.js"]
