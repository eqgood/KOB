<template>
    <ContentField>
        <div class="row justify-content-center">
            <div class="col-3">
                <form @submit.prevent="password">
                    <div class="mb-3">
                        <label for="verify-info" class="form-label">验证信息</label>
                        <input v-model="email" type="text" class="form-control" id="verify-info" placeholder="请输入绑定邮箱">
                    </div>
                     <div class="mb-3">
                        <div class="input-group">
                            <input v-model="verifyCode" type="text" class="form-control" id="verify-code" placeholder="请输入验证码">
                            <button type="button" class="btn btn-primary" @click="sendVerifyCode" :disabled="countDown > 0">
                                {{ countDown > 0 ? `${countDown}s后重新发送` : '发送验证码' }}
                            </button>
                        </div>
                    </div>
                    <div class="mb-3">
                        <label for="password" class="form-label">密码</label>
                        <input v-model="password" type="password" class="form-control" id="password" placeholder="请输入密码">
                    </div>
                    <div class="mb-3">
                        <input v-model="confirmedPassword" type="password" class="form-control" id="confirmedPassword" placeholder="请再次输入密码">
                    </div>

                    <div v-show="successShow" class="modal fade" id="update_success" aria-hidden="true" tabindex="-1">
                        <div class="modal-dialog modal-dialog-centered">
                            <div class="modal-content">
                            <div class="modal-header">
                                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                            </div>
                            <div class="modal-body" style="text-align: center;font-size: 20px;font-weight: 300;">
                                密码修改成功
                            </div>
                            <div class="modal-footer">
                                <button class="btn btn-primary" data-bs-dismiss="modal" @click="click_confirm">确定</button>
                            </div>
                            </div>
                        </div>
                    </div>
                    <div v-if="error_message" class="error_message">{{ error_message }}</div>
                    <button type="submit" class="btn btn-primary w-100" @click="update_password">确认修改</button>
                </form>
            </div>
        </div>
    </ContentField>
</template>

<script> 
import ContentField from '../../../components/ContentField.vue'
import {ref} from 'vue'
import $ from 'jquery'
import { Modal } from 'bootstrap'

export default{
    components: {
        ContentField
    },

    setup(){
        const email = ref('');
        const verifyCode = ref('');
        const password = ref('');
        const confirmedPassword = ref('');
        const error_message = ref('');
        const countDown = ref(0);
        const successShow = ref(false);

        // 邮箱格式校验工具函数
        const validateEmail = (emailStr) => {
            const emailReg = /^[a-zA-Z0-9._%-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/
            return emailReg.test(emailStr)
        }
        const sendVerifyCode = () => { 
            error_message.value = ''

            if (!email.value.trim()) {
                error_message.value = '请输入绑定的邮箱';
                return;
            }
            if (!validateEmail(email.value)) {
                error_message.value = '请输入正确的邮箱格式';
                return;
            }

            $.ajax({
                url: 'https://app7811.acapp.acwing.com.cn/api/user/account/send_verifycode/',
                type: 'POST',
                data: {
                    email: email.value,
                    scene: 'forget_password'
                },
                success(resp) {
                    if (resp.message === 'success') {
                        error_message.value = '验证码发送成功，请查收邮件';
                        // 启动60秒倒计时
                        countDown.value = 60;
                        let timer = setInterval(() => {
                            countDown.value--;
                            // 倒计时结束清除定时器
                            if (countDown.value <= 0) {
                                clearInterval(timer);
                                timer = null;
                            }
                        }, 1000);
                    } else {
                        error_message.value = resp.message;
                    }
                },
                error() {
                    error_message.value = '网络异常，请稍后重试';
                }
            })
        }

        // 提交修改密码函数
        const update_password = () => {
            error_message.value = '';
            // 前置校验
            if (!email.value.trim()) {
                error_message.value = '请输入绑定的邮箱';
                return;
            }
            if (!verifyCode.value.trim()) {
                error_message.value = '请输入验证码';
                return;
            }
            if (!password.value.trim()) {
                error_message.value = '请输入新密码';
                return;
            }
            if (password.value !== confirmedPassword.value) {
                error_message.value = '两次输入的密码不一致';
                return;
            }
            $.ajax({
                url: 'https://app7811.acapp.acwing.com.cn/api/user/account/forget_password/update_password/',
                type: 'post',
                data: {
                    email: email.value,
                    verifyCode: verifyCode.value,
                    Password: password.value,
                    confirmedPassword: confirmedPassword.value
                },
                success(resp) {
                    if (resp.message === 'success') {
                        successShow.value = true;
                        setTimeout(() => {                         
                            const el = document.getElementById("update_success");
                            Modal.getOrCreateInstance(el).show();
                        }, 0);
                    } else {
                        error_message.value = resp.message;
                    }
                },
                error() {
                    error_message.value = '网络异常，修改失败';
                }
            })
        }

        const click_confirm = () =>{
            successShow.value = false;
            setTimeout(() => {
                window.location.href = '/user/account/login/';
            }, 100);
        }

        return {
            email,
            verifyCode,
            password,
            confirmedPassword,
            error_message,
            countDown,
            sendVerifyCode,
            update_password,
            successShow,
            click_confirm
        }   
    }
    
}

</script>

<style scoped>
.error_message {
    color: red;
}
</style>