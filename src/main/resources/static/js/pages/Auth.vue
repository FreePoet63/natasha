<template>
    <v-container v-if="!profile">
        Необходимо авторизироваться через
        <a href="/login">Google</a>
    </v-container>
</template>

<script>
    export default {
       name: 'Auth',
    }
</script>

<style scoped>

</style>