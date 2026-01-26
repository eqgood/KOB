import { AcGameObject } from "./AcGameObject";
import { Snake } from "./Snake";
import { Wall } from "./Wall";

export class GameMap extends AcGameObject{
    constructor(ctx, parent, store){
        super();

        this.ctx = ctx;
        this.parent = parent;
        this.store = store;
        this.L = 0; //一个单位（格子）的长度
        
        //行数和列数分别为偶数和奇数，防止两蛇同时进入一个格子
        this.rows = 13; //行数
        this.cols = 14; //列数

        this.inner_walls_count = 20; //内部墙的数量

        this.walls = []; //存放地图上所有的墙

        this.snakes = [
            new Snake({id: 0, color:"#5078D1", r: this.rows - 2, c: 1}, this),
            new Snake({id: 1, color:"#EE5446", r: 1, c: this.cols - 2}, this),
        ]; //存放两条蛇
    }
    

    create_walls(){
        const g = this.store.state.pk.gamemap;

        for(let r = 0; r < this.rows; r ++){
            for(let c = 0; c < this.cols; c ++){
                if(g[r][c]){
                    this.walls.push(new Wall(r, c, this));
                }
            }
        }
    }

    // 通过键盘获取下一步方向
    add_listening_events(){
        this.ctx.canvas.focus(); // 让canvas获取焦点，从而监听键盘事件
        const [snake0, snake1] = this.snakes;
        this.ctx.canvas.addEventListener("keydown", e =>{
            if(e.key === 'w') snake0.set_direction(0);
            else if (e.key === 'd') snake0.set_direction(1);
            else if (e.key === 's') snake0.set_direction(2);
            else if (e.key === 'a') snake0.set_direction(3);
            else if (e.key === 'ArrowUp') snake1.set_direction(0);
            else if (e.key === 'ArrowRight') snake1.set_direction(1);
            else if (e.key === 'ArrowDown') snake1.set_direction(2);
            else if (e.key === 'ArrowLeft') snake1.set_direction(3);
        })
    }

    start(){
        this.create_walls();
        this.add_listening_events();
    }

    update_size(){
        this.L = parseInt(Math.min(this.parent.clientWidth / this.cols, this.parent.clientHeight / this.rows))
        this.ctx.canvas.width = this.L * this.cols;
        this.ctx.canvas.height = this.L * this.rows;
    }

    // 检查两条蛇是否都准备好下一回合了
    check_ready(){ 
        for(const snake of this.snakes){
            if(snake.status !== "idle") return false;
            if(snake.direction === -1) return false;
        }
        return true;
    }

    // 让两条蛇进入下一回合
    next_step(){
        for(const snake of this.snakes){
            snake.next_step();
        }
    }
    
    // 检测某个格子是否合法（没有撞墙，没有撞蛇的身体）
    check_valid(cell){
        for(const wall of this.walls){
            if(wall.r === cell.r && wall.c ===cell.c) 
                return false;
        }
        for(const snake of this.snakes){
            let k = snake.cells.length;
            if(!snake.check_tail_increasing()){ // 当蛇尾前进时，蛇尾不要判断是否发生碰撞
                k --;
            }
            for(let i = 0; i < k;i ++){
                if (snake.cells[i].r === cell.r && snake.cells[i].c === cell.c) 
                    return false;
            }
        }
        return true;
    }

    update(){
        this.update_size();
        if(this.check_ready()){
            this.next_step();
        }
        this.render();
    }
    //绘制地图
    render(){ 
        const color_even = "#AAD751";
        const color_odd = "#A2D149";
        for(let r = 0;r < this.rows; r ++){
            for(let c = 0; c < this.cols; c ++){
                if((r + c) % 2 === 0){
                    this.ctx.fillStyle = color_even;
                }else{
                    this.ctx.fillStyle = color_odd;
                }
                this.ctx.fillRect(c * this.L, r * this.L, this.L, this.L);
            }
        }
    }
}