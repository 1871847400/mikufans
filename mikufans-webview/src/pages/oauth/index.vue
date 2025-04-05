<template>
  <div>

  </div>
</template>

<script setup lang="ts">
import loginApi from '@/apis/security/login';
const router = useRouter()
const type = useRouteParams('type', '');
const code = useRouteQuery('code', '');
(async function() {
  try {
    await loginApi.oauthLogin({
      oauthType: type.value,
      oauthCode: code.value
    })
    router.replace('/')
  } catch {
    router.replace('/')
  }
})();
</script>