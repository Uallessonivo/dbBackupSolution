FROM node:lts AS runtime
WORKDIR /app
COPY pnpm-lock.yaml ./
RUN npm install -g pnpm && pnpm install
COPY . .
RUN pnpm run build
CMD ["pnpm", "start"]
