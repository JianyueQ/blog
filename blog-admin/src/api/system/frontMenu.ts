import request from '@/utils/request'

/**
 * 获取前台菜单树
 */
export function getFrontMenuTreeApi() {
  return request({
    url: '/sys/frontMenu/tree',
    method: 'get'
  })
}

/**
 * 根据ID获取单个前台菜单
 */
export function getFrontMenuByIdApi(id: number) {
  return request({
    url: `/sys/frontMenu/${id}`,
    method: 'get'
  })
}

/**
 * 添加前台菜单
 */
export function createFrontMenuApi(data: any) {
  return request({
    url: '/sys/frontMenu',
    method: 'post',
    data
  })
}

/**
 * 修改前台菜单
 */
export function updateFrontMenuApi(data: any) {
  return request({
    url: '/sys/frontMenu',
    method: 'put',
    data
  })
}

/**
 * 删除前台菜单
 */
export function deleteFrontMenuApi(id: number) {
  return request({
    url: `/sys/frontMenu/${id}`,
    method: 'delete'
  })
}
