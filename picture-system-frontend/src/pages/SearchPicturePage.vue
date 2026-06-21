<template>
  <div class="search-picture-page">
    <header class="page-header">
      <h1 class="page-title">以图搜图</h1>
      <p class="page-desc">基于原图在全网搜索相似图片，点击结果可在新标签页查看来源</p>
    </header>

    <section class="source-section">
      <div class="section-header">
        <h2 class="section-title">原图</h2>
      </div>
      <a-card hoverable :bordered="false" class="source-card">
        <template #cover>
          <div class="image-cover source-cover">
            <img
              :alt="picture.name ?? '原图'"
              :src="picture.compressedUrl ?? picture.url"
              class="cover-img"
            />
          </div>
        </template>
      </a-card>
    </section>

    <a-divider class="section-divider" />

    <section class="results-section">
      <div class="section-header">
        <h2 class="section-title">识图结果</h2>
        <span v-if="!loading && dataList.length > 0" class="result-count">
          共 {{ dataList.length }} 张
        </span>
      </div>

      <a-list
        :grid="{ gutter: 16, xs: 1, sm: 2, md: 3, lg: 4, xl: 5, xxl: 6 }"
        :data-source="dataList"
        :loading="loading"
        class="result-list"
      >
        <template #renderItem="{ item: picture }">
          <a-list-item class="result-item">
            <a
              :href="picture.fromUrl"
              target="_blank"
              rel="noopener noreferrer"
              class="result-link"
            >
              <a-card hoverable :bordered="false" class="result-card">
                <template #cover>
                  <div class="image-cover result-cover">
                    <img
                      :alt="picture.name ?? '相似图片'"
                      :src="picture.thumbUrl ?? picture.url"
                      class="cover-img"
                      loading="lazy"
                    />
                    <div class="result-overlay" aria-hidden="true">
                      <LinkOutlined class="overlay-icon" />
                      <span class="overlay-text">查看来源</span>
                    </div>
                  </div>
                </template>
              </a-card>
            </a>
          </a-list-item>
        </template>
      </a-list>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { getPictureVoByIdUsingGet, searchPictureUsingPost } from '@/api/pictureController.ts'
import { message } from 'ant-design-vue'
import { LinkOutlined } from '@ant-design/icons-vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const pictureId = computed(() => route.query.pictureId as string)

const picture = ref<API.PictureVO>({})

// 获取图片详情
const fetchPictureDetail = async () => {
  try {
    const res = await getPictureVoByIdUsingGet({
      id: pictureId.value,
    })
    if (res.data.code === 0 && res.data.data) {
      picture.value = res.data.data
    } else {
      message.error('获取图片详情失败，' + res.data.message)
    }
  } catch (e: any) {
    message.error('获取图片详情失败：' + e.message)
  }
}

onMounted(() => {
  fetchPictureDetail()
})

// 以图搜图结果
const dataList = ref<API.ImageSearchResult[]>([])
const loading = ref<boolean>(false)

// 获取以图搜图结果
const fetchResultData = async () => {
  try {
    loading.value = true
    const res = await searchPictureUsingPost({
      pictureId: pictureId.value,
    })
    if (res.data.code === 0 && res.data.data) {
      dataList.value = res.data.data ?? []
    } else {
      message.error('获取数据失败，' + res.data.message)
    }
  } catch (e: any) {
    message.error('获取数据失败：' + e.message)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchResultData()
})
</script>

<style scoped>
.search-picture-page {
  max-width: 1280px;
  margin: 0 auto;
  padding-bottom: 24px;
}

.page-header {
  margin-bottom: 28px;
}

.page-title {
  margin: 0 0 8px;
  font-size: 1.5rem;
  font-weight: 600;
  line-height: 1.3;
  color: #09090b;
  letter-spacing: -0.02em;
}

.page-desc {
  margin: 0;
  font-size: 0.9375rem;
  line-height: 1.6;
  color: #475569;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 16px;
}

.section-title {
  margin: 0;
  font-size: 1rem;
  font-weight: 600;
  color: #18181b;
}

.result-count {
  flex-shrink: 0;
  padding: 2px 10px;
  font-size: 0.8125rem;
  font-weight: 500;
  color: #475569;
  background: #f1f5f9;
  border-radius: 9999px;
}

.source-section {
  margin-bottom: 8px;
}

.source-card {
  width: 100%;
  max-width: 280px;
  overflow: hidden;
  border: 1px solid #f0f0f0;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(15, 23, 42, 0.04);
  transition: box-shadow 0.2s ease, border-color 0.2s ease;
}

.source-card:hover {
  border-color: #e2e8f0;
  box-shadow: 0 4px 12px rgba(15, 23, 42, 0.08);
}

.section-divider {
  margin: 28px 0;
  border-color: #f0f0f0;
}

.image-cover {
  position: relative;
  overflow: hidden;
  background: #f8fafc;
}

.source-cover {
  aspect-ratio: 4 / 3;
}

.result-cover {
  aspect-ratio: 4 / 3;
}

.cover-img {
  display: block;
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.25s ease;
}

.result-link {
  display: block;
  color: inherit;
  text-decoration: none;
  cursor: pointer;
}

.result-link:focus-visible {
  outline: 2px solid #18181b;
  outline-offset: 2px;
  border-radius: 12px;
}

.result-item {
  padding: 0 !important;
}

.result-card {
  overflow: hidden;
  border: 1px solid #f0f0f0;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(15, 23, 42, 0.04);
  transition: box-shadow 0.2s ease, border-color 0.2s ease;
}

.result-card:hover {
  border-color: #e2e8f0;
  box-shadow: 0 4px 12px rgba(15, 23, 42, 0.08);
}

.result-link:hover .cover-img {
  transform: scale(1.03);
}

.result-overlay {
  position: absolute;
  inset: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 6px;
  color: #fff;
  background: rgba(9, 9, 11, 0.45);
  opacity: 0;
  transition: opacity 0.2s ease;
}

.result-link:hover .result-overlay,
.result-link:focus-visible .result-overlay {
  opacity: 1;
}

.overlay-icon {
  font-size: 1.25rem;
}

.overlay-text {
  font-size: 0.8125rem;
  font-weight: 500;
}

.result-list :deep(.ant-spin-nested-loading) {
  min-height: 120px;
}

@media (max-width: 767px) {
  .page-title {
    font-size: 1.25rem;
  }

  .page-desc {
    font-size: 0.875rem;
  }

  .source-card {
    max-width: 100%;
  }

  .section-divider {
    margin: 20px 0;
  }
}

@media (prefers-reduced-motion: reduce) {
  .source-card,
  .result-card,
  .cover-img,
  .result-overlay {
    transition: none;
  }

  .result-link:hover .cover-img {
    transform: none;
  }
}
</style>
