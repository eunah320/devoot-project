FROM node:22.13

WORKDIR /app

COPY package*.json ./

RUN npm install --save @rollup/rollup-linux-x64-gnu
RUN npm install

COPY . .

ENV NODE_ENV=production

RUN npm run build

EXPOSE 3000

ENTRYPOINT ["npm", "run", "dev"]
