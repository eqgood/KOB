<template>
  <div class="window">
    <div class="game-body">
      <MenuView v-if="$store.state.router.router_name === 'menu'"/>
      <PkIndexView v-else-if = "$store.state.router.router_name === 'pk'"/>
      <RecordIndexView v-else-if="$store.state.router.router_name === 'record'"/>
      <RecordContentView v-else-if="$store.state.router.router_name === 'record_content'"/>
      <RanklistView v-else-if="$store.state.router.router_name === 'ranklist'"/>
      <UserBotsIndexView v-else-if="$store.state.router.router_name === 'user_bots'"/>
    </div>
  </div>  
</template>

<script>
import {useStore} from 'vuex';
import MenuView from './views/MenuView.vue';
import PkIndexView from './views/pk/PkIndexView.vue';
import RecordIndexView from './views/record/RecordIndexView.vue';
import RecordContentView from './views/record/RecordContentView.vue';
import RanklistView from './views/ranklist/RanklistIndexView.vue';
import UserBotsIndexView from './views/user/bots/UserBotsIndexView.vue';

export default {
  components: {
    MenuView,
    PkIndexView,
    RecordIndexView,
    RecordContentView,
    RanklistView,
    UserBotsIndexView
  },
  setup(){
    const store = useStore();
    const jwt_token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIzNWYzOGUwNDI0MTQ0Zjk5YjczYmM1OGQ5ZmY0MmNjMiIsInN1YiI6IjEiLCJpc3MiOiJzZyIsImlhdCI6MTc3MDE5OTc3MywiZXhwIjoxNzcxNDA5MzczfQ.embktDl63_x38DUiCcT5gkL_8VNCAMF26Vq_jAoGgiw";
      if(jwt_token){
          store.commit("updateToken",jwt_token);
          store.dispatch("getinfo",{
              success(){
                  store.commit("updatePullingInfo", false);
              },
              error(){
                  store.commit("updatePullingInfo", false);
              }
          })
      }else{
          store.commit("updatePullingInfo", false);
    }
  }
}
</script>

<style>
body{
  margin: 0;
}
div.game-body{
  background-image: url('./assets/images/background.png');
  background-size: cover;
  width: 100%;
  height: 100%;
}
div.window{
  width: 100vw;
  height: 100vh;
}
</style>
