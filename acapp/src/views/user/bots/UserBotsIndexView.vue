<template>
    <ContentField>
        <div class="game-table">
            <div>
                <span style="font-size: 130%;">我的Bots</span>
                <button type="button" style = "float: right;" @click = "show_add_modal_handler(true)">
                    创建Bot
                </button>

                <div class="game-modal" id="add-bot-btn" tabindex="-1" v-if="show_add_modal">
                    <div>
                        <h5 style = "margin:2px;">创建Bot</h5>
                    </div>
                    <div>
                        <div>
                            <label for="add-bot-title">名称</label>
                            <input style = "width:85%" v-model = "botadd.title" type="text" id="add-bot-title" placeholder="请输入Bot名称">
                        </div>
                        <div>
                            <label for="add-bot-description">简介</label>
                            <textarea style = "width:85%; margin-top: 10px;" v-model = "botadd.description" id="add-bot-description" rows="3" placeholder="请输入Bot简介"></textarea>
                        </div>
                        <div>
                            <label for="add-bot-code">代码</label>
                            <VAceEditor
                                v-model:value="botadd.content"
                                @init="editorInit"
                                lang="c_cpp"
                                theme="textmate"
                                style="height: 150px" />
                        </div>
                    </div>
                    <div>
                        <div class = "error-message">{{botadd.error_message}}</div>
                        <button @click="add_bot" type="button">创建</button>
                        <button type="button" @click = "show_add_modal_handler(false)">取消</button>
                    </div>
                </div>
                <table>
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
                                <button @click="show_update_modal_handler(bot.id, true)" type="button" style="margin-right: 10px;">
                                    修改
                                </button>

                                <div class="game-modal" :id="'update-bot-modal-' + bot.id" tabindex="-1" v-if="bot.show_update_modal"> 
                                    <div>
                                        <h5 style = "margin: 2px;">修改Bot</h5>
                                    </div>
                                    <div>
                                        <div>
                                            <label for="add-bot-title">名称</label>
                                            <input style = "width:85%" v-model = "bot.title" type="text" id="add-bot-title" placeholder="请输入Bot名称">
                                        </div>
                                        <div>
                                            <label for="add-bot-description" >简介</label>
                                            <textarea style = "width:85%; margin-top: 10px" v-model = "bot.description" id="add-bot-description" rows="3" placeholder="请输入Bot简介"></textarea>
                                        </div>
                                        <div>
                                            <label for="add-bot-code">代码</label>
                                            <VAceEditor v-model:value="bot.content" @init="editorInit" lang="c_cpp"  
                                                        theme="textmate" style="height: 150px" :options="{
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
                                    <div>
                                        <div class = "error-message">{{bot.error_message}}</div>
                                        <button @click="update_bot(bot)" type="button">保存修改</button>
                                        <button @click="show_update_modal_handler(bot.id, false)" type="button">取消</button>
                                    </div>
                                </div>

                                

                                <button @click="remove_bot(bot)" type="button">删除</button>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </ContentField>
</template>

<script>
import {ref, reactive} from "vue";
import {useStore} from "vuex";
import $ from "jquery";
import { VAceEditor } from 'vue3-ace-editor';
import ace from 'ace-builds';
import ContentField from "@/components/ContentField.vue";

import 'ace-builds/src-noconflict/mode-c_cpp';
import 'ace-builds/src-noconflict/mode-json';
import 'ace-builds/src-noconflict/theme-chrome';
import 'ace-builds/src-noconflict/ext-language_tools';

export default {
    components:{
        VAceEditor,
        ContentField        
    },
    setup(){
         ace.config.set(
            "basePath",
            "https://cdn.jsdelivr.net/npm/ace-builds@" +
            require("ace-builds").version +
            "/src-noconflict/")

        const botadd = reactive({
            title: '',
            description: '',
            code: '',
            error_message: ''
        });

        const store = useStore();
        let bots = ref([]);
        let show_add_modal = ref(false);


        const refresh_bots = () => {
            $.ajax({
                url: "https://app7811.acapp.acwing.com.cn/api/user/bot/getlist/",
                type: "GET",
                headers:{
                    Authorization: "Bearer " + store.state.user.token
                },
                success(resp){
                    for(const bot of resp){
                        bot.show_update_modal = false;
                    }
                    bots.value = resp;
                    console.log(resp); 
                },
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
                        show_add_modal.value = false;
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
                        refresh_bots();
                    }else{
                        botadd.error_message = resp.message;
                    }
                }
            })
        };

        const show_add_modal_handler = (is_show) =>{
            show_add_modal.value = is_show;
        }
        const show_update_modal_handler = (bot_id, is_show) =>{
            const new_bots = [];
            for(const bot of bots.value){
                if(bot.id === bot_id){
                    bot.show_update_modal = is_show;
                }
                new_bots.push(bot);
            }
            bots.value = new_bots;
        }
        return {
            bots,
            botadd,
            add_bot,
            remove_bot,
            update_bot,
            show_add_modal,
            show_add_modal_handler,
            show_update_modal_handler
        }
    }
}
</script>

<style scoped>
.error-message {
    color: red;
}
div.game-table{
    display:flex;
    justify-content: center;
    width: 100%;
    height: calc(100% - 5vh);
    padding-top: 5vh;
}

div.game-table table{
    background-color: rgba(255, 255, 255, 0.5);
    border-radius: 5px;
}
td{
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    width:12vw;
    max-width: 12vw;
    text-align: center;
}
th{
    text-align: center;
}
.game-modal{
    background-color: white;
    padding:10px;
    border-radius: 5px;
    position:absolute;
    width:40vw;
    height:60vh;
    left:0;
    right:0;
    top:0;
    bottom:0;
    margin:auto;
    text-align:left;
}
</style>