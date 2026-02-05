import { AcGameObject } from "./AcGameObject.js";
import { Cell } from "./Cell.js";

export class Snake extends AcGameObject{
    constructor(info, gamemap){
        super();

        this.id = info.id;
        this.color = info.color;
        this.gamemap = gamemap; // 目的时调用参数

        this.cells = [new Cell(info.r, info.c)]; // 存放蛇的身体，cells[0]存放蛇头
        this.next_cell = null; // 下一步的目标位置
        
        this.speed = 5; // 蛇每秒钟走多少格子
        this.direction = -1; // -1表示没有指令，0表示上，1表示右，2表示下，3表示左
        this.status = "idle"; // idle表示静止(表示还没有指令)，move表示正在移动，die表示死亡
        
        this.dr = [-1, 0, 1, 0]; // 四个方向行的偏移量
        this.dc = [0, 1, 0, -1]; // 四个方向列的偏移量

        this.step = 0; //表示回合数
        this.eps = 1e-2; // 允许的误差(两点坐标值差)

        this.eye_direction = 0; // 眼睛方向
        if(this.id === 1) this.eye_direction = 2; // 默认第二条蛇的眼睛方向向下
        
        this.eye_dx = [[-1, 1], [1, 1], [1, -1], [-1, -1]]; // 两只眼睛x偏移方向
        this.eye_dy = [[-1, -1], [-1, 1], [1, 1], [1, -1]]; // 两只眼睛y偏移方向
    }

    // 这里是一个接口，用来设置蛇的方向（可以更改，不一定只用键盘来触发，还可以通过后端数据来触发）
    set_direction(d){
        this.direction = d;
    }
    // 判断当前回合蛇的长度是否增加
    check_tail_increasing(){
        if(this.step <= 10) return true;
        if(this.step % 3 === 1) return true;
        return false;
    }

    next_step(){
        const d = this.direction;
        this.next_cell = new Cell(this.cells[0].r + this.dr[d],this.cells[0].c + this.dc[d]);
        this.eye_direction = d; // 更新眼睛方向
        this.direction = -1; // 清空操作
        this.status = "move"; //把状态变为move
        this.step ++; 

        // 每个元素向后移动一位且第一位不变，注意这里不要直接赋值
        const k = this.cells.length;
        for(let i = k; i > 0; i --){
            this.cells[i] = JSON.parse(JSON.stringify(this.cells[i - 1])); 
        }
    }

    update_move(){
        const dx = this.next_cell.x - this.cells[0].x;
        const dy =this.next_cell.y - this.cells[0].y;
        const distance = Math.sqrt(dx * dx + dy * dy);
        const move_distance = this.speed * this.timedelta / 1000; // 每两帧之间走的距离

        if(distance < this.eps) { // 到达目标位置
            this.cells[0] = this.next_cell;
            this.next_cell = null;
            this.status = "idle"; // 走完了，停下来

            if(!this.check_tail_increasing()){
                this.cells.pop(); // 蛇尾删除
            }
        }else {
            this.cells[0].x += move_distance * dx / distance;
            this.cells[0].y += move_distance * dy / distance;
        }

        // 蛇尾的处理
        if(!this.check_tail_increasing()){
            const k = this.cells.length; // 这里的k是更新后的长度
            const tail = this.cells[k - 1];
            const tail_target = this.cells[k - 2];
            const tail_dx = tail_target.x - tail.x;
            const tail_dy = tail_target.y - tail.y;
            const tail_distance = Math.sqrt(tail_dx * tail_dx + tail_dy * tail_dy);
            tail.x += move_distance * tail_dx / tail_distance;
            tail.y += move_distance * tail_dy / tail_distance;
        }
    }
    
    update(){
        if(this.status === "move"){
            this.update_move();
        }
        this.render();
    }

    render(){
        const L = this.gamemap.L;
        const ctx = this.gamemap.ctx;

        ctx.fillStyle = this.color;

        if(this.status === "die"){
            ctx.fillStyle = "white";
        }

        for (const cell of this.cells){
            ctx.beginPath();
            ctx.arc(cell.x * L, cell.y * L, L / 2 * 0.8, 0, Math.PI * 2);
            ctx.fill();
        }

        for(let i = 1; i < this.cells.length;i ++){
            const a = this.cells[i - 1];
            const b = this.cells[i];
            if(Math.abs(a.x - b.x) < this.eps && Math.abs(a.y - b.y) < this.eps)
                continue;
            if(Math.abs(a.x - b.x) < this.eps){ // 说明是上下移动
                ctx.fillRect((a.x - 0.4) * L, Math.min(a.y, b.y) * L, L * 0.8, Math.abs(a.y - b.y) * L);
            } else{
                ctx.fillRect(Math.min(a.x, b.x) * L, (a.y - 0.4) * L, Math.abs(a.x - b.x) * L, L * 0.8);
            }
        }
        ctx.fillStyle = "black";
        for(let i = 0; i < 2; i ++){
            const eye_x = this.cells[0].x * L + this.eye_dx[this.eye_direction][i] * L * 0.15;
            const eye_y = this.cells[0].y * L + this.eye_dy[this.eye_direction][i] * L * 0.15;
            ctx.beginPath();
            ctx.arc(eye_x, eye_y, L * 0.06, 0, Math.PI * 2);
            ctx.fill();
        }
    }
}