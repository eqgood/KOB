import $ from 'jquery'

export default {
  state: {
    AcWingOS: "AcWingOS",
    id: "",
    username: "",
    photo: "",
    token: "",
    is_login: false,
    pulling_info: true, //是否正在拉取用户信息
  },
  getters: {
  },
  mutations: {
    updateUser(state, user) {
        state.id = user.id,
        state.username = user.username,
        state.photo = user.photo,
        state.is_login = user.is_login
    },
    updateToken(state, token) {
        state.token = token
    },
    logout(state) {
        state.id = "",
        state.username = "",
        state.photo = "",
        state.token = "",
        state.is_login = false
    },
    updatePullingInfo(state, pulling_info){
        state.pulling_info = pulling_info
    }
  },
  actions: {
    login(context, data){
        $.ajax({
            url: "https://app7811.acapp.acwing.com.cn/api/user/account/token/",
            type: "post",
            data: {
                username: data.username,
                password: data.password 
            },
            success(resp){
                if(resp.message === "success"){
                    localStorage.setItem("jwt_token", resp.token); //将token存储到本地存储中
                    context.commit("updateToken", resp.token);
                    data.success(resp);
                }else{
                    data.error(resp); //登陆失败后，调用回调函数
                }
            },
            error(resp){
                data.error(resp); 
            }
        });
    },
    getinfo(context, data){
        $.ajax({
            url: "https://app7811.acapp.acwing.com.cn/api/user/account/info/",
            type: "get",
            headers: {
                Authorization: "Bearer " + context.state.token,
            },
            success(resp){
                if(resp.message === "success"){
                    context.commit("updateUser", {
                        id: resp.id,
                        username: resp.username,
                        photo: resp.photo,
                        is_login: true
                    });
                    data.success(resp);
                }else{
                    data.error(resp);
                }
            },
            error(resp){
                data.error(resp);
            }
        });
    },

    logout(context){
        localStorage.removeItem("jwt_token");
        context.commit("logout");
    },


  },
  modules: {
  }
}
