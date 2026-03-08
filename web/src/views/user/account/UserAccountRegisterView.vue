<template>
    <ContentField>
        <div class="row justify-content-center">
            <div class="col-3">
                <form @submit.prevent="register">
                    <div class="mb-3">
                        <label for="username" class="form-label">用户名</label>
                        <input  v-model="username" type="text" class="form-control" id="username" placeholder="请输入用户名">
                    </div>
                    <div class="mb-3">
                        <label for="password" class="form-label">密码</label>
                        <input v-model="password" type="password" class="form-control" id="password" placeholder="请输入密码">
                    </div>
                    <div class="mb-3">
                        <input v-model="confirmedPassword" type="password" class="form-control" id="confirmedPassword" placeholder="请再次输入密码">
                    </div>

                    <div class="mb-3">
                        <label for="verify--info" class="form-label">验证信息</label>
                        <input v-model="email" type="text" class="form-control" id="verify--info" placeholder="请输入绑定邮箱">
                    </div>
                     <div class="mb-3">
                        <div class="input-group">
                            <input v-model="verifyCode" type="text" class="form-control" id="verify-code" placeholder="请输入验证码">
                            <button type="button" class="btn btn-primary" @click="sendVerifyCode" :disabled="countDown > 0">
                                {{ countDown > 0 ? `${countDown}s后重新发送` : '发送验证码' }}
                            </button>
                        </div>
                    </div>

                    <div v-if="error_message" class="error_message">{{ error_message }}</div>
                    <button type="submit" class="btn btn-primary w-100">注册</button>
                </form>
            </div>
        </div>
    </ContentField>
</template>

<script>
import ContentField from '../../../components/ContentField.vue'
import {ref} from 'vue'
import router from '../../../router/index.js'
import $ from 'jquery'

export default {
    components: {
        ContentField
    },
    setup(){
        let username = ref("");
        let password = ref("");
        let confirmedPassword = ref("");
        const email = ref('');
        const verifyCode = ref('');
        let error_message = ref("");

        const register = () =>{
            $.ajax({
                url : "https://app7811.acapp.acwing.com.cn/api/user/account/register/",
                type: "post",
                data:{
                    username: username.value,
                    password: password.value,
                    confirmedPassword: confirmedPassword.value,
                },
                success(resp){
                    if(resp.message === "success"){
                        router.push({ name: 'user_account_login' });
                    }else{
                        error_message.value = resp.message;
                    }
                },
            })
        }

        return{
            username,
            password,
            confirmedPassword,
            error_message,
            email,
            verifyCode,
            register
        }
    }
}
</script>

<style scoped>
.error_message{
    color:red;
}
</style>