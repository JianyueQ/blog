import request from '@/utils/request'

// 获取验证码图片列表
export function getCodeImageListApi(params: any) {
  return request({
    url: '/code/image/list',
    method: 'get',
    params
  })
}

// 获取验证码图片详情
export function getCodeImageDetailApi(id: string) {
  return request({
    url: '/code/image/' + id,
    method: 'get'
  })
}

// 新增验证码图片
export function addCodeImageApi(data: any) {
  return request({
    url: '/code/image/add',
    method: 'post',
    data
  })
}

// 修改验证码图片
export function updateCodeImageApi(data: any) {
  return request({
    url: '/code/image/update',
    method: 'put',
    data
  })
}

// 删除验证码图片
export function deleteCodeImageApi(ids: string[] | string) {
  return request({
    url: '/code/image/delete/' + ids,
    method: 'delete'
  })
}

// 上传验证码图片
export function uploadCodeImageApi(data: FormData) {
  return request({
    url: '/code/image/upload',
    method: 'post',
    data,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}
