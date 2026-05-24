interface Settings {
  title: string;
  shortTitle: string;
  description: string;
  repository: string;
}

const settings: Settings = {
  title: import.meta.env.VITE_APP || '博客管理系统',
  shortTitle: import.meta.env.VITE_APP_TITLE || '博客管理系统',
  description: import.meta.env.VITE_APP_DESCRIPTION || '一个现代化的博客管理系统',
  repository: import.meta.env.VITE_APP_REPOSITORY || 'https://github.com'
}

export default settings 