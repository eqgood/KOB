<template>
    <div class="result-board">
        <div class = "result-board-text" v-if="$store.state.pk.loser === 'all'"> 
            Draw          
        </div>
        <!-- 判断输赢 注意：== 代表直接比较，不比较类型，===会比较类型 -->
        <div class = "result-board-text" v-else-if="$store.state.pk.loser == 'A' && $store.state.pk.a_id == $store.state.user.id" > 
            Lose          
        </div>
        <div class = "result-board-text" v-else-if="$store.state.pk.loser == 'B'&& $store.state.pk.b_id == $store.state.user.id"> 
            Lose           
        </div>
        <div class = "result-board-text" v-else> 
            Win           
        </div>
        <div class="result-board-btn">
            <button type="button" class="btn btn-warning btn-md" @click="restart">
                再来！
            </button>
        </div>
    </div>
</template>

<script>
import {useStore} from 'vuex';
export default {
    setup(){
        const store = useStore();
        const restart =() =>{
            store.commit("updateLoser", "none");
            store.commit("updateStatus", "matching");
            store.commit("updateOpponent",{
                username: "我的对手",
                photo: "https://cdn.acwing.com/media/article/image/2022/08/09/1_1db2488f17-anonymous.png",
            })
        }
        return {
            restart,
        }
    }
}
</script>

<style scoped>
.result-board{
    width: 30vw;
    height: 35vh;
    background-color: rgba(50, 50, 50, 0.5);
    position: absolute;
    top: 30vh;
    left: 35vw;
}
.result-board-text{
    color: white;
    font-size: 50px;
    text-align: center;
    padding-top: 5vh;
    font-weight: 600;
    font-style: italic;
}
.result-board-btn{
    text-align: center;
    padding-top: 8vh;
} 
</style>