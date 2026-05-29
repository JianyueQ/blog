<template>
  <div class="app-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>第三方登录配置</span>
          <el-button type="primary" @click="handleRefresh">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
        </div>
      </template>

      <el-table v-loading="loading" :data="dataList" style="width: 100%">
        <el-table-column label="图标" align="center" width="80">
          <template #default="scope">
            <div class="icon-preview">
              <img
                v-if="scope.row.icon && scope.row.icon.startsWith('http')"
                :src="scope.row.icon"
                :alt="scope.row.configName"
                class="icon-img"
              />
              <svg-icon
                v-else-if="scope.row.icon"
                :name="getSvgName(scope.row.icon)"
                :size="24"
              />
              <span v-else class="icon-empty">-</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="名称" align="center" prop="configName" width="120" />
        <el-table-column label="Key" align="center" prop="configKey" width="120">
          <template #default="scope">
            <el-tag type="info">{{ scope.row.configKey }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="AppId" align="center" width="120">
          <template #default="scope">
            <el-tag v-if="scope.row.appId" type="success" size="small">已配置</el-tag>
            <el-tag v-else type="danger" size="small">未配置</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="AppSecret" align="center" width="120">
          <template #default="scope">
            <el-tag v-if="scope.row.appSecret" type="success" size="small">已配置</el-tag>
            <el-tag v-else type="danger" size="small">未配置</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="回调地址" align="center" prop="redirectUrl" show-overflow-tooltip min-width="200" />
        <el-table-column label="状态" align="center" width="100">
          <template #default="scope">
            <el-switch
              v-model="scope.row.status"
              :active-value="1"
              :inactive-value="0"
              @change="handleStatusChange(scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column label="排序" align="center" prop="sort" width="80" />
        <el-table-column label="操作" align="center" fixed="right" width="100">
          <template #default="scope">
            <el-button type="primary" link icon="Edit" @click="handleUpdate(scope.row)">
              编辑
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 编辑对话框 -->
    <el-dialog v-model="open" title="编辑第三方登录配置" width="600px" append-to-body destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="110px">
        <el-form-item label="名称">
          <el-input v-model="form.configName" disabled />
        </el-form-item>
        <el-form-item label="Key">
          <el-input v-model="form.configKey" disabled />
        </el-form-item>
        <el-form-item label="AppId" prop="appId">
          <el-input v-model="form.appId" placeholder="请输入AppId" />
        </el-form-item>
        <el-form-item label="AppSecret" prop="appSecret">
          <el-input v-model="form.appSecret" type="password" placeholder="请输入AppSecret（留空则不修改）" show-password />
        </el-form-item>
        <el-form-item label="回调地址" prop="redirectUrl">
          <el-input v-model="form.redirectUrl" placeholder="回调地址（后台和前台共用）" />
        </el-form-item>
        <el-form-item label="图标" prop="icon">
          <div style="display: flex; align-items: center; gap: 12px; width: 100%;">
            <el-input v-model="form.icon" placeholder="图标URL 或 内置图标名（如 qq.svg）" />
            <!-- 图标预览 -->
            <div class="edit-icon-preview">
              <img
                v-if="form.icon && form.icon.startsWith('http')"
                :src="form.icon"
                alt="图标预览"
                class="icon-img"
              />
              <svg-icon
                v-else-if="form.icon"
                :name="getSvgName(form.icon)"
                :size="24"
              />
              <span v-else class="icon-empty">?</span>
            </div>
          </div>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" :max="9999" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="open = false">取 消</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ElMessage, ElMessageBox } from "element-plus"
import {
  listThirdPartyConfigApi,
  getThirdPartyConfigApi,
  updateThirdPartyConfigApi,
} from "@/api/system/thirdPartyConfig"

const loading = ref(true)
const dataList = ref<any[]>([])
const open = ref(false)
const form = ref<any>({})
const formRef = ref()

const rules = {
  appId: [{ required: true, message: "请输入AppId", trigger: "blur" }],
}

/** 从图标字段中提取 SVG 名称（去掉 .svg 后缀） */
const getSvgName = (icon: string) => {
  if (!icon) return ''
  return icon.replace(/\.svg$/, '')
}

const getList = () => {
  loading.value = true
  listThirdPartyConfigApi()
    .then((response: any) => {
      // 后端返回 IPage 分页对象，数据在 records 中
      dataList.value = response.data?.records || response.data || []
    })
    .finally(() => {
      loading.value = false
    })
}

const handleRefresh = () => {
  getList()
  ElMessage.success("刷新成功")
}

/** 状态开关切换时直接保存 */
const handleStatusChange = (row: any) => {
  const text = row.status === 1 ? '启用' : '禁用'
  ElMessageBox.confirm(`确认${text}「${row.configName}」吗？`, "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(() => {
      updateThirdPartyConfigApi({ id: row.id, status: row.status }).then(() => {
        ElMessage.success(`已${text}`)
      })
    })
    .catch(() => {
      // 取消操作，恢复状态
      row.status = row.status === 1 ? 0 : 1
    })
}

const handleUpdate = (row: any) => {
  getThirdPartyConfigApi(row.id).then((response: any) => {
    form.value = { ...response.data }
    open.value = true
  })
}

const submitForm = () => {
  formRef.value?.validate((valid: any) => {
    if (valid) {
      updateThirdPartyConfigApi(form.value).then(() => {
        ElMessage.success("修改成功")
        open.value = false
        getList()
      })
    }
  })
}

onMounted(() => {
  getList()
})
</script>

<style scoped lang="scss">
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.icon-preview {
  display: flex;
  align-items: center;
  justify-content: center;
}

.icon-img {
  width: 28px;
  height: 28px;
  border-radius: 4px;
  object-fit: contain;
}

.icon-empty {
  color: var(--el-text-color-placeholder);
  font-size: 18px;
}

.edit-icon-preview {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid var(--el-border-color);
  border-radius: 6px;
  flex-shrink: 0;
  background: var(--el-fill-color-light);
}
</style>
