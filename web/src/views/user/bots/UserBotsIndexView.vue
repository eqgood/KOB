<template>
    <div class="container">
        <div class="row">
            <div class="col-3">
                <div class="card" style="margin-top: 20px;">
                    <div class="card-body">
                        <img :src="$store.state.user.photo" alt="" style="width: 100%;">
                    </div>
                </div>
            </div>
            <div class="col-9">
                <div class="card" style="margin-top: 20px;">
                    <div class="card-header">
                        <span style="font-size: 130%;">我的Bots</span>
                        <button type="button" class="btn btn-primary float-end" data-bs-toggle="modal" data-bs-target="#add-bot-btn">
                            创建Bot
                        </button>

                        <div class="modal fade" id="add-bot-btn" tabindex="-1">
                            <div class="modal-dialog modal-xl">
                                <div class="modal-content">
                                <div class="modal-header">
                                    <h1 class="modal-title fs-5" id="exampleModalLabel">创建Bot</h1>
                                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                </div>
                                <div class="modal-body">
                                    <div class="mb-3">
                                        <label for="add-bot-title" class="form-label">名称</label>
                                        <input v-model = "botadd.title" type="text" class="form-control" id="add-bot-title" placeholder="请输入Bot名称">
                                    </div>
                                    <div class="mb-3">
                                        <label for="add-bot-description" class="form-label">简介</label>
                                        <textarea v-model = "botadd.description" class="form-control" id="add-bot-description" rows="3" placeholder="请输入Bot简介"></textarea>
                                    </div>
                                    <div class="mb-3">
                                        <label for="add-bot-code" class="form-label">代码</label>
                                        <VAceEditor
                                            v-model:value="botadd.content"
                                            @init="editorInit"
                                            lang="c_cpp"
                                            theme="textmate"
                                            style="height: 300px" />
                                    </div>
                                </div>
                                <div class="modal-footer">
                                    <div class = "error-message">{{botadd.error_message}}</div>
                                    <button @click="add_bot" type="button" class="btn btn-primary">创建</button>
                                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal" @mousedown="(e) => e.currentTarget.blur()">取消</button>
                                </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="card-body">
                        <table class="table table-hover">
                            <thead>
                                <tr>
                                    <th>名称</th>
                                    <th>创建时间</th>
                                    <th>操作</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr v-for="bot in bots" :key="bot.id">
                                    <td>{{ bot.title }}</td>
                                    <td>{{ bot.createTime }}</td>
                                    <td>
                                        <button  type="button" class="btn btn-secondary" style="margin-right: 10px;" data-bs-toggle="modal" :data-bs-target="'#update-bot-modal-' + bot.id">
                                            修改
                                        </button>

                                        <div class="modal fade" :id="'update-bot-modal-' + bot.id" tabindex="-1">
                                            <div class="modal-dialog modal-xl">
                                                <div class="modal-content">
                                                <div class="modal-header">
                                                    <h1 class="modal-title fs-5" id="exampleModalLabel">修改Bot</h1>
                                                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                                </div>
                                                <div class="modal-body">
                                                    <div class="mb-3">
                                                        <label for="add-bot-title" class="form-label">名称</label>
                                                        <input v-model = "bot.title" type="text" class="form-control" id="add-bot-title" placeholder="请输入Bot名称">
                                                    </div>
                                                    <div class="mb-3">
                                                        <label for="add-bot-description" class="form-label">简介</label>
                                                        <textarea v-model = "bot.description" class="form-control" id="add-bot-description" rows="3" placeholder="请输入Bot简介"></textarea>
                                                    </div>
                                                    <div class="mb-3">
                                                        <label for="add-bot-code" class="form-label">代码</label>
                                                        <VAceEditor v-model:value="bot.content" class = "form-control" @init="editorInit" lang="c_cpp"  
                                                                    theme="textmate" style="height: 300px" :options="{
                                                                                                                enableBasicAutocompletion: true, //启用基本自动完成
                                                                                                                enableSnippets: true, // 启用代码段
                                                                                                                enableLiveAutocompletion: true, // 启用实时自动完成
                                                                                                                fontSize: 15, //设置字号
                                                                                                                tabSize: 4, // 标签大小
                                                                                                                showPrintMargin: false, //去除编辑器里的竖线
                                                                                                                highlightActiveLine: true,
                                                                                                            }" />

                                                    </div>
                                                </div>
                                                <div class="modal-footer">
                                                    <div class = "error-message">{{bot.error_message}}</div>
                                                    <button @click="update_bot(bot)" type="button" class="btn btn-primary">保存修改</button>
                                                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal" @mousedown="(e) => e.currentTarget.blur()">取消</button>
                                                </div>
                                                </div>
                                            </div>
                                        </div>

                                        

                                        <button @click="remove_bot(bot)" type="button" class="btn btn-danger">删除</button>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import {ref, reactive} from "vue";
import {useStore} from "vuex";
import $ from "jquery";
import { Modal } from "bootstrap/dist/js/bootstrap.bundle.min";
import { VAceEditor } from 'vue3-ace-editor';
import ace from 'ace-builds';

import 'ace-builds/src-noconflict/mode-c_cpp';
import 'ace-builds/src-noconflict/mode-json';
import 'ace-builds/src-noconflict/theme-chrome';
import 'ace-builds/src-noconflict/ext-language_tools';

export default {
    components:{
        VAceEditor        
    },
    setup(){
         ace.config.set(
            "basePath",
            "https://cdn.jsdelivr.net/npm/ace-builds@" +
            require("ace-builds").version +
            "/src-noconflict/")

        const editorInit = () => {};

        const botadd = reactive({
            title: '',
            description: '',
            content: '',
            error_message: ''
        });

        const store = useStore();
        let bots = ref([]);


        const refresh_bots = () => {
            $.ajax({
                url: "https://app7811.acapp.acwing.com.cn/api/user/bot/getlist/",
                type: "GET",
                headers:{
                    Authorization: "Bearer " + store.state.user.token
                },
                success(resp){
                    bots.value = resp;
                },
                error(err){
                    console.log(err);
                }
            })
        }

        refresh_bots();

        const add_bot =() =>{
            botadd.error_message = '';
            $.ajax({
                url: "https://app7811.acapp.acwing.com.cn/api/user/bot/add/",
                type: "POST",
                headers:{
                    Authorization: "Bearer " + store.state.user.token
                },
                data:{
                    title: botadd.title,
                    description: botadd.description,
                    content:botadd.content
                },
                success(resp){
                    if(resp.message === "success"){
                        botadd.title = '';
                        botadd.description = '';
                        botadd.content = '';
                        document.activeElement?.blur();
                        const el = document.getElementById("add-bot-btn");
                        Modal.getOrCreateInstance(el).hide();
                        refresh_bots();
                    }else{
                        botadd.error_message = resp.message;
                    }
                }
            })
        };

        const remove_bot = (bot) => {
            $.ajax({
                url: "https://app7811.acapp.acwing.com.cn/api/user/bot/remove/",
                type: "POST",
                headers:{
                    Authorization: "Bearer " + store.state.user.token
                },
                data:{
                    bot_id: bot.id
                },
                success(resp){
                    if(resp.message === "success"){
                      refresh_bots();  
                    }
                }
            })
        };

        const update_bot =(bot) =>{
            botadd.error_message = '';
            $.ajax({
                url: "https://app7811.acapp.acwing.com.cn/api/user/bot/update/",
                type: "POST",
                headers:{
                    Authorization: "Bearer " + store.state.user.token
                },
                data:{
                    bot_id: bot.id,
                    title: bot.title,
                    description: bot.description,
                    content: bot.content
                },
                success(resp){
                    if(resp.message === "success"){
                        document.activeElement?.blur();
                        const el = document.getElementById(`update-bot-modal-${bot.id}`);
                        Modal.getOrCreateInstance(el).hide();
                        refresh_bots();
                    }else{
                        botadd.error_message = resp.message;
                        bot.error_message = resp.message;
                    }
                }
            })
        };
        return {
            bots,
            botadd,
            add_bot,
            remove_bot,
            update_bot,
            editorInit
        }
    }
}
</script>

<style scoped>
.error-message {
    color: red;
}
</style>