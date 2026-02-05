<template>
    <div class="matchground-field">
        <div class = "matchground">
            <div class = "matchground-head">
                <div>
                    <div class="user_photo">
                        <img :src="$store.state.user.photo">
                    </div>
                    <div class="user_username">
                        {{$store.state.user.username}}
                    </div>
                </div>
                <select v-model = "select_bot" class="form-select" >
                    <option value = "-1" selected>亲自上阵</option>
                    <option v-for="bot in bots" :key="bot.id" :value="bot.id">{{bot.title}}</option>
                </select>
                <div>
                    <div class="user_photo">
                        <img :src="$store.state.pk.opponent_photo" >
                    </div>
                    <div class="user_username">
                        {{$store.state.pk.opponent_username}}
                    </div>
                </div>
            </div>

            <div class= "start-match-btn" >
                <button type="button"  @click="click_match_btn">{{match_btn_info}}</button>
            </div>
        </div>
    </div>
</template>

<script>
import {ref} from 'vue';
import { useStore } from 'vuex';
import $ from 'jquery';

export default{
    setup(){
        let match_btn_info = ref("开始匹配");
        const store = useStore();
        let bots = ref([]);
        let select_bot = ref(-1);
        
        const click_match_btn = () =>{
            if(match_btn_info.value === "开始匹配"){
                match_btn_info.value = "取消";
                store.state.pk.socket.send(JSON.stringify({
                    event: "start-matching",
                    bot_id: select_bot.value,
                }));
            }else{
                match_btn_info.value = "开始匹配";
                store.state.pk.socket.send(JSON.stringify({
                    event: "stop-matching",
                }));
            }
        }
        const refresh_bots = () => {
            $.ajax({
                url: "https://app7811.acapp.acwing.com.cn/api/user/bot/getlist/",
                type: "GET",
                headers:{
                    Authorization: "Bearer " + store.state.user.token
                },
                success(resp){
                    bots.value = resp;
                    console.log(resp);
                },
                error(err){
                    console.log(err);
                }
            })
        }
        refresh_bots(); //从云端动态获取bot列表
        return{
            match_btn_info,
            click_match_btn,
            bots,
            select_bot,
        }
    }

}
</script>

<style scoped>
    div.matchground-field{ 
        display: flex;
        justify-content: center;
        align-items: center;
        width:100%;
        height:100%;

    }
    div.matchground{
        width: 55%;
        height: 60%;
        background-color: rgb(50, 50, 50, 0.5);
        border-radius: 5px;
        display:flex;
        flex-direction: column;
        justify-content: space-around;
    }
    div.matchground-head{
        display:flex;
        justify-content: space-evenly;

    }
    div.user_photo{
        text-align: center;
        margin-top: 10vh;
    }
    div.user_photo img{
        border-radius: 50%;
        width:10vh;
    }
    div.user_username{
        text-align: center;
        font-size: 20px;
        font-weight:600;
        color:white;
        padding-top:2vh;
    }
    select.form-select{ 
        display:flex;
        justify-content: center;
        align-items: center;
        height: 4.5vh;
        width: 8vw;
        margin-top: 19vh;
        border-radius: 5px;
        background-color: rgb(255, 255, 255, 0.5);
        color: black;
        font-size: 15px;
        font-weight: 600;
        border: none;
        outline: none;

    }
    div.start-match-btn{ 
        text-align: center; 
    }
    div.start-match-btn button{ 
        width: 10vw;
        height: 4vh;
        border-radius: 5px;
        background-color: #FFC310;
        border: none;
        font-size: 15px;
        font-weight: 600;
        cursor: pointer;
    }
</style>