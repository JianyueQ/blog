<template>
  <div class="bind-callback">
    <div class="callback-card" :class="{ success: isSuccess, fail: !isSuccess }">
      <el-icon v-if="isSuccess" class="callback-icon"><CircleCheck /></el-icon>
      <el-icon v-else class="callback-icon"><CircleClose /></el-icon>
      <h2 class="callback-title">{{ isSuccess ? '绑定成功' : '绑定失败' }}</h2>
      <p v-if="errorMsg" class="callback-msg">{{ errorMsg }}</p>
      <p class="callback-hint">此窗口将在 {{ countdown }} 秒后自动关闭</p>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const isSuccess = ref(false)
const errorMsg = ref('')
const countdown = ref(2)

onMounted(() => {
  const result = route.query.result as string
  isSuccess.value = result === 'success'
  errorMsg.value = (route.query.msg as string) || ''

  // 通过 BroadcastChannel 通知父窗口刷新绑定列表
  const channel = new BroadcastChannel('third_party_bind')
  channel.postMessage({
    type: 'bind_result',
    success: isSuccess.value,
    msg: errorMsg.value
  })
  channel.close()

  // 倒计时自动关闭
  const timer = setInterval(() => {
    countdown.value--
    if (countdown.value <= 0) {
      clearInterval(timer)
      window.close()
    }
  }, 1000)
})
</script>

<style scoped>
.bind-callback {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background: #f0f2f5;
}

.callback-card {
  text-align: center;
  padding: 48px 40px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  min-width: 360px;
}

.callback-icon {
  font-size: 64px;
  margin-bottom: 16px;
}

.success .callback-icon {
  color: #67c23a;
}

.fail .callback-icon {
  color: #f56c6c;
}

.callback-title {
  font-size: 22px;
  font-weight: 600;
  margin: 0 0 8px;
  color: #303133;
}

.callback-msg {
  font-size: 14px;
  color: #909399;
  margin: 0 0 16px;
}

.callback-hint {
  font-size: 13px;
  color: #c0c4cc;
  margin: 0;
}
</style>
