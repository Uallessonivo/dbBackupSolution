FROM node:lts AS runtime
WORKDIR /app
COPY package.json ./
COPY pnpm-lock.yaml ./
COPY . .
RUN npm install -g pnpm && npm install && pnpm install
RUN pnpm build
CMD ["pnpm", "start"]