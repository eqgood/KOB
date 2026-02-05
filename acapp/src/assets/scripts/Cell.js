// 蛇的一个格子
export class Cell{
    constructor(r, c){
        this.r = r;
        this.c = c;

        // 由于canvas的坐标系以左上角为原点，x轴向右，y轴向下
        this.x = c + 0.5;  
        this.y = r + 0.5;  
    }
}