<template>
    <ContentField>
        我的Bots
    </ContentField>
</template>

<script>
import ContentField from '../../../components/ContentField.vue'
import $ from 'jquery'
import {useStore} from 'vuex'


export default {
    components: {
        ContentField
    },
    setup(){
        const store  = useStore();
        // $.ajax({
        //     url: "http://127.0.0.1:3000/user/bot/add/",
        //     type: "post",
        //     data:{
        //         title: "Bot的title",
        //         description: "this is A Bot",
        //         content: "Bot的代码",
        //     },
        //     headers:{
        //         Authorization: "Bearer " + store.state.user.token
        //     },
        //     success(resp){
        //         console.log(resp);
        //     },
        //     error(resp){ 
        //         console.log(resp);
        //     }
        // })
        // $.ajax({
        //     url: "http://127.0.0.1:3000/user/bot/remove/",
        //     type: "post",
        //     data:{
        //         bot_id: 4,
        //     },
        //     headers:{
        //         Authorization: "Bearer " + store.state.user.token
        //     },
        //     success(resp){
        //         console.log(resp);
        //     },
        //     error(resp){ 
        //         console.log(resp);
        //     }
        // })

        // $.ajax({
        //     url: "http://127.0.0.1:3000/user/bot/update/",
        //     type: "post",
        //     data:{
        //         bot_id: 10,
        //         title: "Bot2",
        //         description: "this is Bot2",
        //         content: "Bot2的代码"
        //     },
        //     headers:{
        //         Authorization: "Bearer " + store.state.user.token
        //     },
        //     success(resp){
        //         console.log(resp);
        //     },
        //     error(resp){ 
        //         console.log(resp);
        //     }
        // })

        $.ajax({
            url: "http://127.0.0.1:3000/user/bot/getlist/",
            type: "get",
            headers:{
                Authorization: "Bearer " + store.state.user.token
            },
            success(resp){
                console.log(resp);
            },
            error(resp){ 
                console.log(resp);
            }
        })
    }
}
</script>

<style scoped>

</style>