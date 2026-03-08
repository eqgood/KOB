<template>
    <ContentField v-if = "!$store.state.user.pulling_info">
        <div class="row justify-content-center">
            <div class="col-3">
                <form @submit.prevent="login">
                    <div class="mb-3">
                        <label for="username" class="form-label">用户名</label>
                        <input  v-model="username" type="text" class="form-control" id="username" placeholder="请输入用户名">
                    </div>
                    <div class="mb-3">
                        <label for="password" class="form-label">密码</label>
                        <input v-model="password" type="password" class="form-control" id="password" placeholder="请输入密码">
                    </div>
                    <div class="mb-3 d-flex justify-content-end">
                        <button 
                            type="button" 
                            class="forget-password-btn" 
                            @click="goToForgetPassword"
                        >
                            忘记密码？
                        </button>
                    </div>
                    <div v-if="error_message" class="error_message">{{ error_message }}</div>
                    <button type="submit" class="btn btn-primary w-100">登录</button>
                </form>
                <div style="text-align: center; margin-top: 20px; cursor: pointer;" @click="acwing_login">
                    <img width="30px" src="https://cdn.acwing.com/media/article/image/2022/09/06/1_32f001fd2d-acwing_logo.png" alt="">
                </div>
            </div>
        </div>
    </ContentField>
</template>

<script>
import ContentField from '../../../components/ContentField.vue'
import {useStore} from 'vuex'
import {ref} from 'vue'
import router from '../../../router/index.js'
import $ from 'jquery'

export default {
    components: {
        ContentField
    },
    setup() {
        const store = useStore();
        let username = ref('');
        let password = ref('');
        let error_message = ref(''); 

        const goToForgetPassword = () => {
            router.push({ name: 'user_account_forget_password' });
        }

        const jwt_token = localStorage.getItem("jwt_token");
        if(jwt_token){
            store.commit("updateToken",jwt_token);
            store.dispatch("getinfo",{
                success(){
                    router.push({name: 'home'});
                    store.commit("updatePullingInfo", false);
                },
                error(){
                    store.commit("updatePullingInfo", false);
                }
            })
        }else{
            store.commit("updatePullingInfo", false);
        }

        const login = () =>{
            error_message.value = '';
            store.dispatch("login",{
                username: username.value,
                password: password.value,
                success(){
                    store.dispatch("getinfo",{
                        success(){
                            router.push({ name: 'home' });
                        }
                    })
                },
                error(){
                    error_message.value = "用户名或密码错误";
                }
            })
        }

        const acwing_login = () => {
            $.ajax({
                url: "https://app7811.acapp.acwing.com.cn/api/user/account/acwing/web/apply_code/",
                type: "GET",
                success: resp => {
                    if(resp.result === "success"){
                        window.location.replace(resp.apply_code_url);
                    }
                }  
            })

        }
        return {
            username,
            password,
            error_message,
            login,
            acwing_login,
            goToForgetPassword
        };
    }
}
</script>

<style scoped>
.error_message{
    color: red;
}

.forget-password-btn {
    background: none;        
    border: none;            
    padding: 0;             
    margin: 0;               
    font-size: inherit;      
    color: #0d6efd;        
    cursor: pointer;         
    text-decoration: none;   
}

.forget-password-btn:hover {
    color: #0a58ca;          
    text-decoration: none;   
    background: none;        
    border: none;            
}


</style>