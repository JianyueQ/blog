<template>
  <div class="app-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>前台菜单管理</span>
          <ButtonGroup>
            <el-button
              v-permission="['sys:frontMenu:add']"
              type="primary"
              icon="Plus"
              @click="handleAdd(null)"
            >新增</el-button>
          </ButtonGroup>
        </div>
      </template>

      <!-- 表格区域 -->
      <el-table
        v-loading="loading"
        :data="menuList"
        row-key="id"
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
      >
        <el-table-column label="菜单名称" prop="title" show-overflow-tooltip min-width="150" />
        <el-table-column label="亮色图标" align="center" width="100">
          <template #default="{ row }">
            <svg-icon v-if="row.icon" :name="row.icon" :size="16" />
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="暗色图标" align="center" width="100">
          <template #default="{ row }">
            <svg-icon v-if="row.iconDark" :name="row.iconDark" :size="16" />
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="路由路径" prop="path" show-overflow-tooltip min-width="180" />
        <el-table-column label="排序" prop="sort" width="70" align="center" />
        <el-table-column label="状态" align="center" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="隐藏" align="center" width="80">
          <template #default="{ row }">
            <el-tag :type="row.hidden === 0 ? 'success' : 'info'" size="small">
              {{ row.hidden === 0 ? '显示' : '隐藏' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="外链" align="center" width="80">
          <template #default="{ row }">
            <el-tag :type="row.isExternal === 1 ? 'warning' : 'info'" size="small">
              {{ row.isExternal === 1 ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <!-- <el-table-column label="颜色类名" prop="colorClass" show-overflow-tooltip width="140" /> -->
        <el-table-column label="操作" width="220" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="success" link @click="handleAdd(row)" v-permission="['sys:frontMenu:add']">
              <el-icon><Plus /></el-icon>新增
            </el-button>
            <el-button type="primary" link @click="handleEdit(row)" v-permission="['sys:frontMenu:update']">
              <el-icon><Edit /></el-icon>修改
            </el-button>
            <el-button type="danger" link @click="handleDelete(row)" v-permission="['sys:frontMenu:delete']">
              <el-icon><Delete /></el-icon>删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 添加或修改菜单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '新增前台菜单' : '修改前台菜单'"
      width="650px"
      append-to-body
      destroy-on-close
    >
      <el-form
        ref="menuFormRef"
        :model="menuForm"
        :rules="rules"
        label-width="100px"
      >
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="上级菜单" prop="parentId">
              <el-tree-select
                v-model="menuForm.parentId"
                :data="menuOptions"
                :props="{ label: 'title', value: 'id' }"
                value-key="id"
                placeholder="选择上级菜单（不选则为一级菜单）"
                check-strictly
                :render-after-expand="false"
                clearable
                class="flex-grow"
              />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="菜单名称" prop="title">
              <el-input v-model="menuForm.title" placeholder="请输入菜单名称" />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="排序" prop="sort">
              <el-input-number v-model="menuForm.sort" :min="0" class="flex-grow" />
            </el-form-item>
          </el-col>

          <el-col :span="24">
            <el-form-item label="路由路径" prop="path">
              <el-input v-model="menuForm.path" :placeholder="menuForm.isExternal === 1 ? '请输入外链URL，如 https://...' : '请输入路由路径，如 /archive'" />
            </el-form-item>
          </el-col>

          <el-col :span="24">
            <el-form-item label="亮色图标" prop="icon">
              <el-input v-model="menuForm.icon" placeholder="点击选择亮色图标" readonly>
                <template #prefix>
                  <svg-icon v-if="menuForm.icon" :name="menuForm.icon" :size="14" />
                </template>
                <template #append>
                  <el-button @click="openIconSelect('light')">选择图标</el-button>
                </template>
              </el-input>
            </el-form-item>
          </el-col>

          <el-col :span="24">
            <el-form-item label="暗色图标" prop="iconDark">
              <el-input v-model="menuForm.iconDark" placeholder="点击选择暗色图标" readonly>
                <template #prefix>
                  <svg-icon v-if="menuForm.iconDark" :name="menuForm.iconDark" :size="14" />
                </template>
                <template #append>
                  <el-button @click="openIconSelect('dark')">选择图标</el-button>
                </template>
              </el-input>
            </el-form-item>
          </el-col>

          <!-- 颜色类名（已隐藏，改用 SVG 文件自身颜色） -->
          <!--
          <el-col :span="24">
            <el-form-item label="颜色类名" prop="colorClass">
              <el-input v-model="menuForm.colorClass" placeholder="CSS 类名，如 home-link" />
            </el-form-item>
          </el-col>
          -->

          <el-col :span="8">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="menuForm.status">
                <el-radio :value="1">启用</el-radio>
                <el-radio :value="0">禁用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>

          <el-col :span="8">
            <el-form-item label="隐藏" prop="hidden">
              <el-radio-group v-model="menuForm.hidden">
                <el-radio :value="0">显示</el-radio>
                <el-radio :value="1">隐藏</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>

          <el-col :span="8">
            <el-form-item label="是否外链" prop="isExternal">
              <el-radio-group v-model="menuForm.isExternal">
                <el-radio :value="0">否</el-radio>
                <el-radio :value="1">是</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取 消</el-button>
          <el-button type="primary" :loading="submitLoading" @click="submitForm">确 定</el-button>
        </div>
      </template>
    </el-dialog>
    <!-- 前台图标选择器（亮色） -->
    <front-icon-select
      v-model="menuForm.icon"
      v-model:visible="showIconSelect"
      mode="light"
    />
    <!-- 前台图标选择器（暗色） -->
    <front-icon-select
      v-model="menuForm.iconDark"
      v-model:visible="showDarkIconSelect"
      mode="dark"
    />
  </div>
</template>

<script setup lang="ts">
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance } from 'element-plus'
import FrontIconSelect from '@/components/FrontIconSelect/index.vue'
import {
  getFrontMenuTreeApi,
  getFrontMenuByIdApi,
  createFrontMenuApi,
  updateFrontMenuApi,
  deleteFrontMenuApi,
} from '@/api/system/frontMenu'

const loading = ref(false)
const dialogVisible = ref(false)
const dialogType = ref<'add' | 'edit'>('add')
const menuFormRef = ref<FormInstance>()
const showIconSelect = ref(false)
const showDarkIconSelect = ref(false)
const iconSelectMode = ref<'light' | 'dark'>('light')
const submitLoading = ref(false)

// 表单校验规则
const rules = {
  title: [
    { required: true, message: '请输入菜单名称', trigger: 'blur' }
  ],
  sort: [
    { required: true, message: '请输入排序', trigger: 'blur' }
  ],
  path: [
    { required: true, message: '请输入路由路径', trigger: 'blur' }
  ]
}

// 菜单表单对象
const menuForm = reactive({
  id: undefined as number | undefined,
  parentId: 0,
  title: '',
  path: '',
  icon: '',
  iconDark: '',  // 暗色模式图标
  sort: 0,
  hidden: 0,
  isExternal: 0,
  colorClass: '',
  status: 1
})

// 菜单列表数据
const menuList = ref<any[]>([])

// 菜单树选项
const menuOptions = ref<any[]>([])

// 查询菜单列表
const getList = async () => {
  loading.value = true
  try {
    const { data } = await getFrontMenuTreeApi()
    menuList.value = data
    menuOptions.value = [{ id: 0, title: '顶级菜单', children: data }]
  } catch (error) {
    console.error('获取前台菜单失败', error)
  } finally {
    loading.value = false
  }
}

// 提交表单
const submitForm = async () => {
  if (!menuFormRef.value) return

  await menuFormRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        const formData = { ...menuForm }
        if (dialogType.value === 'add') {
          await createFrontMenuApi(formData)
          ElMessage.success('新增成功')
        } else {
          await updateFrontMenuApi(formData)
          ElMessage.success('修改成功')
        }
        dialogVisible.value = false
        getList()
      } catch (error) {
        console.error('提交失败', error)
      } finally {
        submitLoading.value = false
      }
    }
  })
}

// 删除菜单
const handleDelete = (row: any) => {
  ElMessageBox.confirm(
    `确定要删除菜单"${row.title}"吗？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await deleteFrontMenuApi(row.id)
      ElMessage.success('删除成功')
      getList()
    } catch (error) {
      console.error('删除失败', error)
    }
  }).catch(() => {})
}

// 重置表单
const resetForm = () => {
  menuForm.id = undefined
  menuForm.parentId = 0
  menuForm.title = ''
  menuForm.path = ''
  menuForm.icon = ''
  menuForm.iconDark = ''
  menuForm.sort = 0
  menuForm.hidden = 0
  menuForm.isExternal = 0
  menuForm.colorClass = ''
  menuForm.status = 1
}

// 打开图标选择器
const openIconSelect = (mode: 'light' | 'dark') => {
  iconSelectMode.value = mode
  if (mode === 'dark') {
    showDarkIconSelect.value = true
  } else {
    showIconSelect.value = true
  }
}

// 新增菜单
const handleAdd = (row?: any) => {
  resetForm()
  menuOptions.value = [{ id: 0, title: '顶级菜单', children: menuList.value }]
  if (row) {
    menuForm.parentId = row.id
  }
  dialogType.value = 'add'
  dialogVisible.value = true
}

// 修改菜单
const handleEdit = (row: any) => {
  resetForm()
  menuOptions.value = [{ id: 0, title: '顶级菜单', children: menuList.value }]
  dialogType.value = 'edit'
  dialogVisible.value = true
  Object.assign(menuForm, {
    id: row.id,
    parentId: row.parentId,
    title: row.title,
    path: row.path,
    icon: row.icon || '',
    iconDark: row.iconDark || '',
    sort: row.sort,
    hidden: row.hidden,
    isExternal: row.isExternal,
    colorClass: row.colorClass || '',
    status: row.status
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
</style>
