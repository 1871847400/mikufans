export default {
  info(...data: any[]) {
    if (import.meta.env.DEV) {
      console.log('[Info]', ...data);
    }
  },
  warn(...data: any[]) {
    if (import.meta.env.DEV) {
      console.warn('[Warn]', ...data);
    }
  },
  error(...data: any[]) {
    if (import.meta.env.DEV) {
      console.error('[Error]', ...data);
    }
  },
  debug(...data: any[]) {
    if (import.meta.env.DEV) {
      console.debug('[Debug]', ...data)
    }
  }
}