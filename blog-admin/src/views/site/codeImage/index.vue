<template>
  <div class="app-container">
    <!-- 搜索表单 -->
    <div class="search-wrapper">
      <el-form ref="queryFormRef" :model="queryParams" :inline="true">
        <el-form-item label="文件名称" prop="filename">
          <el-input
            v-model="queryParams.filename"
            placeholder="请输入文件名称"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="来源" prop="source">
          <el-input
            v-model="queryParams.source"
            placeholder="请输入来源"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 操作按钮区域 -->
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <ButtonGroup>
            <el-button
              v-permission="['sys:codeImage:add']"
              type="primary"
              icon="Plus"
              @click="handleAdd"
            >新增</el-button>
            <el-button
              v-permission="['sys:codeImage:delete']"
              type="danger"
              icon="Delete"
              :disabled="selectedIds.length === 0"
              @click="handleBatchDelete"
            >批量删除</el-button>
          </ButtonGroup>
        </div>
      </template>

      <!-- 数据表格 -->
      <el-table
        v-loading="loading"
        :data="tableData"
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="图片" align="center" prop="url" width="120">
          <template #default="scope">
            <el-image
              :src="scope.row.url"
              style="width: 80px; height: 80px;"
              fit="cover"
              :preview-src-list="[scope.row.url]"
              preview-teleported
            />
          </template>
        </el-table-column>
        <el-table-column label="文件名称" align="center" prop="filename" show-overflow-tooltip />
        <el-table-column label="原始文件名" align="center" prop="originalFilename" show-overflow-tooltip />
        <el-table-column label="大小" align="center" prop="size" width="120">
          <template #default="scope">
            {{ formatFileSize(scope.row.size) }}
          </template>
        </el-table-column>
        <el-table-column label="扩展名" align="center" prop="ext" width="80" />
        <el-table-column label="来源" align="center" prop="source" width="120" />
        <el-table-column label="创建时间" align="center" prop="createTime" width="180" />
        <el-table-column label="操作" align="center" width="200" fixed="right">
          <template #default="scope">
            <el-button
              v-permission="['sys:codeImage:update']"
              type="primary"
              link
              icon="Edit"
              @click="handleUpdate(scope.row)"
            >修改</el-button>
            <el-button
              v-permission="['sys:codeImage:delete']"
              type="danger"
              link
              icon="Delete"
              @click="handleDelete(scope.row)"
            >删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页组件 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :page-sizes="[10, 20, 30, 50]"
          :total="total"
          :background="true"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 添加对话框 -->
    <el-dialog
      :title="dialog.title"
      v-model="dialog.visible"
      width="800px"
      append-to-body
      destroy-on-close
      class="custom-dialog"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="80px"
        class="custom-form"
      >
        <el-form-item label="来源" prop="source">
          <el-input
            v-model="form.source"
            placeholder="请输入来源"
            clearable
          />
        </el-form-item>
        <el-form-item label="图片上传" prop="fileList">
          <el-upload
            ref="uploadRef"
            v-model:file-list="fileList"
            action="#"
            list-type="picture-card"
            :auto-upload="false"
            :multiple="true"
            :on-preview="handlePreview"
            :on-remove="handleRemove"
            :before-upload="beforeUpload"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
          <div class="upload-tip">支持批量上传，只能上传jpg/png/gif文件</div>
        </el-form-item>
      </el-form>

      <!-- 图片预览对话框 -->
      <el-dialog v-model="previewVisible" title="预览图片" :append-to-body="true">
        <img :src="previewImageUrl" style="width: 100%; height: 500px; object-fit: contain;" />
      </el-dialog>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="cancel">取 消</el-button>
          <el-button type="primary" :loading="submitLoading" @click="submitForm">确 定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import type { UploadProps, UploadUserFile } from 'element-plus'
import {
  getCodeImageListApi,
  addCodeImageApi,
  updateCodeImageApi,
  deleteCodeImageApi,
  uploadCodeImageApi
} from '@/api/site/codeImage'

// 查询参数
const queryParams = reactive<any>({
  pageNum: 1,
  pageSize: 10,
  filename: null,
  source: null
})

const loading = ref(false)
const total = ref(0)
const tableData = ref<any>([])
const queryFormRef = ref<FormInstance>()
const formRef = ref<FormInstance>()
const submitLoading = ref(false)
const uploadRef = ref<any>(null)

// 文件列表
const fileList = ref<UploadUserFile[]>([])
const previewVisible = ref(false)
const previewImageUrl = ref('')

// 选中项数组
const selectedIds = ref<string[]>([])

// 弹窗控制
const dialog = reactive({
  title: '',
  visible: false,
  type: 'add'
})

// 表单数据
const form = reactive<any>({
  id: undefined,
  url: '',
  source: ''
})

// 表单校验规则
const rules = reactive<FormRules>({
  source: [
    { required: false, message: '请输入来源', trigger: 'blur' }
  ]
})

// 格式化文件大小
const formatFileSize = (size: number) => {
  if (!size) return '-'
  const units = ['B', 'KB', 'MB', 'GB']
  let unitIndex = 0
  let fileSize = size
  while (fileSize >= 1024 && unitIndex < units.length - 1) {
    fileSize /= 1024
    unitIndex++
  }
  return fileSize.toFixed(2) + ' ' + units[unitIndex]
}

// 获取验证码图片列表
const getList = async () => {
  loading.value = true
  try {
    const { data } = await getCodeImageListApi(queryParams)
    tableData.value = data.records
    total.value = data.total
  } catch (error) {
  }
  loading.value = false
}

// 表格选择项变化
const handleSelectionChange = (selection: any[]) => {
  selectedIds.value = selection.map(item => item.id)
}

// 处理图片预览
const handlePreview: UploadProps['onPreview'] = (uploadFile) => {
  previewImageUrl.value = uploadFile.url!
  previewVisible.value = true
}

// 处理图片删除
const handleRemove: UploadProps['onRemove'] = () => {
  // el-upload会自动处理fileList
}

// 上传前的校验
const beforeUpload: UploadProps['beforeUpload'] = (file) => {
  const isImage = /^image\/(jpeg|png|gif|webp)$/.test(file.type)
  if (!isImage) {
    ElMessage.error('只能上传jpg/png/gif/webp格式的图片!')
    return false
  }
  return true
}

// 批量删除
const handleBatchDelete = () => {
  if (selectedIds.value.length === 0) {
    ElMessage.warning('请选择要删除的记录')
    return
  }
  ElMessageBox.confirm(`是否确认删除 ${selectedIds.value.length} 条记录?`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteCodeImageApi(selectedIds.value.join(','))
      ElMessage.success('删除成功')
      getList()
    } catch (error) {
    }
  })
}

// 删除
const handleDelete = (row: any) => {
  ElMessageBox.confirm(`确定要删除该验证码图片吗？`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteCodeImageApi(row.id)
      ElMessage.success('删除成功')
      getList()
    } catch (error) {
    }
  })
}

// 搜索
const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

// 重置查询
const resetQuery = () => {
  queryFormRef.value?.resetFields()
  queryParams.filename = null
  queryParams.source = null
  handleQuery()
}

// 新增
const handleAdd = () => {
  dialog.type = 'add'
  dialog.title = '新增验证码图片'
  dialog.visible = true
  form.id = undefined
  form.source = ''
  fileList.value = []
}

// 修改
const handleUpdate = (row: any) => {
  dialog.type = 'edit'
  dialog.title = '修改验证码图片'
  dialog.visible = true
  Object.assign(form, row)
  fileList.value = []
}

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        if (dialog.type === 'add') {
          // 批量上传
          const rawFiles = fileList.value
            .filter(item => item.raw)
            .map(item => item.raw)
          if (rawFiles.length === 0) {
            ElMessage.warning('请选择要上传的图片')
            submitLoading.value = false
            return
          }
          for (const rawFile of rawFiles) {
            const formData = new FormData()
            formData.append('file', rawFile)
            if (form.source) {
              formData.append('source', form.source)
            }
            await uploadCodeImageApi(formData)
          }
          ElMessage.success(`成功上传 ${rawFiles.length} 张图片`)
        } else {
          await updateCodeImageApi(form)
          ElMessage.success('修改成功')
        }
        getList()
        dialog.visible = false
        fileList.value = []
      } catch (error) {
      } finally {
        submitLoading.value = false
      }
    }
  })
}

// 取消按钮
const cancel = () => {
  dialog.visible = false
  formRef.value?.resetFields()
  fileList.value = []
}

// 分页大小改变
const handleSizeChange = (val: number) => {
  queryParams.pageSize = val
  getList()
}

// 页码改变
const handleCurrentChange = (val: number) => {
  queryParams.pageNum = val
  getList()
}

// 初始化
onMounted(() => {
  getList()
})
</script>

<style scoped lang="scss">
.upload-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 8px;
}

:deep(.el-upload--picture-card) {
  --el-upload-picture-card-size: 100px;
}

:deep(.el-upload-list--picture-card) {
  --el-upload-list-picture-card-size: 100px;
}
</style>
