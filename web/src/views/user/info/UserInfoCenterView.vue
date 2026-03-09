<template>
    <div class="container">
        <div class="row">
            <div class="col-3">
                <div class="card" style="margin-top: 20px;">
                    <div class="card-body">
                        <img :src="$store.state.user.photo" alt="" style="width: 100%;">

                        <input
                            ref="fileInput"
                            type="file"
                            accept="image/*"
                            class="d-none"
                            @change="handlePhotoChange"
                        />

                        <div class="d-flex justify-content-center mt-4" id=" photo-change">
                            <button type="button" class="btn btn-outline-dark" @click="triggerFileInput">更改头像</button>
                            <div class="text-danger mt-2 ms-2">{{ photoMsg }}</div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-9"> 
                <div class="card" style="margin-top: 20px;"> 
                    <div class="card-body">
                        <h3 class="pb-2 border-bottom border-secondary-subtle" style = "margin-top: 15px;">个人信息</h3>
                        <div class="mb-3" style = "margin-top: 15px;">
                            <label class="form-label">用户名:</label>
                            <input v-model="current_info.username" type="text" class="form-control" >
                        </div>
                        <div class="mb-3" style = "margin-top: 15px;">
                            <label class="form-label">邮箱</label>
                            <input v-model="current_info.email" type="text" class="form-control" >
                        </div>
                        <div class="mb-3" style = "margin-top: 15px;">
                            <label class="form-label">个人简介:</label>
                            <textarea v-model="current_info.description" class="form-control" style = "height: 200px;"></textarea>
                        </div>

                        <div class="d-flex flex-column align-items-center" style="margin-top: 20px;">
                            <div class = "error-message">{{ current_info.message }}</div>
                            
                            <div class="modal fade" id="confirmed_success" aria-hidden="true" tabindex="-1">
                                <div class="modal-dialog modal-dialog-centered">
                                    <div class="modal-content">
                                        <div class="modal-header">
                                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                        </div>
                                        <div class="modal-body" style="text-align: center;font-size: 20px;font-weight: 300;">
                                            信息更新成功
                                        </div>
                                        <div class="modal-footer">
                                            <button class="btn btn-primary" data-bs-dismiss="modal">确定</button>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <button type="button" class="btn btn-primary" @click="click_update(current_info)">
                                更新信息
                            </button>
                        </div>
                    </div> 
                </div>

            </div>
        </div>
    </div>
</template>

<script> 
import $ from 'jquery';
import {ref, reactive} from 'vue';
import {useStore} from 'vuex';
import { Modal } from 'bootstrap';

export default{
    setup(){
        const store = useStore();
        store.commit("updateIsUpdateInfo", "");
        const current_info = reactive({
            photo: '',
            username: '',
            description: '',
            message: '',
            email:'',
        });

        const fileInput = ref(null);
        const photoMsg = ref('');

        const triggerFileInput = () => {
            photoMsg.value = '';
            fileInput.value?.click();
        };

        const handlePhotoChange = (e) => {
            photoMsg.value = '';
            const file = e.target.files?.[0];
            if (!file) return;

            // 校验
            if (!file.type.startsWith('image/')) {
                photoMsg.value = '请选择图片文件';
                return;
            }
            if (file.size > 2 * 1024 * 1024) {
                photoMsg.value = '图片不能超过2MB';
                return;
            }

            const form = new FormData();
            form.append('photo', file);

            $.ajax({
                url: "https://app7811.acapp.acwing.com.cn/api/user/account/updatephoto/",
                type: "POST",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                data: form,
                processData: false,
                contentType: false,
                success(resp) {
                    if (resp.message === "success") {
                        store.commit("updateUser", {
                            id: store.state.user.id,
                            photo: resp.photo,
                            username: store.state.user.username,
                            is_login: store.state.user.is_login,
                        });
                    } else {
                        photoMsg.value = resp.message;
                    }
                    e.target.value = ''; // 允许重复选择同一文件
                },
                error(xhr) {
                    photoMsg.value = `上传失败(${xhr.status})`;
                    e.target.value = '';
                }
            });
        };

        const refresh_info = () => {
            $.ajax({
                url: "https://app7811.acapp.acwing.com.cn/api/user/account/info/",
                type: "GET",
                headers:{
                    Authorization: "Bearer " + store.state.user.token
                },
                success(resp){
                    current_info.photo = resp.photo;
                    current_info.username = resp.username;
                    current_info.description = resp.description;
                    current_info.email = resp.email;
                },
                error(err){
                    console.log(err);
                }
            })
        }
        refresh_info();
        const click_update = () => {
            $.ajax({
                url: "https://app7811.acapp.acwing.com.cn/api/user/account/updateinfo/",
                type: "POST",
                headers: {
                Authorization: "Bearer " + store.state.user.token,
                },
                data: {
                username: current_info.username,
                description: current_info.description,
                email: current_info.email,
                },
                success(resp) {
                if (resp.message === "success") {
                    current_info.message = ""
                    // 成功后再弹窗
                    const el = document.getElementById("confirmed_success")
                    Modal.getOrCreateInstance(el).show()
                } else {
                    // 失败只显示红字，不要动 modal
                    current_info.message = resp.message
                }
                },
                error(resp) {
                    current_info.message = resp.message
                }
            })
        }
        return{
            current_info,
            click_update,
            fileInput,
            photoMsg,
            triggerFileInput,
            handlePhotoChange,
        }   
    }
}
</script>


<style scoped>
.error-message{
    color: red;
    min-height: 22px;
}
</style>