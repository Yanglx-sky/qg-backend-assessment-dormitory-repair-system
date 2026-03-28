// 引入axios库
// 注意：需要在HTML中先引入axios库，然后再引入此文件

// 创建axios实例
const axiosInstance = axios.create({
    baseURL: '', // 基础URL
    timeout: 10000, // 请求超时时间
    headers: {
        'Content-Type': 'application/json'
    }
});

// 请求拦截器
axiosInstance.interceptors.request.use(
    config => {
        // 从localStorage中获取access token
        const accessToken = localStorage.getItem('accessToken');
        if (accessToken) {
            // 将token添加到请求头
            config.headers.Authorization = `Bearer ${accessToken}`;
        }
        return config;
    },
    error => {
        // 处理请求错误
        return Promise.reject(error);
    }
);

// 响应拦截器
axiosInstance.interceptors.response.use(
    response => {
        // 直接返回响应数据
        return response.data;
    },
    error => {
        // 处理响应错误
        if (error.response) {
            // 服务器返回错误状态码
            switch (error.response.status) {
                case 401:
                    // 未授权，尝试刷新token
                    return refreshAccessToken().then(newAccessToken => {
                        // 重新发起请求
                        const config = error.config;
                        config.headers.Authorization = `Bearer ${newAccessToken}`;
                        return axiosInstance(config);
                    }).catch(() => {
                        // 刷新token失败，跳转到登录页面
                        localStorage.removeItem('user');
                        localStorage.removeItem('accessToken');
                        localStorage.removeItem('refreshToken');
                        window.location.href = 'login.html';
                        return Promise.reject(error);
                    });
                case 403:
                    // 禁止访问，跳转到登录页面
                    localStorage.removeItem('user');
                    localStorage.removeItem('accessToken');
                    localStorage.removeItem('refreshToken');
                    window.location.href = 'login.html';
                    break;
                default:
                    // 其他错误，直接返回错误信息
                    return Promise.reject(error.response.data);
            }
        } else if (error.request) {
            // 请求已发送但没有收到响应
            return Promise.reject({ success: false, message: '网络错误，请检查网络连接' });
        } else {
            // 请求配置错误
            return Promise.reject({ success: false, message: error.message });
        }
    }
);

// 刷新access token的函数
function refreshAccessToken() {
    const refreshToken = localStorage.getItem('refreshToken');
    if (!refreshToken) {
        return Promise.reject('No refresh token found');
    }
    return axios.post('/users/refresh-token', { refreshToken: refreshToken })
        .then(response => {
            if (response.data.success) {
                // 更新localStorage中的access token
                localStorage.setItem('accessToken', response.data.accessToken);
                return response.data.accessToken;
            } else {
                return Promise.reject('Access token refresh failed');
            }
        });
}

// 导出axios实例
window.axiosInstance = axiosInstance;