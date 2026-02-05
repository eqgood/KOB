const AC_GAME_OBJECTS = [];

//每一个在游戏中需要被更新的对象都应该继承这个类，并且把对象添加到AC_GAME_OBJECTS数组中
export class AcGameObject {
    constructor(){
        AC_GAME_OBJECTS.push(this);
        this.timedelta = 0;  //当前距离上一帧的时间间隔
        this.has_called_start = false;  //是否执行过start函数
    }
    start(){   //只执行一次

    }
    update(){  //每一帧执行一次，除了第一帧

    }
    on_destroy(){   //在删除前执行

    }  
    destroy(){
        this.on_destroy();
        for(let i in AC_GAME_OBJECTS){
            const obj = AC_GAME_OBJECTS[i];
            if(obj === this){
                AC_GAME_OBJECTS.splice(i);
                break;
            }
        }
    }

}

let last_timestamp;  //上一次执行的时刻
const step = timestamp => { //timestamp为浏览器当前的时刻
    for(let obj of AC_GAME_OBJECTS){
        if(!obj.has_called_start){
            obj.start();
            obj.has_called_start = true;
        }else{
            obj.timedelta = timestamp - last_timestamp;
            obj.update();
        }
    }
    last_timestamp = timestamp;
    requestAnimationFrame(step);
}
requestAnimationFrame(step);  //该函数会在浏览器下一帧刷新之前调用step函数