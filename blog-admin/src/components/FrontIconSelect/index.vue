<template>
  <el-dialog
    v-model="dialogVisible"
    :title="mode === 'dark' ? '选择暗色图标' : '选择亮色图标'"
    width="700px"
    append-to-body
    top="5vh"
  >
    <div class="icon-container">
      <div class="search-bar">
        <el-input
          v-model="searchText"
          placeholder="搜索图标"
          clearable
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </div>
      <el-scrollbar height="400px">
        <div class="icon-list">
          <div
            v-for="name in filteredIcons"
            :key="name"
            class="icon-item"
            :class="{ active: modelValue === name }"
            @click="selectIcon(name)"
          >
            <svg-icon :name="name" :size="28" />
            <span class="icon-name">{{ getDisplayName(name) }}</span>
          </div>
        </div>
        <el-empty v-if="filteredIcons.length === 0" description="没有匹配的图标" />
      </el-scrollbar>
    </div>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { Search } from '@element-plus/icons-vue'
import SvgIcon from '@/components/SvgIcon/index.vue'

const props = defineProps<{
  modelValue: string
  visible: boolean
  mode?: 'light' | 'dark'  // 图标模式：亮色或暗色
}>()

const emit = defineEmits(['update:modelValue', 'update:visible'])

const dialogVisible = computed({
  get: () => props.visible,
  set: (value) => emit('update:visible', value)
})

const searchText = ref('')

// 通过 import.meta.glob 获取前台 SVG 图标文件列表
// 统一从 front/ 目录读取（亮色和暗色图标共存于同一目录）
const frontSvgModules = import.meta.glob('../../icons/svg/front/*.svg', { eager: true })

// 根据 mode 动态选择图标列表
// 亮色图标：文件名不含 -dark 后缀；暗色图标：文件名含 -dark 后缀
const iconNames = computed(() => {
  return Object.keys(frontSvgModules)
    .map(path => {
      const filename = path.split('/').pop()?.replace('.svg', '') || ''
      return { filename, fullPath: `front/${filename}` }
    })
    .filter(({ filename }) => {
      const isDark = filename.endsWith('-dark')
      return props.mode === 'dark' ? isDark : !isDark
    })
    .map(({ fullPath }) => fullPath)
    .sort()
})

const filteredIcons = computed(() => {
  if (!searchText.value) return iconNames.value
  const keyword = searchText.value.toLowerCase()
  return iconNames.value.filter(name => name.toLowerCase().includes(keyword))
})

const getDisplayName = (name: string) => {
  return name.replace('front/', '').replace('-dark', '')
}

const selectIcon = (iconName: string) => {
  emit('update:modelValue', iconName)
  emit('update:visible', false)
}
</script>

<style scoped>
.icon-container {
  padding: 20px;
}

.search-bar {
  margin-bottom: 20px;
}

.icon-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(110px, 1fr));
  gap: 12px;
  padding: 12px;
}

.icon-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 16px 8px;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
  border: 1px solid transparent;
}

.icon-item:hover {
  background-color: #ecf5ff;
  border-color: #409EFF;
}

.icon-item.active {
  background-color: #ecf5ff;
  border-color: #409EFF;
  color: #409EFF;
}

.icon-name {
  font-size: 12px;
  color: #606266;
  margin-top: 8px;
  word-break: break-all;
  text-align: center;
}

.icon-item.active .icon-name {
  color: #409EFF;
}
</style>
