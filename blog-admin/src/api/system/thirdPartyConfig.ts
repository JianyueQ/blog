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
export function getAuthRenderUrlApi(source: string, sourceType?: string, purpose?: string) {
    return request({
        url: '/api/auth/render/' + source,
        method: 'get',
        params: { sourceType, purpose }
    })
}

/**
 * 获取当前用户绑定的第三方账号列表
 */
export function listUserThirdPartyApi() {
    return request({
        url: '/sys/userThirdParty/list',
        method: 'get'
    })
}

/**
 * 解绑第三方账号
 */
export function unbindThirdPartyApi(type: string) {
    return request({
        url: '/sys/userThirdParty/unbind/' + type,
        method: 'delete'
    })
}

/**
 * 绑定第三方账号（通过授权码）
 */
export function bindThirdPartyApi(type: string, code: string) {
    return request({
        url: '/sys/userThirdParty/bind/' + type,
        method: 'post',
        params: { code }
    })
}
