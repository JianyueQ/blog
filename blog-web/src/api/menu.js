import request from '@/utils/request'

/**
 * 获取前台导航菜单列表
 */
export function getNavListApi() {
    return request({
        url: '/api/frontMenu/navList',
        method: 'get'
    })
}
