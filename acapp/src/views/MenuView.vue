<template>
    <div class = "menu-field">
        <div class="menu">
            <div class="menu-item" @click="click_pk_handler()">对战</div>
            <div class="menu-item" @click="click_record_handler()">对局列表</div>
            <div class="menu-item" @click="click_rank_handler()">排行榜</div>
            <div class="menu-item" @click="click_bot_handler()">我的Bot</div>
        </div>
    </div>
</template>

<script> 
import {useStore} from 'vuex';
export default { 
    setup(){
        const store = useStore();
        store.commit("updateRouterName", "menu");

        const click_pk_handler = () => {
            store.commit("updateRouterName", "pk");

        };
        const click_record_handler = () => {
            store.commit("updateRouterName", "record");
        };
        const click_rank_handler = () => {
            store.commit("updateRouterName", "ranklist");
        };
        const click_bot_handler = () => {
            store.commit("updateRouterName", "user_bots");
        };
        return {
            click_pk_handler,
            click_record_handler,
            click_rank_handler,
            click_bot_handler
        };

    }
}
</script>

<style scoped>
div.menu-field{
    display: flex;
    justify-content: center;
    align-items: center;
    width: 100%;
    height: 100%;
}
div.menu{
    width: 25vw;
    height: 30vh;
    background-color: rgba(50, 50, 50, 0.5);
} 
div.menu-item{
    text-align: center;
    width: 100%;
    height: 7.5vh;
    color: white;
    font-size: 24px;
    line-height: 7.5vh;
    font-style: italic;
    font-weight: 600;
    cursor: pointer;
    user-select: none;
}
div.menu-item:hover{
    background-color: rgba(255, 255, 255, 0.5);
    color: black;
}
</style>