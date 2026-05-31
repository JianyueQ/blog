<template>
  <div class="moments-container">
    <!-- 说说列表 -->
    <div class="moments-list" v-loading="loading">
      <div v-for="moment in moments" :key="moment.id" class="moment-item">
        <div class="user-avatar">
          <img v-lazy="moment.avatar" :key="moment.avatar" class="avatar" />
          <!-- 移动端显示的用户信息 -->
          <div class="mobile-user-info">
            <span class="name">{{ moment.nickname }}</span>
            <span class="time">
              <i class="fas fa-clock"></i>
              {{ formatTime(moment.createTime) }}
            </span>
          </div>
        </div>
        <div class="moment-main">
          <!-- PC端显示的用户信息 -->
          <div class="moment-header">
            <span class="name">{{ moment.nickname }}</span>
            <span class="time">
              <i class="fas fa-clock"></i>
              {{ formatTime(moment.createTime) }}
            </span>
          </div>
          <div class="moment-content-wrapper">
            <div class="moment-content markdown-body" v-html="moment.htmlContent || moment.content"></div>
            <div class="moment-images" v-if="moment.images?.length">
              <img v-for="(img, index) in moment.images" :key="img" v-lazy="img"
                @click="previewImage(moment.images, index)" />
            </div>
          </div>
        </div>
      </div>
      <div class="pagination-box">
        <el-pagination background v-if="moments.length" @current-change="handlePageChange"
          :current-page="params.pageNum" :page-size="params.pageSize" layout="prev, pager, next" :total="total">
        </el-pagination>
      </div>
    </div>

    <!-- 添加图片预览组件 -->
    <mj-image-preview ref="imagePreview" />
  </div>
</template>

<script>
import { formatTime } from '@/utils/time'
import { getMoments } from '@/api/moments'

export default {
  name: 'Moments',

  data() {
    return {
      moments: [],
      loading: false,
      total: 0,
      params: {
        pageNum: 1,
        pageSize: 10,
      },
    }
  },
  mounted() {
    this.fetchMoments()
  },

  methods: {
    parseImages(images) {
      if (!images) return []
      return images.split(',').filter(img => img)
    },
    async fetchMoments() {
      try {
        this.loading = true
        const res = await getMoments(this.params)
        this.moments = res.data.records
        this.total = res.data.total
        this.moments.forEach(moment => {
          moment.images = this.parseImages(moment.images)
          // 后端已返回 HTML，前端无需再次渲染
        })
      } catch (error) {
        console.error('获取说说列表失败:', error)
      } finally {
        this.loading = false
      }
    },

    handlePageChange(page) {
      this.params.pageNum = page
      this.fetchMoments()
    },

    previewImage(images, index) {
      // 使用图片预览组件的 show 方法
      this.$refs.imagePreview.show(images, index)
    },

    formatTime(time) {
      return formatTime(time)
    }
  }
}
</script>

<style lang="scss" scoped>
.moments-container {
  max-width: 800px;
  margin: 0 auto;
  min-height: calc(100vh - 70px);
}

.moment-item {
  border-radius: $border-radius-sm * 3;
  padding: $spacing-lg;
  margin-bottom: $spacing-sm;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.02);
  display: flex;
  gap: 16px;

  @media screen and (max-width: 768px) {
    flex-direction: column;
    gap: 12px;
    padding: $spacing-md;
  }
}

.user-avatar {
  flex-shrink: 0;
  width: 46px;

  .avatar {
    width: 46px;
    height: 46px;
    border-radius: $border-radius-md;
    object-fit: cover;
  }

  @media screen and (max-width: 768px) {
    width: auto;
    display: flex;
    align-items: center;
    gap: 12px;

    .avatar {
      width: 40px;
      height: 40px;
    }
  }
}

.moment-main {
  flex: 1;
  min-width: 0;

  @media screen and (max-width: 768px) {
    .moment-header {
      display: none;
    }
  }
}

.moment-header {
  margin-bottom: 12px;
  display: flex;
  align-items: center;
  gap: 10px;

  .name {
    font-size: 15px;
    font-weight: 500;
    color: var(--text-primary);
  }

  .time {
    font-size: 13px;
    color: var(--text-secondary);
    display: flex;
    align-items: center;
    gap: 4px;

    i {
      color: $primary;
    }
  }
}

.moment-content-wrapper {
  background: var(--card-bg);
  padding: 16px;
  border-radius: 0 $border-radius-lg*2 $border-radius-lg*2 $border-radius-lg*2;

  @media screen and (max-width: 768px) {
    border-radius: $border-radius-lg;
  }

  .moment-content {
    color: var(--text-primary);
    line-height: 1.8;
    font-size: 15px;
    word-break: break-word;
    
    // Markdown 样式
    :deep(p) {
      margin: 0 0 12px 0;
      line-height: 1.8;
    }
    
    :deep(h1), :deep(h2), :deep(h3), :deep(h4), :deep(h5), :deep(h6) {
      margin: 16px 0 8px 0;
      font-weight: 600;
      line-height: 1.4;
      color: var(--text-primary);
    }
    
    :deep(h1) { font-size: 1.8em; }
    :deep(h2) { font-size: 1.5em; }
    :deep(h3) { font-size: 1.3em; }
    :deep(h4) { font-size: 1.1em; }
    
    :deep(ul), :deep(ol) {
      margin: 8px 0;
      padding-left: 24px;
    }
    
    :deep(li) {
      margin-left: 0;
      margin-bottom: 4px;
    }
    
    :deep(blockquote) {
      margin: 12px 0;
      padding: 8px 16px;
      border-left: 4px solid $primary;
      background: var(--hover-bg);
      color: var(--text-secondary);
    }
    
    :deep(code) {
      padding: 2px 6px;
      background: var(--hover-bg);
      border-radius: 4px;
      font-family: 'Courier New', monospace;
      font-size: 0.9em;
      color: var(--text-primary);
    }
    
    :deep(pre) {
      margin: 12px 0;
      padding: 12px;
      background: var(--hover-bg);
      border-radius: 8px;
      overflow-x: auto;
      
      code {
        padding: 0;
        background: none;
      }
    }
    
    :deep(img) {
      max-width: 100%;
      height: auto;
      border-radius: 8px;
      margin: 12px 0;
    }
    
    :deep(a) {
      color: $primary;
      text-decoration: none;
      
      &:hover {
        text-decoration: underline;
      }
    }
    
    :deep(table) {
      width: 100%;
      border-collapse: collapse;
      margin: 12px 0;
      
      th, td {
        border: 1px solid var(--border-color);
        padding: 8px 12px;
        text-align: left;
      }
      
      th {
        background: var(--hover-bg);
        font-weight: 600;
      }
    }
    
    :deep(hr) {
      border: none;
      border-top: 1px solid var(--border-color);
      margin: 16px 0;
    }
  }

  .moment-images {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
    gap: 8px;
    margin-top: 16px;

    @media screen and (max-width: 768px) {
      grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
    }

    img {
      width: 100%;
      aspect-ratio: 16/9;
      object-fit: cover;
      border-radius: 8px;
      cursor: zoom-in;
    }
  }
}

.mobile-user-info {
  display: none;
  flex-direction: column;

  @media screen and (max-width: 768px) {
    display: flex;
  }

  .name {
    font-size: 15px;
    font-weight: 500;
    color: var(--text-primary);
  }

  .time {
    font-size: 13px;
    color: var(--text-secondary);
    display: flex;
    align-items: center;
    gap: 4px;
    margin-top: 2px;

    i {
      color: $primary;
    }
  }
}
</style>