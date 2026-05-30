<template>
  <div class="app-container">
    <el-row :gutter="20">
      <!-- 个人信息卡片 -->
      <el-col :span="8">
        <el-card class="profile-card" :body-style="{ padding: '0px' }">
          <div class="profile-header">
            <div class="header-backdrop"></div>
            <div class="header-content">
              <div class="avatar-wrapper">
                <el-avatar 
                  :size="100" 
                  :src="userInfo.sysUser.avatar" 
                  class="profile-avatar"
                />
                <div class="avatar-upload-overlay" @click="triggerAvatarUpload">
                  <el-icon><Camera /></el-icon>
                  <span>更换头像</span>
                </div>
              </div>
              <h2 class="profile-name">{{ userInfo.sysUser.nickname }}</h2>
              <input 
                ref="avatarInputRef" 
                type="file" 
                accept="image/*" 
                style="display: none" 
                @change="handleAvatarChange"
              />
            </div>
          </div>
          <div class="profile-info">
            <ul class="profile-list">
              <li>
                <div class="info-label">
                  <el-icon><User /></el-icon>
                  <span>用户名称</span>
                </div>
                <div class="info-content">{{ userInfo.sysUser.username }}</div>
              </li>
              <li>
                <div class="info-label">
                  <el-icon><Iphone /></el-icon>
                  <span>手机号码</span>
                </div>
                <div class="info-content">{{ userInfo.sysUser.mobile || '未设置' }}</div>
              </li>
              <li>
                <div class="info-label">
                  <el-icon><Message /></el-icon>
                  <span>用户邮箱</span>
                </div>
                <div class="info-content">{{ userInfo.sysUser.email || '未设置' }}</div>
              </li>
              <li>
                <div class="info-label">
                  <el-icon><UserFilled /></el-icon>
                  <span>所属角色</span>
                </div>
                <div class="info-content">{{ userInfo.roles.join(',') || '未设置' }}</div>
              </li>
              <li>
                <div class="info-label">
                  <el-icon><Calendar /></el-icon>
                  <span>创建日期</span>
                </div>
                <div class="info-content">{{ userInfo.sysUser.createTime }}</div>
              </li>
            </ul>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧标签页 -->
      <el-col :span="16">
        <el-card class="tab-card">
          <el-tabs v-model="activeTab" class="profile-tabs">
            <!-- 基本资料 -->
            <el-tab-pane label="基本资料" name="basic">
              <el-form
                ref="userFormRef"
                :model="userForm"
                :rules="userRules"
                label-width="100px"
                class="profile-form"
              >
                <el-form-item label="用户昵称" prop="nickname">
                  <el-input 
                    v-model="userForm.nickname" 
                    maxlength="30"
                    placeholder="请输入用户昵称"
                  />
                </el-form-item>
                <el-form-item label="手机号码" prop="mobile">
                  <el-input 
                    v-model="userForm.mobile" 
                    maxlength="11"
                    placeholder="请输入手机号码"
                  />
                </el-form-item>
                <el-form-item label="邮箱" prop="email">
                  <el-input 
                    v-model="userForm.email" 
                    maxlength="50"
                    placeholder="请输入邮箱地址"
                  />
                </el-form-item>
                <el-form-item label="性别">
                  <el-radio-group v-model="userForm.sex">
                    <el-radio :value="1">男</el-radio>
                    <el-radio :value="2">女</el-radio>
                  </el-radio-group>
                </el-form-item>
                <el-form-item>
                  <el-button 
                    v-permission="['sys:profile:edit']"
                    type="primary" 
                    @click="submitUserForm"
                    :loading="submitLoading"
                  >
                    <el-icon><Check /></el-icon>
                    保存更改
                  </el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>

            <!-- 修改密码 -->
            <el-tab-pane label="修改密码" name="password">
              <el-form
                ref="pwdFormRef"
                :model="pwdForm"
                :rules="pwdRules"
                label-width="100px"
                class="profile-form"
              >
                <el-form-item label="旧密码" prop="oldPassword">
                  <el-input
                    v-model="pwdForm.oldPassword"
                    type="password"
                    placeholder="请输入旧密码"
                    show-password
                  />
                </el-form-item>
                <el-form-item label="新密码" prop="newPassword">
                  <el-input
                    v-model="pwdForm.newPassword"
                    type="password"
                    placeholder="请输入新密码"
                    show-password
                  />
                </el-form-item>
                <el-form-item label="确认密码" prop="confirmPassword">
                  <el-input
                    v-model="pwdForm.confirmPassword"
                    type="password"
                    placeholder="请确认新密码"
                    show-password
                  />
                </el-form-item>
                <el-form-item>
                  <el-button 
                    v-permission="['sys:profile:password']"
                    type="primary" 
                    @click="submitPwdForm"
                    :loading="pwdLoading"
                  >
                    <el-icon><Key /></el-icon>
                    修改密码
                  </el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>

            <!-- 账号绑定 -->
            <el-tab-pane label="账号绑定" name="binding">
              <div class="binding-container">
                <div class="binding-header">
                  <span>绑定第三方账号后，可使用第三方快捷登录</span>
                </div>
                <div class="binding-list" v-loading="bindingLoading">
                  <div 
                    v-for="item in thirdPartyList" 
                    :key="item.configKey"
                    class="binding-item"
                  >
                    <div class="binding-item-left">
                      <div class="binding-icon">
                        <img
                          v-if="item.icon && item.icon.startsWith('http')"
                          :src="item.icon"
                          :alt="item.configName"
                          class="binding-icon-img"
                        />
                        <svg-icon
                          v-else-if="item.icon"
                          :name="item.icon.replace(/\.svg$/, '')"
                          :size="24"
                        />
                      </div>
                      <div class="binding-info">
                        <div class="binding-name">{{ item.configName }}</div>
                        <div class="binding-status" v-if="item.bound">
                          <el-tag type="success" size="small">已绑定</el-tag>
                          <span class="binding-nickname">{{ item.thirdPartyNickname }}</span>
                        </div>
                        <div class="binding-status" v-else>
                          <el-tag type="info" size="small">未绑定</el-tag>
                        </div>
                      </div>
                    </div>
                    <div class="binding-actions">
                      <el-button 
                        v-if="item.bound" 
                        type="danger" 
                        link 
                        @click="handleUnbind(item)"
                      >
                        解绑
                      </el-button>
                      <el-button 
                        v-else 
                        type="primary" 
                        link 
                        @click="handleBind(item)"
                      >
                        绑定
                      </el-button>
                    </div>
                  </div>
                </div>
              </div>
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script lang="ts" setup>
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserProfileApi, updateUserProfileApi, updateUserPwdApi } from '@/api/system/user'
import { uploadApi } from '@/api/file'
import { 
  getAdminEnabledThirdPartyConfigApi, 
  getAuthRenderUrlApi,
  listUserThirdPartyApi,
  unbindThirdPartyApi 
} from '@/api/system/thirdPartyConfig'

const activeTab = ref('basic')
const userFormRef = ref()
const pwdFormRef = ref()
const avatarInputRef = ref()
const avatarUploading = ref(false)

// 用户信息
const userInfo = ref<any>({
  sysUser: {},
  roles: []
})

// 表单数据
const userForm = reactive({
  nickname: '',
  mobile: '',
  email: '',
  sex: 1
})

const pwdForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 表单校验规则
const userRules = reactive<any>({
  nickname: [{ required: true, message: '请输入用户昵称', trigger: 'blur' }],
  email: [
    { required: false, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  mobile: [
    { required: true, message: '请输入手机号码', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ]
})

const pwdRules = reactive<any>({
  oldPassword: [{ required: true, message: '请输入旧密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule: any, value: string, callback: Function) => {
        if (value !== pwdForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
})

// 添加loading状态
const submitLoading = ref(false)
const pwdLoading = ref(false)
const bindingLoading = ref(false)

// 第三方绑定相关
const thirdPartyList = ref<any[]>([])
const boundList = ref<any[]>([])

// 获取用户信息
const getUser = async () => {
  try {
    const { data } = await getUserProfileApi()
    Object.assign(userInfo.value, data)
    Object.assign(userForm, {
      id: data.sysUser.id,
      nickname: data.sysUser.nickname,
      mobile: data.sysUser.mobile,
      email: data.sysUser.email,
      sex: data.sysUser.sex
    })
  } catch (error) {
    console.error('获取用户信息失败:', error)
  }
}

// 提交用户表单
const submitUserForm = async () => {
  try {
    submitLoading.value = true
    await userFormRef.value.validate()
    await updateUserProfileApi(userForm)
    ElMessage.success('修改成功')
    getUser()
  } catch (error) {
    console.error('提交失败:', error)
  } finally {
    submitLoading.value = false
  }
}

// 提交密码表单
const submitPwdForm = async () => {
  try {
    pwdLoading.value = true
    await pwdFormRef.value.validate()
    await updateUserPwdApi(pwdForm.oldPassword, pwdForm.newPassword)
    ElMessage.success('修改成功')
    Object.assign(pwdForm, {
      oldPassword: '',
      newPassword: '',
      confirmPassword: ''
    })
  } catch (error) {
    console.error('修改密码失败:', error)
  } finally {
    pwdLoading.value = false
  }
}

// 触发头像上传
const triggerAvatarUpload = () => {
  avatarInputRef.value?.click()
}

// 处理头像文件选择
const handleAvatarChange = async (e: any) => {
  const file = e.target.files[0]
  if (!file) return

  // 验证文件类型
  if (!file.type.startsWith('image/')) {
    ElMessage.warning('请上传图片文件')
    return
  }

  // 验证文件大小 (5MB)
  if (file.size > 5 * 1024 * 1024) {
    ElMessage.warning('图片大小不能超过 5MB')
    return
  }

  avatarUploading.value = true
  const formData = new FormData()
  formData.append('file', file)

  try {
    const res: any = await uploadApi(formData, 'avatar', 60000)
    if (res.code === 200 && res.data) {
      // 更新用户头像
      const updateData = {
        id: userInfo.value.sysUser.id,
        avatar: res.data
      }
      await updateUserProfileApi(updateData)
      ElMessage.success('头像上传成功')
      // 刷新用户信息
      await getUser()
    } else {
      ElMessage.error(res.message || '上传失败')
    }
  } catch (error: any) {
    console.error('上传失败:', error)
    ElMessage.error(error.message || '上传失败')
  } finally {
    avatarUploading.value = false
    // 清空 input
    if (avatarInputRef.value) {
      avatarInputRef.value.value = ''
    }
  }
}

onMounted(() => {
  getUser()
  getThirdPartyList()
})

// 获取第三方配置列表
const getThirdPartyList = async () => {
  try {
    bindingLoading.value = true
    // 获取所有后台第三方配置
    const configRes = await getAdminEnabledThirdPartyConfigApi()
    const configs = configRes.data || []
    
    // 获取当前用户已绑定的列表
    const boundRes = await listUserThirdPartyApi()
    boundList.value = boundRes.data || []
    
    // 合并数据
    thirdPartyList.value = configs.map((config: any) => {
      // configKey 是 github_admin，提取 source
      const source = config.configKey.replace(/_admin$/, '')
      const bound = boundList.value.find((b: any) => b.thirdPartyType === source)
      return {
        ...config,
        source,
        bound: !!bound,
        thirdPartyNickname: bound?.thirdPartyNickname,
        thirdPartyAvatar: bound?.thirdPartyAvatar
      }
    })
  } catch (error) {
    console.error('获取第三方配置失败:', error)
  } finally {
    bindingLoading.value = false
  }
}

// 绑定第三方账号
const handleBind = async (item: any) => {
  try {
    // 获取授权地址，purpose=bind 标识绑定流程
    const res = await getAuthRenderUrlApi(item.source, 'admin', 'bind')
    if (res.data) {
      // 打开授权窗口
      const authWindow = window.open(res.data, '_blank', 'width=800,height=600')
      
      // 监听绑定结果
      const channel = new BroadcastChannel('third_party_bind')
      channel.onmessage = (event) => {
        channel.close()
        if (event.data.success) {
          ElMessage.success('绑定成功')
        } else {
          ElMessage.error(event.data.msg || '绑定失败')
        }
        getThirdPartyList()
      }

      // 窗口关闭检测（用户手动关闭未授权）
      const checkClosed = setInterval(() => {
        if (authWindow?.closed) {
          clearInterval(checkClosed)
          channel.close()
        }
      }, 500)
    }
  } catch (error) {
    ElMessage.error('获取授权地址失败')
  }
}

// 解绑第三方账号
const handleUnbind = (item: any) => {
  ElMessageBox.confirm(`确认解绑「${item.configName}」账号吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await unbindThirdPartyApi(item.source)
      ElMessage.success('解绑成功')
      getThirdPartyList()
    } catch (error) {
      ElMessage.error('解绑失败')
    }
  }).catch(() => {})
}
</script>

<style lang="scss" scoped>


.profile-card {
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s;
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }
}

.profile-header {
  position: relative;
  height: 200px;
  
  .header-backdrop {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: linear-gradient(135deg, #1890ff 0%, #36cfc9 100%);
  }
  
  .header-content {
    position: relative;
    height: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    color: white;
    padding: 20px;
    
    .profile-avatar {
      border: 4px solid rgba(255, 255, 255, 0.8);
      box-shadow: 0 2px 10px rgba(0, 0, 0, 0.15);
    }
          
    .avatar-wrapper {
      position: relative;
      display: inline-block;
      width: 100px;
      height: 100px;
      
      .avatar-upload-overlay {
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        z-index: 10;
        background: rgba(0, 0, 0, 0.5);
        border-radius: 50%;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        color: white;
        cursor: pointer;
        opacity: 0;
        transition: opacity 0.3s;
              
        .el-icon {
          font-size: 24px;
          margin-bottom: 4px;
        }
              
        span {
          font-size: 12px;
        }
      }
            
      &:hover .avatar-upload-overlay {
        opacity: 1;
      }
    }
    
    .profile-name {
      margin: 15px 0 10px;
      font-size: 24px;
      font-weight: 600;
    }
  }
}

.profile-info {
  padding: 20px;
  
  .profile-list {
    padding: 0;
    margin: 0;
    list-style: none;
    
    li {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 15px 0;
      border-bottom: 1px solid #f0f0f0;
      
      &:last-child {
        border-bottom: none;
      }
      
      .info-label {
        display: flex;
        align-items: center;
        color: #666;
        
        .el-icon {
          margin-right: 8px;
          font-size: 16px;
        }
      }
      
      .info-content {
        color: #333;
        font-weight: 500;
      }
    }
  }
}

.tab-card {
  border-radius: 8px;
  
  :deep(.el-tabs__nav-wrap) {
    padding: 0 20px;
  }
}

.profile-form {
  max-width: 500px;
  margin: 20px auto;
  padding: 20px;
  
  .el-form-item:last-child {
    margin-bottom: 0;
    text-align: center;
    
    .el-button {
      width: 120px;
    }
  }
}

.profile-tabs {
  :deep(.el-tabs__item) {
    font-size: 15px;
    
    &.is-active {
      font-weight: 600;
    }
  }
}

// 账号绑定样式
.binding-container {
  padding: 20px;
  
  .binding-header {
    margin-bottom: 20px;
    color: #666;
    font-size: 14px;
  }
  
  .binding-list {
    .binding-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 16px;
      margin-bottom: 12px;
      background: #f5f7fa;
      border-radius: 8px;
      transition: all 0.3s;
      
      &:hover {
        background: #e8f4ff;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
      }
      
      .binding-item-left {
        display: flex;
        align-items: center;
        gap: 16px;
        
        .binding-icon {
          width: 48px;
          height: 48px;
          display: flex;
          align-items: center;
          justify-content: center;
          background: white;
          border-radius: 8px;
          box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
          
          .binding-icon-img {
            width: 32px;
            height: 32px;
            object-fit: contain;
          }
        }
        
        .binding-info {
          .binding-name {
            font-size: 16px;
            font-weight: 500;
            color: #333;
            margin-bottom: 6px;
          }
          
          .binding-status {
            display: flex;
            align-items: center;
            gap: 8px;
            
            .binding-nickname {
              color: #666;
              font-size: 13px;
            }
          }
        }
      }
      
      .binding-actions {
        .el-button {
          font-size: 14px;
        }
      }
    }
  }
}
</style>