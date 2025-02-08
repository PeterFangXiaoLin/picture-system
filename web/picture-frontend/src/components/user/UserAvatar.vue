<template>
  <div>
    <div class="user-info-head" @click="editCropper">
      <el-avatar
        :size="120"
        :src="loginUserStore.loginUser.userAvatar ?? defaultAvatar"
        class="avatar-img"
      />
    </div>

    <!-- 头像裁剪弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      title="修改头像"
      width="800px"
      destroy-on-close
      @opened="handleDialogOpened"
      @close="handleDialogClose"
    >
      <el-row>
        <el-col :xs="24" :md="12" :style="{ height: '350px' }">
          <vue-cropper
            ref="cropperRef"
            :img="cropperOptions.img"
            :info="true"
            :auto-crop="true"
            :auto-crop-width="200"
            :auto-crop-height="200"
            :fixed-box="true"
            :output-type="'png'"
            @real-time="handleRealTime"
            v-if="cropperVisible"
          />
        </el-col>
        <el-col :xs="24" :md="12" :style="{ height: '350px' }">
          <div class="avatar-upload-preview">
            <img :src="previews.url" :style="previews.img" />
          </div>
        </el-col>
      </el-row>

      <div class="cropper-control mt-4">
        <el-row :gutter="10" justify="center">
          <el-col :span="6">
            <el-upload
              action="#"
              :show-file-list="false"
              :before-upload="handleBeforeUpload"
              :http-request="uploadRequest"
            >
              <el-button>
                <el-icon><Upload /></el-icon>
                选择图片
              </el-button>
            </el-upload>
          </el-col>
          <el-col :span="12">
            <el-button-group>
              <el-button :icon="ZoomIn" @click="handleScale(1)" />
              <el-button :icon="ZoomOut" @click="handleScale(-1)" />
              <el-button :icon="RefreshLeft" @click="rotateLeft" />
              <el-button :icon="RefreshRight" @click="rotateRight" />
            </el-button-group>
          </el-col>
          <el-col :span="6">
            <el-button type="primary" @click="handleUpload">
              <el-icon><Check /></el-icon>
              提交
            </el-button>
          </el-col>
        </el-row>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { useLoginUserStore } from '@/stores/useLoginUserStore'
import { VueCropper } from 'vue-cropper'
import 'vue-cropper/dist/index.css'
import { Upload, ZoomIn, ZoomOut, RefreshLeft, RefreshRight, Check } from '@element-plus/icons-vue'
import { editUserInfoUsingPost } from '@/api/userController'

const loginUserStore = useLoginUserStore()
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

const dialogVisible = ref(false)
const cropperVisible = ref(false)
const cropperRef = ref()
const previews = ref({})

const cropperOptions = reactive({
  img: loginUserStore.loginUser.userAvatar ?? defaultAvatar,
  filename: 'avatar.png'
})

// 打开裁剪弹窗
const editCropper = () => {
  dialogVisible.value = true
}

// 弹窗打开回调
const handleDialogOpened = () => {
  cropperVisible.value = true
}

// 弹窗关闭回调
const handleDialogClose = () => {
  cropperVisible.value = false
  cropperOptions.img = loginUserStore.loginUser.userAvatar ?? defaultAvatar
}

// 实时预览
const handleRealTime = (data: any) => {
  previews.value = data
}

// 缩放
const handleScale = (num: number) => {
  cropperRef.value?.changeScale(num || 1)
}

// 向左旋转
const rotateLeft = () => {
  cropperRef.value?.rotateLeft()
}

// 向右旋转
const rotateRight = () => {
  cropperRef.value?.rotateRight()
}

// 上传前校验
const handleBeforeUpload = (file: File) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!')
    return false
  }

  const reader = new FileReader()
  reader.readAsDataURL(file)
  reader.onload = () => {
    cropperOptions.img = reader.result as string
    cropperOptions.filename = file.name
  }
  return false
}

// 覆盖默认的上传行为
const uploadRequest = () => {
  // 阻止默认上传
}

// 提交头像
const handleUpload = () => {
  cropperRef.value?.getCropBlob(async (blob: Blob) => {
    try {
      // 将blob转为base64
      const reader = new FileReader()
      reader.readAsDataURL(blob)
      reader.onload = async () => {
        const base64Url = reader.result as string
        // 调用接口更新头像
        const res = await editUserInfoUsingPost({
          id: loginUserStore.loginUser.id,
          userAvatar: base64Url
        })
        if (res.data.code === 0) {
          ElMessage.success('修改成功')
          dialogVisible.value = false
          // 重新获取用户信息
          loginUserStore.getLoginUser()
        } else {
          ElMessage.error('修改失败：' + res.data.message)
        }
      }
    } catch (error) {
      ElMessage.error('上传失败：' + error)
    }
  })
}
</script>

<style scoped>
.user-info-head {
  position: relative;
  display: inline-block;
  cursor: pointer;
  width: 120px;
  height: 120px;
  border-radius: 50%;
  overflow: hidden;
}

.user-info-head:hover::after {
  content: '+';
  position: absolute;
  left: 0;
  right: 0;
  top: 0;
  bottom: 0;
  color: #fff;
  background: rgba(0, 0, 0, 0.5);
  font-size: 24px;
  font-style: normal;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  cursor: pointer;
  display: flex;
  justify-content: center;
  align-items: center;
}

.avatar-img {
  width: 100%;
  height: 100%;
  display: block;
}

.avatar-upload-preview {
  position: relative;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 200px;
  height: 200px;
  border-radius: 50%;
  box-shadow: 0 0 4px #ccc;
  overflow: hidden;
}

.cropper-control {
  padding: 0 20px;
}
</style> 