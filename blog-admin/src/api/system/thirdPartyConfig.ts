import request from '@/utils/request'

/**
 * 获取第三方登录配置分页列表
 */
export function listThirdPartyConfigApi(params?: any) {
    return request({
        url: '/sys/thirdPartyConfig/list',
        method: 'get',
        params
    })
}

/**
 * 获取第三方登录配置详情
 */
export function getThirdPartyConfigApi(id: number) {
    return request({
        url: '/sys/thirdPartyConfig/' + id,
        method: 'get'
    })
}

/**
 * 修改第三方登录配置
 */
export function updateThirdPartyConfigApi(data: any) {
    return request({
        url: '/sys/thirdPartyConfig/update',
        method: 'put',
        data
    })
}

/**
 * 获取已启用的后台第三方登录配置列表（公开接口，无需登录）
 */
export function getAdminEnabledThirdPartyConfigApi() {
    return request({
        url: '/thirdPartyConfig/admin/enabledList',
        method: 'get'
    })
}

/**
 * 获取第三方授权地址
 */
export function getAuthRenderUrlApi(source: string, sourceType?: string) {
    return request({
        url: '/api/auth/render/' + source,
        method: 'get',
        params: { sourceType }
    })
}
