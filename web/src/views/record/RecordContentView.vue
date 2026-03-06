<template>
    <PlayGround/>
    <div class="user-color" v-if = "parseInt($store.state.user.id) === parseInt($store.state.pk.b_id)">你是红色方</div>
    <div class="user-color" v-if = "parseInt($store.state.user.id) === parseInt($store.state.pk.a_id)">你是蓝色方</div>
</template>

<script>
import PlayGround from '../../components/PlayGround.vue'

export default {
    components: {
        PlayGround,
    },
    setup() {
    }
}
</script>

<style scoped>
div.user-color{
    text-align: center;
    color:white;
    font-size: 30px;
    font-weight: 600;
}
</style>